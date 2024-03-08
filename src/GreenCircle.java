import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GreenCircle extends Circle {

  private final double PANESIZE = 800;
  private boolean touched;

  public GreenCircle() {
    setRadius(15);
    setLayoutX(randCoord());
    setLayoutY(randCoord());
    setFill(Color.GREEN);
    touched = false;
  }

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
