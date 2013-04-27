package thirdparty.eu.schudt.javafx.controls.calendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;

/**
 * @author Christian Schudt
 */
public class DatePicker extends HBox {


    private static final String CSS_DATE_PICKER_VALID = "datepicker-valid";
    private static final String CSS_DATE_PICKER_INVALID = "datepicker-invalid";


    /**
     * Initializes the date picker with the default locale.
     */
    public DatePicker() {
        this(Locale.getDefault());
    }

    private Timer timer;

    /**
     * Initializes the date picker with the given locale.
     *
     * @param locale The locale.
     */
    public DatePicker(Locale locale) {
        calendarView = new CalendarView(locale);

        textField = new TextField();
        this.locale.set(locale);

        calendarView.setEffect(new DropShadow());

        // Use the same locale.
        calendarView.localeProperty().bind(localeProperty());

        // Bind the current date of the calendar view with the selected date, so that the calendar shows up with the same month as in the text field.
        calendarView.currentDateProperty().bind(selectedDateProperty());

        // When the user selects a date in the calendar view, hide it.
        calendarView.selectedDateProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                selectedDate.set(calendarView.selectedDateProperty().get());
                hidePopup();
            }
        });

        // Let the prompt text property listen to locale or date format changes.
        textField.promptTextProperty().bind(new StringBinding() {
            {
                super.bind(localeProperty(), promptTextProperty(), dateFormatProperty());
            }

            @Override
            protected String computeValue() {
                // First check, if there is a custom prompt text.
                if (promptTextProperty().get() != null) {
                    return promptTextProperty().get();
                }

                // If not, use the the date format's pattern.
                DateFormat dateFormat = getActualDateFormat();
                if (dateFormat instanceof SimpleDateFormat) {
                    return ((SimpleDateFormat) dateFormat).toPattern();
                }

                return "";
            }
        });


        // Change the CSS styles, when this control becomes invalid.
        invalid.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (invalid.get()) {
                    textField.getStyleClass().add(CSS_DATE_PICKER_INVALID);
                    textField.getStyleClass().remove(CSS_DATE_PICKER_VALID);
                } else {
                    textField.getStyleClass().remove(CSS_DATE_PICKER_INVALID);
                    textField.getStyleClass().add(CSS_DATE_PICKER_VALID);
                }
            }
        });

        // When the text field no longer has the focus, try to parse the date.
        textField.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!textField.focusedProperty().get()) {
                    if (!textField.getText().isEmpty()) {
                        tryParse(true);
                    }
                } 
//                else {
//                    showPopup();
//                }
            }
        });

        // Listen to user input.
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s1) {
                // Only evaluate the input, it it wasn't set programmatically.
                if (textSetProgrammatically) {
                    return;
                }

                if (timer != null) {
                    timer.cancel();
                }

                // If the user clears the text field, set the date to null and the field to valid.
                if (s1.isEmpty()) {
                    selectedDate.set(null);
                    invalid.set(false);
                } else {
                    // Start a timer, so that the user input is not evaluated immediately, but after a second.
                    // This way, input like 01/01/1 is not immediately parsed as 01/01/01.
                    // The user gets one second time, to complete his date, maybe his intention was to enter 01/01/12.
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    tryParse(false);
                                }
                            });
                        }
                    }, 1000);
                }
            }
        });

        selectedDateProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateTextField();
                invalid.set(false);
            }
        });

        localeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateTextField();
            }
        });

//        textField.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent keyEvent) {
//                if (keyEvent.getCode() == KeyCode.DOWN) {
//                    showPopup();
//                }
//            }
//        });

        final Button button = new Button(">");
        button.getStyleClass().add("calendar-popup-button");
        button.setFocusTraversable(false);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showPopup();
            }
        });
        
        getChildren().add(textField);
        getChildren().add(button);
    }

    private void hidePopup() {
        if (popup != null) {
            popup.hide();
        }
    }

    /**
     * Tries to parse the text field for a valid date.
     *
     * @param setDateToNullOnException True, if the date should be set to null, when a {@link ParseException} occurs.
     *                                 This is the case, when the text field loses focus.
     */
    private void tryParse(boolean setDateToNullOnException) {
        if (timer != null) {
            timer.cancel();
        }
        try {
            // Double parse the date here, since e.g. 01.01.1 is parsed as year 1, and then formatted as 01.01.01 and then parsed as year 2001.
            // This might lead to an undesired date.
            DateFormat dateFormat = getActualDateFormat();
            Date parsedDate = dateFormat.parse(textField.getText());
            parsedDate = dateFormat.parse(dateFormat.format(parsedDate));
            if (selectedDate.get() == null || selectedDate.get() != null && parsedDate.getTime() != selectedDate.get().getTime()) {
                selectedDate.set(parsedDate);
            }
            invalid.set(false);
            updateTextField();
        } catch (ParseException e) {
            invalid.set(true);
            if (setDateToNullOnException) {
                selectedDate.set(null);
            }
        }

    }

    private boolean textSetProgrammatically;

    /**
     * Updates the text field.
     */
    private void updateTextField() {
        // Mark the we update the text field (and not the user), so that it can be ignored, by textField.textProperty()
        textSetProgrammatically = true;
        if (selectedDateProperty().get() != null) {
            String date = getActualDateFormat().format(selectedDateProperty().get());
            if (!textField.getText().equals(date)) {
                textField.setText(date);
            }
        } else {
            textField.setText("");
        }
        textSetProgrammatically = false;
    }

    /**
     * Gets the actual date format. If {@link #dateFormatProperty()} is set, take it, otherwise get a default format for the current locale.
     *
     * @return The date format.
     */
    private DateFormat getActualDateFormat() {
        if (dateFormat.get() != null) {
            return dateFormat.get();
        }

        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale.get());
        format.setCalendar(calendarView.getCalendar());
        format.setLenient(false);

        return format;
    }

    private CalendarView calendarView;

    /**
     * Use this to set further properties of the calendar.
     *
     * @return The calendar view.
     */
    public CalendarView getCalendarView() {
        return calendarView;
    }

    private TextField textField;


    private BooleanProperty invalid = new SimpleBooleanProperty();

    /**
     * States whether the user input is invalid (is no valid date).
     *
     * @return The property.
     */
    public ReadOnlyBooleanProperty invalidProperty() {
        return invalid;
    }


    /**
     * The locale.
     *
     * @return The property.
     */
    public ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    private ObjectProperty<Locale> locale = new SimpleObjectProperty<Locale>();

    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    public Locale getLocale() {
        return locale.get();
    }


    /**
     * The selected date.
     *
     * @return The property.
     */
    public ObjectProperty<Date> selectedDateProperty() {
        return selectedDate;
    }

    private ObjectProperty<Date> selectedDate = new SimpleObjectProperty<Date>();

    public void setSelectedDate(Date date) {
        this.selectedDate.set(date);
    }

    public Date getSelectedDate() {
        return selectedDate.get();
    }

    /**
     * Gets the date format.
     *
     * @return The date format.
     */
    public ObjectProperty<DateFormat> dateFormatProperty() {
        return dateFormat;
    }

    private ObjectProperty<DateFormat> dateFormat = new SimpleObjectProperty<DateFormat>();

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat.set(dateFormat);
    }

    public DateFormat getDateFormat() {
        return dateFormat.get();
    }

    private StringProperty promptText = new SimpleStringProperty();

    /**
     * The prompt text for the text field.
     * By default, the prompt text is taken from the date format pattern.
     *
     * @return The property.
     */
    public StringProperty promptTextProperty() {
        return promptText;
    }


    public void setPromptText(String promptText) {
        this.promptText.set(promptText);
    }

    public String getPromptText() {
        return promptText.get();
    }

    private Popup popup;

    /**
     * Shows the pop up.
     */
    private void showPopup() {

        if (popup == null) {
            popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);
            popup.setAutoFix(true);
            popup.getContent().add(calendarView);
        }

        Bounds calendarBounds = calendarView.getBoundsInLocal();
        Bounds bounds = localToScene(getBoundsInLocal());

        double posX = calendarBounds.getMinX() + bounds.getMinX() + getScene().getX() + getScene().getWindow().getX();
        double posY = calendarBounds.getMinY() + bounds.getHeight() + bounds.getMinY() + getScene().getY() + getScene().getWindow().getY();
        
        popup.show(this, posX, posY);
    }
}
