import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Project extends Application {

  int index = 0;

  final double PANESIZE = 800;
  BorderPane pane = new BorderPane();
  HBox hbox = new HBox();
  StackPane stackPane = new StackPane();
  Button resetBtn = new Button("Generate Line");
  Button circlesBtn = new Button("Generate Circles");
  RandomWalk walk = new RandomWalk();
  GreenCircles greenCircles = new GreenCircles();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    hbox.setSpacing(20);
    hbox.setAlignment(Pos.BOTTOM_RIGHT);

    stackPane.getChildren().addAll(greenCircles, walk);
    hbox.getChildren().addAll(resetBtn, circlesBtn);

    pane.setCenter(stackPane);
    pane.setBottom(hbox);

    circlesBtn.setOnAction(e -> greenCircles.paint());
    resetBtn.setOnAction(e -> walk.paint());
    greenCircles
      .boundsInLocalProperty()
      .addListener((observable, oldValue, newValue) -> {
        if (greenCircles.intersects(walk.getBoundsInLocal())) {
          System.out.println("Overlaps " + index++);
        }
      });

    Scene scene = new Scene(pane, PANESIZE, PANESIZE);
    stage.setTitle("Random Walk");
    stage.setScene(scene);
    stage.show();
    stage.setResizable(false);
  }

  public class GreenCircles extends Pane {

    private void paint() {
      getChildren().clear();
      Circle[] randCircles = new Circle[10];

      for (int i = 0; i < 10; i++) {
        randCircles[i] = new Circle(randCoord(), randCoord(), 15);
        randCircles[i].setFill(Color.GREEN);
        getChildren().add(randCircles[i]);
      }
    }

    private double randCoord() {
      return 0 + (int) (Math.random() * ((PANESIZE - 0) + 1));
    }
  }

  public class RandomWalk extends Pane {

    private final int STEP_COUNT = 2000;

    private void paint() {
      // Clear children
      getChildren().clear();
      // Create the 'car' that walks the path
      Circle car = new Circle(8);
      car.setFill(new Color(1, 0, 0, .4)); // opaque red
      car.setTranslateX(PANESIZE / 2); // half window size
      car.setTranslateY(PANESIZE / 2); // half window size

      // Create Polyline and get a new random path
      Polyline path = setWalkPath();
      // Create Polyline which will be the line being drawn
      Polyline drawLine = new Polyline();
      drawLine.setStroke(new Color(0, 0, 1, 0.4));

      // add car and drawLine to pane
      getChildren().addAll(car, drawLine);

      // Create PathTransition
      PathTransition pathTrans = getWalkTransition(path, car);
      pathTrans.play(); // play transition animation

      // Listener for change in circle x, will add points to the drawLine
      car
        .translateXProperty()
        .addListener((observable, oldValue, newValue) -> {
          // Adds one point each time listener gets called
          drawLine.getPoints().add(car.translateXProperty().doubleValue());
          drawLine.getPoints().add(car.translateYProperty().doubleValue());
        });
      // Listener for change in circle y, will add points to the drawLine
      car
        .translateYProperty()
        .addListener((observable, oldValue, newValue) -> {
          // adds one point each time listener gets called
          drawLine.getPoints().add(car.translateXProperty().doubleValue());
          drawLine.getPoints().add(car.translateYProperty().doubleValue());
        });
    }

    private PathTransition getWalkTransition(Shape path, Node node) {
      PathTransition pathTrans = new PathTransition();
      pathTrans.setPath(path);
      pathTrans.setNode(node);
      pathTrans.setDuration(Duration.seconds(480));
      pathTrans.setCycleCount(1);
      pathTrans.setInterpolator(Interpolator.LINEAR);

      return pathTrans;
    }

    private Polyline setWalkPath() {
      Polyline path = new Polyline();
      double coordinates[] = { PANESIZE / 2, PANESIZE / 2 };
      for (int i = 0; i < STEP_COUNT; i++) {
        // Add a point to the polyLine
        // X coord
        path.getPoints().add(coordinates[0]);
        // Y coord
        path.getPoints().add(coordinates[1]);
        // Generates random endX endY coordinates
        getEndCoordinates(coordinates);
      }
      return path;
    }

    private double randDistance() {
      int num = 9 + (int) (Math.random() * ((18 - 9) + 1));
      return (double) num;
    }

    private void getEndCoordinates(double[] coords) {
      double newX, newY;
      do {
        double changeInDistance = randDistance();
        double direction = randDirection();
        newX = changeInDistance * Math.cos(direction) + coords[0];
        newY = changeInDistance * Math.sin(direction) + coords[1];
      } while (newX < 0 || newY < 0 || newX > 800 || newY > 800);

      coords[0] = newX;
      coords[1] = newY;
    }

    /**
     * returns a random double between 1-8 to represent the 8 standard cardinal directions
     * @return double between 1-8
     */
    private double randDirection() {
      int direction = 1 + (int) (Math.random() * ((8 - 1) + 1));
      return getDirection(direction);
    }

    /**
     * translates a number 1-8 into a radian value to represent the 8 cardinal directions
     * @param num the number that gets translated
     * @return a radian value for direction
     */
    private double getDirection(int num) {
      final double PI = Math.PI;
      switch (num) {
        case 1: // East
          return (double) (0);
        case 2: // North East
          return (double) (PI / 4);
        case 3: // North
          return (double) (PI / 2);
        case 4: // North West
          return (double) ((3 * PI) / 4);
        case 5: // West
          return (double) (PI);
        case 6: // South West
          return (double) ((5 * PI) / 4);
        case 7: // South
          return (double) ((3 * PI) / 2);
        default: // Default South East, just to please
          return (double) ((7 * PI) / 4);
      }
    }
  }
}
