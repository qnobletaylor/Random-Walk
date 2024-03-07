import java.util.ArrayList;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
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

    final int SIZE = 2000;

    private void paint() {
      getChildren().clear();
      Circle car = new Circle(8);
      car.setFill(new Color(1, 0, 0, .4));
      PathTransition trans = new PathTransition();

      Polyline linePath = new Polyline();
      ArrayList<Double> points = new ArrayList<>();
      double coordinates[] = { 400, 400, 0, 0 };
      for (int i = 0; i < 2000; i++) {
        getEndCoordinates(coordinates);
        points.add(coordinates[0]);
        points.add(coordinates[1]);
        Line line = new Line(
          coordinates[0],
          coordinates[1],
          coordinates[2],
          coordinates[3]
        );
        //Circle circle = new Circle(coordinates[2], coordinates[3], 5);
        //circle.setFill(new Color(1, 0, 0, .3));
        //line.setStroke(Color.BLUE);
        resetStartCoords(coordinates);
        //getChildren().addAll(circle);
      }
      //linePath.setStroke(Color.BLUE);
      getChildren().addAll(car);
      linePath.getPoints().addAll(points);

      trans.setPath(linePath);
      trans.setNode(car);
      trans.setDuration(Duration.seconds(480));
      trans.setCycleCount(PathTransition.INDEFINITE);
      trans.setInterpolator(Interpolator.LINEAR);
      trans.play();

      DoubleProperty xPos = new SimpleDoubleProperty();
      DoubleProperty yPos = new SimpleDoubleProperty();

      xPos.bind(car.translateXProperty());
      yPos.bind(car.translateYProperty());

      car
        .translateXProperty()
        .addListener((observable, oldValue, newValue) -> {
          System.out.println("car moving");
          Circle drawLine = new Circle(
            xPos.doubleValue(),
            yPos.doubleValue(),
            3
          );
          drawLine.setFill(new Color(0, 0, 1, 0.4));
          getChildren().add(drawLine);
        });
      car
        .translateYProperty()
        .addListener((observable, oldValue, newValue) -> {
          System.out.println("car moving");
          Circle drawLine = new Circle(
            xPos.doubleValue(),
            yPos.doubleValue(),
            3
          );
          drawLine.setFill(new Color(0, 0, 1, 0.4));
          getChildren().add(drawLine);
        });
    }

    private void resetStartCoords(double[] coords) {
      coords[0] = coords[2];
      coords[1] = coords[3];
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

      coords[2] = newX;
      coords[3] = newY;
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
