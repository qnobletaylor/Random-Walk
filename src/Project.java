import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class Project extends Application {

  final double SCENE_SIZE = 800;
  private BorderPane rootPane = new BorderPane();
  private HBox hBox = new HBox();
  private static Pane pane = new Pane();
  private Button resetBtn = new Button("Reset Scene");
  private Button circlesBtn = new Button("Create Circles");
  private Button pauseBtn = new Button("Play");
  private static RandomWalk rootWalk = new RandomWalk();
  private static ArrayList<GreenCircle> greenCircleList;
  private static ArrayList<RandomWalk> walkList;

  // Create the collision listener
  private static ChangeListener<Bounds> collisionListener = new ChangeListener<>() {
    @Override
    public void changed(
      ObservableValue<? extends Bounds> arg0,
      Bounds arg1,
      Bounds arg2
    ) {
      checkCollision();
    }
  };

  public static void main(String[] args) {
    launch(args);
  }

  // start function
  @Override
  public void start(Stage stage) throws Exception {
    // init the lists
    greenCircleList = new ArrayList<>();
    walkList = new ArrayList<>();
    // Set the main RandomWalk start coordinates
    rootWalk.setStartX(SCENE_SIZE / 2);
    rootWalk.setStartY(SCENE_SIZE / 2);
    rootWalk.paint(false);
    pane.getChildren().add(rootWalk);

    hBox.setSpacing(20); // 20 pixels between each node in Hbox
    hBox.setAlignment(Pos.BOTTOM_CENTER); // aligns nodes to the bottom center
    hBox.getChildren().addAll(pauseBtn, resetBtn, circlesBtn);

    rootPane.setCenter(pane);
    rootPane.setBottom(hBox);

    // Resets the pane and creates new green circles
    circlesBtn.setOnAction(e -> {
      clearScene();
      setAllListeners(walkList);
      for (int i = 0; i < 10; i++) {
        greenCircleList.add(new GreenCircle());
      }
      pane.getChildren().addAll(greenCircleList);
    });

    // Resets the pane.
    resetBtn.setOnAction(e -> {
      clearScene();
      setAllListeners(walkList);
    });

    // Event handler to pause and play all animations.
    pauseBtn.setOnAction(e -> {
      RandomWalk.pauseAndPlay();
    });

    Scene scene = new Scene(rootPane, SCENE_SIZE, SCENE_SIZE);
    stage.setTitle("Random Walk");
    stage.setScene(scene);
    stage.show();
    stage.setResizable(false); // Non resizable window
  }

  /**
   * Clears the pane holding the lines and green circles of all nodes, also removes the listeners
   * from all walk objects prior to removing them from the lists.
   */
  private static void clearScene() {
    pane.getChildren().remove(1, pane.getChildren().size());
    removeAllListeners(walkList);
    walkList.clear();
    greenCircleList.clear();
    RandomWalk.getTransList().clear();
    walkList.add(rootWalk);
    rootWalk.paint(false);
  }

  /**
   * sets the listeners of all RandomWalk objects within the walkList
   * @param walkList arrayList of RandomWalks
   */
  private static void setAllListeners(ArrayList<RandomWalk> walkList) {
    for (RandomWalk i : walkList) {
      if (!i.isListener()) { //Checks for listener flag
        i.getCar().boundsInParentProperty().addListener(collisionListener);
        i.setListener(true);
      }
    }
  }

  /**
   * removes collision listener from all RandomWalk objects within the walkList
   * @param walkList arrayList of RandomWalks
   */
  private static void removeAllListeners(ArrayList<RandomWalk> walkList) {
    for (RandomWalk i : walkList) {
      i.getCar().boundsInParentProperty().removeListener(collisionListener);
      i.setListener(false);
    }
  }

  /**
   * Checks for the collision between greenCircles and any car of a RandomWalk.  If a collision is
   * detected then a new anonymous RandomWalk is created and added to the walkList and
   * setAllListeners gets called to set the listener for the new object.
   */
  private static void checkCollision() {
    for (GreenCircle green : greenCircleList) {
      for (RandomWalk walk : walkList) {
        Shape intersect = Shape.intersect(walk.getCar(), green);
        if (intersect.getBoundsInLocal().getWidth() != -1) {
          if (!green.getTouched()) {
            green.setFill(Color.RED); // set the circle color to red
            green.setTouched(true); // set the interaction flag
            // Console msg
            System.out.printf(
              "Green Circle Collided @ %.2f, %.2f\n",
              green.getLayoutX(),
              green.getLayoutY()
            );
            // Create a new random walk and add it to the list
            walkList.add(
              new RandomWalk(green.getLayoutX(), green.getLayoutY(), true)
            );
            // add it to the pane
            pane.getChildren().add(walkList.get(walkList.size() - 1));
            // set listener on each new 'car'
            setAllListeners(walkList);
          }
        }
      }
    }
  }
}
