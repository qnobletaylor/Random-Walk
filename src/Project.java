import java.util.ArrayList;
import javafx.application.Application;
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
  private HBox hbox = new HBox();
  private static Pane pane = new Pane();
  private Button resetBtn = new Button("Reset Stage");
  private Button circlesBtn = new Button("Create Circles");
  private Button pauseBtn = new Button("Play");
  private RandomWalk rootWalk = new RandomWalk();
  private static ArrayList<GreenCircle> greenCircleList;
  private static ArrayList<RandomWalk> walkList;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    greenCircleList = new ArrayList<>();
    walkList = new ArrayList<>();

    hbox.setSpacing(20);
    hbox.setAlignment(Pos.BOTTOM_RIGHT);

    pane.getChildren().add(rootWalk);

    hbox.getChildren().addAll(pauseBtn, resetBtn, circlesBtn);
    rootPane.setCenter(pane);
    rootPane.setBottom(hbox);

    // Resets the pane and creates new green circles
    circlesBtn.setOnAction(e -> {
      if (pane.getChildren().size() > 1) {
        pane.getChildren().remove(1, pane.getChildren().size());
        greenCircleList.clear();
      }

      for (int i = 0; i < 10; i++) {
        greenCircleList.add(new GreenCircle());
        pane.getChildren().add(greenCircleList.get(i));
      }
    });

    // Resets the pane.
    resetBtn.setOnAction(e -> {
      rootWalk.paint(false);
      pane.getChildren().remove(1, pane.getChildren().size()); // removes everything on pane
      walkList.clear();
      greenCircleList.clear();
    });

    // Event handler to pause and play all animations.
    pauseBtn.setOnAction(e -> RandomWalk.pauseAndPlay(RandomWalk.getTransList())
    );

    Scene scene = new Scene(rootPane, SCENE_SIZE, SCENE_SIZE);
    stage.setTitle("Random Walk");
    stage.setScene(scene);
    stage.show();
    stage.setResizable(false);
  }

  private static void checkCollision(Shape car) {
    for (GreenCircle green : greenCircleList) {
      Shape intersect = Shape.intersect(car, green);
      if (intersect.getBoundsInLocal().getWidth() != -1) {
        if (!green.getTouched()) {
          green.setFill(Color.RED);
          green.setTouched(true);
          System.out.printf(
            "Green Circle Collided @ %.2f, %.2f\n",
            green.getLayoutX(),
            green.getLayoutY()
          );
          walkList.add(new RandomWalk(green.getLayoutX(), green.getLayoutY()));
          pane.getChildren().add(walkList.get(walkList.size() - 1));
        }
      }
    }
  }
}
