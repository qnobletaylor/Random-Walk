import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Description : GreenCircle is a class that can create a circle but adds in specific attributes for
 * this program's usage.
 *
 * @author Quinlin Taylor
 * @since Thu Mar 7 2024
 * @file GreenCircle.java
 */
public class GreenCircle extends Circle {

  private final double PANESIZE = 800; //Size of window
  private boolean touched; // Flag for checking if the circle has been interacted with

  /**
   * Default constructor always creates a circle with the same attributes
   */
  public GreenCircle() {
    setRadius(20);
    setLayoutX(randCoord());
    setLayoutY(randCoord());
    setFill(Color.GREEN);
    touched = false;
  }

  /**
   * returns a random coordinate between 0 and the size of window
   * @return double value to be used as either x or y coordinate
   */
  private double randCoord() {
    return 0 + (int) (Math.random() * ((PANESIZE - 0) + 1));
  }

  public void setTouched(boolean bool) {
    touched = bool;
  }

  public boolean getTouched() {
    return touched;
  }
}
