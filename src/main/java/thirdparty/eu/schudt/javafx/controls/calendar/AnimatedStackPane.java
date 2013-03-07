package thirdparty.eu.schudt.javafx.controls.calendar;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Calendar;
import java.util.Date;

/**
 * The StackPane which manages two {@link DatePane}, which are necessary for the animation.
 *
 * @author Christian Schudt
 */
final class AnimatedStackPane extends StackPane {

    DatePane animatePane;
    DatePane actualPane;

    private final static Double SLIDE_ANIMATION_DURATION = 0.7;

    public AnimatedStackPane(final DatePane actualPane, final DatePane animatePane) {

        // The first MonthView.
        this.animatePane = animatePane;

        // Set it invisible as long as it is not needed.
        animatePane.setVisible(false);

        // The second MonthView.
        this.actualPane = actualPane;

        getChildren().add(animatePane);
        getChildren().add(actualPane);
        getStyleClass().add("calendar-daypane");

        // Listen to changes of the calendar date, if it changes, check if the new date has another month and move the panes accordingly.
        actualPane.calendarView.calendarDate.addListener(new ChangeListener<Date>() {
            @Override
            public void changed(ObservableValue<? extends Date> observableValue, Date oldDate, Date newDate) {


                Calendar calendar = actualPane.calendarView.getCalendar();

                calendar.setTime(oldDate);
                int oldYear = calendar.get(Calendar.YEAR);
                int oldMonth = calendar.get(Calendar.MONTH);

                calendar.setTime(newDate);
                int newYear = calendar.get(Calendar.YEAR);
                int newMonth = calendar.get(Calendar.MONTH);

                // move the panes, if necessary.
                if (getWidth() > 0 && actualPane.calendarView.ongoingTransitions.get() == 0) {
                    if (newYear > oldYear || newYear == oldYear && newMonth > oldMonth) {
                        slideLeftRight(-1, oldDate);
                    } else if (newYear < oldYear || newYear == oldYear && newMonth < oldMonth) {
                        slideLeftRight(1, oldDate);
                    }
                }
            }
        });
    }

    private ParallelTransition slideTransition;

    /**
     * Slides the panes from left to right or vice versa.
     *
     * @param direction The direction, either 1 (moves to right) or -1 (moves to left).
     * @param oldDate   The date, which the {@link #animatePane} gets set.
     */
    private void slideLeftRight(int direction, Date oldDate) {

        // Stop any previous animation.
        if (slideTransition != null) {
            slideTransition.stop();
        }

        TranslateTransition transition1 = new TranslateTransition(Duration.seconds(SLIDE_ANIMATION_DURATION), animatePane);
        TranslateTransition transition2 = new TranslateTransition(Duration.seconds(SLIDE_ANIMATION_DURATION), actualPane);

        // Set the animatePane to visible.
        animatePane.setVisible(true);

        // Set the offset depending on the direction.‚‚
        animatePane.setDate(oldDate);

        // Set the clip, so that the translate transition stays within the clip.
        // Use the bounds from one pane.
        setClip(new Rectangle(animatePane.getBoundsInLocal().getWidth(), animatePane.getBoundsInLocal().getHeight()));

        // Move the old pane away from 0. (I added 1px, so that both panes overlap, which makes it look a little smoother).
        transition1.setFromX(-direction * 1);
        // and either to right or to left.
        transition1.setToX(getLayoutBounds().getWidth() * direction + -direction * 1);

        // Move new pane from left or right
        transition2.setFromX(-getBoundsInParent().getWidth() * direction);

        // Move the new pane to 0
        transition2.setToX(0);

        slideTransition = new ParallelTransition();
        slideTransition.getChildren().addAll(transition1, transition2);
        slideTransition.setInterpolator(Interpolator.EASE_OUT);

        slideTransition.play();
        slideTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // When we are finished, set the animate pane to invisible and remove the clip.
                animatePane.setVisible(false);
                // If the calendar gets resized, we don't have any clip.
                setClip(null);
            }
        });
    }
}
