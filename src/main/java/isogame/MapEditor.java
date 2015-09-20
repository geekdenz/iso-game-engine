package isogame;
 
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
 
public class MapEditor extends Application {
	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}

	@Override
	public void start(javafx.stage.Stage primaryStage) {
		Canvas canvas = new Canvas();
		StackPane root = new StackPane();
		root.getChildren().add(canvas);

		try {
			Stage stage = exampleStage();

			AnimationTimer animateCanvas = new AnimationTimer() {
				@Override
				public void handle(long now) {
					GraphicsContext cx = canvas.getGraphicsContext2D();
					cx.clearRect(0, 0, 300, 250);
					stage.render(cx, null);
				}
			};

			animateCanvas.start();

			Scene scene = new Scene(root, 300, 250);

			primaryStage.setTitle("Hello World!");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Make a simple example stage for testing purposes.
	 * */
	private Stage exampleStage() throws CorruptDataException {
		TerrainTexture black = new TerrainTexture("/black.jpg");
		TerrainTexture white = new TerrainTexture("/white.jpg");
		Tile[] data = new Tile[8 * 8];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				TerrainTexture t;
				if ((x + y) % 2 == 0) t = black; else t = white;
				data[(y * 8) + x] = new Tile(
					new MapPoint(x, y), 0,
					SlopeType.NONE, false,
					StartZoneType.NONE, t);
			}
		}
		StageInfo terrain = new StageInfo(8, 8, data);
		return new Stage(terrain);
	}
}

