import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class RandomWalk extends Pane {

    // custom attributes
    private final int STEP_COUNT = 2000;
    // Where the walk starts x coordinate
    private double startX;
    // y coordinate
    private double startY;
    private Circle car;
    private Polyline path;
    private Polyline drawLine;
    private PathTransition pathTrans;
    private static ArrayList<PathTransition> transList = new ArrayList<>();
    private static Button pause = new Button("Play");

    /**
     * Constructor for setting the start point of the walk, it also calls the paint() function which
     * runs the RandomWalk
     * @param startX x pixel coordinate
     * @param startY y pixel coordinate
     */
    public RandomWalk(double startX, double startY) {
      this.startX = startX;
      this.startY = startY;
      paint(true);
    }

    /**
     * Defaults constructor has the walk start at the middle of the scene
     */
    public RandomWalk(){}

    public void paint(boolean autoPlay) {
      // Clear children
      getChildren().clear();
      // Create the 'car' that walks the path
      car = new Circle(8);
      car.setFill(new Color(1, 0, 0, .4)); // opaque red
      car.setTranslateX(startX); // starting point of circle
      car.setTranslateY(startY);

      // Path to follow gets generated
      path = setWalkPath(startX, startY);

      // Create Polyline which will be the line being drawn
      drawLine = new Polyline();
      drawLine.setStroke(new Color(0, 0, 1, 0.4));

      // add car and drawLine to pane
      getChildren().addAll(car, drawLine);

      // Create PathTransition
      transList.add(pathTrans = getWalkTransition(path, car));
      if (autoPlay) pathTrans.play(); // play transition animation

      drawLine.setSmooth(true);

      // Listener on the car to draw the path
      car
        .boundsInParentProperty()
        .addListener((observable, oldValue, newValue) -> {
          // adds one point each time listener gets called
          drawLine.getPoints().add(car.translateXProperty().doubleValue());
          drawLine.getPoints().add(car.translateYProperty().doubleValue());
        });

      // Listens for collision on a green circle
      car
        .boundsInParentProperty()
        .addListener((c, n, o) -> checkCollision(car));

      //pause = new Button("Play");
      //getChildren().add(pause);

      pause.setOnAction(e -> {
        pauseAndPlay(transList);
        if (pathTrans.getStatus() != Animation.Status.RUNNING) {
          pause.setText("Play");
        } else pause.setText("Pause");
      });
    }

    public static void pauseAndPlay(ArrayList<PathTransition> list) {
      for (PathTransition transPath : list) {
        if (transPath.getStatus() != Animation.Status.RUNNING) {
          transPath.play();
        } else {
          transPath.pause();
        }
      }
    }

    /**
     *
     * @param path
     * @param node
     * @return
     */
    private PathTransition getWalkTransition(Shape path, Node node) {
      PathTransition pathTrans = new PathTransition();
      pathTrans.setPath(path);
      pathTrans.setNode(node);
      pathTrans.setDuration(Duration.seconds(210));
      pathTrans.setCycleCount(1);
      pathTrans.setInterpolator(Interpolator.LINEAR);

      return pathTrans;
    }

    /**
     * Creates a Polyline of STEP_COUNT, 2000, points and each point is randomly created as 9-18
     * pixels away from the previous point as well as in a random direction.
     * @param startX x pixel double for starting coordinate
     * @param startY y pixel double for starting coordinate
     * @return a random Polyline of 2000 points long
     */
    private Polyline setWalkPath(double startX, double startY) {
      Polyline path = new Polyline();
      double coordinates[] = { startX, startY };
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

    /**
     * returns a random whole number between 9 and 18
     * @return the amount of pixels to move
     */
    private double randDistance() {
      int num = 9 + (int) (Math.random() * ((18 - 9) + 1));
      return (double) num;
    }

    /**
     * takes two doubles representing coordinates and calculates a new set of coordinates that are
     * 9-18 pixels from the input coordinates and in a random cardinal/ordinal direction
     * @param coords a double array, using the first two elements as x and y coordinates
     */
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
     * returns a random double between 1-8 to represent the 8 cardinal/ordinal directions
     * @return double between 1-8
     */
    private double randDirection() {
      int direction = 1 + (int) (Math.random() * ((8 - 1) + 1));
      return getDirection(direction);
    }

    /**
     * translates a number 1-8 into a radian value to represent the 8 cardinal/ordinal directions
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

    // * Getters & Setters

    public int getSTEP_COUNT() {
      return STEP_COUNT;
    }

    public double getStartX() {
      return startX;
    }

    public void setStartX(double startX) {
      this.startX = startX;
    }

    public double getStartY() {
      return startY;
    }

    public void setStartY(double startY) {
      this.startY = startY;
    }

    public Circle getCar() {
      return car;
    }

    public void setCar(Circle car) {
      this.car = car;
    }

    public Polyline getPath() {
      return path;
    }

    public void setPath(Polyline path) {
      this.path = path;
    }

    public Polyline getDrawLine() {
      return drawLine;
    }

    public void setDrawLine(Polyline drawLine) {
      this.drawLine = drawLine;
    }

    public PathTransition getPathTrans() {
      return pathTrans;
    }

    public void setPathTrans(PathTransition pathTrans) {
      this.pathTrans = pathTrans;
    }

    public Button getPause() {
      return pause;
    }

    public void setPause(Button pause) {
      this.pause = pause;
    }

    public static ArrayList<PathTransition> getTransList() {
      return transList;
    }
  }
}