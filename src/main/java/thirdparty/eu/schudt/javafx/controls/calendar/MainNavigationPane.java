package thirdparty.eu.schudt.javafx.controls.calendar;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Calendar;

/**
 * The main navigation pane.
 *
 * @author Christian Schudt
 */
final class MainNavigationPane extends HBox {

    private static final String CSS_CALENDAR_NAVIGATION_ARROW = "calendar-navigation-arrow";
    private static final String CSS_CALENDAR_NAVIGATION_BUTTON = "calendar-navigation-button";
    private static final String CSS_CALENDAR_NAVIGATION_TITLE = "calendar-navigation-title";
    private static final String CSS_CALENDAR_HEADER = "calendar-header";

    private CalendarView calendarView;
    Button titleButton;

    public MainNavigationPane(final CalendarView calendarView) {

        this.calendarView = calendarView;


        titleButton = new Button();
        titleButton.getStyleClass().add(CSS_CALENDAR_NAVIGATION_TITLE);
        titleButton.textProperty().bind(calendarView.title);

        titleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switch (calendarView.currentlyViewing.get()) {
                    case Calendar.MONTH:
                        calendarView.currentlyViewing.set(Calendar.YEAR);
                        break;
                    case Calendar.YEAR:
                        calendarView.currentlyViewing.set(Calendar.ERA);
                }
            }
        });
        titleButton.disableProperty().bind(new BooleanBinding() {
            {
                super.bind(calendarView.ongoingTransitions, calendarView.currentlyViewing);
            }

            @Override
            protected boolean computeValue() {
                return calendarView.currentlyViewing.get() == Calendar.ERA || calendarView.ongoingTransitions.get() > 0;
            }
        });
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(titleButton);
        buttonBox.setAlignment(Pos.CENTER);

        HBox.setHgrow(buttonBox, Priority.ALWAYS);

        getChildren().add(getNavigationButton(-1));
        getChildren().add(buttonBox);
        getChildren().add(getNavigationButton(1));

        getStyleClass().add(CSS_CALENDAR_HEADER);
    }

    /**
     * Gets a navigation button.
     *
     * @param direction Either -1 (for left) or 1 (for right).
     * @return The button.
     */
    private Button getNavigationButton(final int direction) {

        Button button = new Button();

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Calendar calendar = calendarView.getCalendar();
                switch (calendarView.currentlyViewing.get()) {
                    case Calendar.MONTH:
                        calendar.add(Calendar.MONTH, 1 * direction);
                        break;
                    case Calendar.YEAR:
                        calendar.add(Calendar.YEAR, 1 * direction);
                        break;
                    case Calendar.ERA:
                        calendar.add(Calendar.YEAR, 20 * direction);
                        break;
                }

                calendarView.calendarDate.set(calendar.getTime());
            }
        });

        // Make a region, so that -fx-shape can be applied from CSS.
        Region rectangle = new Region();
        rectangle.setMaxWidth(Control.USE_PREF_SIZE);
        rectangle.setMaxHeight(Control.USE_PREF_SIZE);
        rectangle.setRotate(direction < 0 ? 90 : 270);
        rectangle.getStyleClass().add(CSS_CALENDAR_NAVIGATION_ARROW);
        // Set that region as the button graphic.
        button.setGraphic(rectangle);
        button.getStyleClass().add(CSS_CALENDAR_NAVIGATION_BUTTON);
        return button;
    }

}
