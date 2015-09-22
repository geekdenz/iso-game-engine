package isogame;
 
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import java.util.EnumSet;
import java.util.Set;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.SCROLL_SPEED;
 
public class MapEditor extends Application {
	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}

	@Override
	public void start(javafx.stage.Stage primaryStage) {
		StackPane root = new StackPane();
		Scene scene = new Scene(root, 960, 540);

		Canvas canvas = new Canvas();
		root.getChildren().add(canvas);
		canvas.widthProperty().bind(scene.widthProperty());
		canvas.heightProperty().bind(scene.heightProperty());

		try {
			Stage stage = exampleStage();
			View view = new View(960, 540);
			view.centreOnTile(stage, new MapPoint(3, 3));

			final ContinuousAnimator scrolling = new ContinuousAnimator();
			scrolling.reset(view.getScrollPos());

			final GraphicsContext cx = canvas.getGraphicsContext2D();

			AnimationTimer animateCanvas = new AnimationTimer() {
				int count0 = 0;
				int count = 0;
				long now0 = 0;

				@Override
				public void handle(long now) {
					count++;
					if (now0 == 0) now0 = now;
					if ((now - now0) >= 5000000000l) {
						System.err.println("fps: " + ((count - count0) / 5));
						now0 = now;
						count0 = count;
					}

					view.setScrollPos(scrolling.valueAt(now));
					view.renderFrame(cx, stage);
				}
			};

			primaryStage.setTitle("Hello World!");
			primaryStage.setScene(scene);
			animateCanvas.start();
			primaryStage.show();

			// Listen for window resize events
			scene.widthProperty().addListener((obs, w, w0) -> {
				view.setViewport(w.intValue(), (int) canvas.getHeight());
			});
			scene.heightProperty().addListener((obs, h, h0) -> {
				view.setViewport((int) canvas.getWidth(), h.intValue());
			});

			// Listen for keyboard events
			final Set<KeyCode> keys = EnumSet.noneOf(KeyCode.class);
			scene.setOnKeyPressed(event -> {
				KeyCode k = event.getCode();
				keys.add(k);
				setScrollingAnimation(scrolling, keys);
				switch (k) {
					case A: view.rotateLeft(); break;
					case D: view.rotateRight(); break;
				}
			});
			scene.setOnKeyReleased(event -> {
				keys.remove(event.getCode());
				setScrollingAnimation(scrolling, keys);
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void setScrollingAnimation(
		ContinuousAnimator scrolling, Set<KeyCode> keys
	) {
		boolean kup = keys.contains(KeyCode.UP);
		boolean kdown = keys.contains(KeyCode.DOWN);
		boolean kleft = keys.contains(KeyCode.LEFT);
		boolean kright = keys.contains(KeyCode.RIGHT);

		if (kup && !kdown) {
			if (kleft && !kright) {
				scrolling.setAnimation(new Point2D(-TILEH, -TILEH), SCROLL_SPEED);
				scrolling.start();
			} else if (kright && !kleft) {
				scrolling.setAnimation(new Point2D(TILEH, -TILEH), SCROLL_SPEED);
				scrolling.start();
			} else if (!kleft && !kright) {
				scrolling.setAnimation(new Point2D(0, -TILEH), SCROLL_SPEED);
				scrolling.start();
			}
		} else if (kdown && !kup) {
			if (kleft && !kright) {
				scrolling.setAnimation(new Point2D(-TILEH, TILEH), SCROLL_SPEED);
				scrolling.start();
			} else if (kright && !kleft) {
				scrolling.setAnimation(new Point2D(TILEH, TILEH), SCROLL_SPEED);
				scrolling.start();
			} else if (!kleft && !kright) {
				scrolling.setAnimation(new Point2D(0, TILEH), SCROLL_SPEED);
				scrolling.start();
			}
		} else if (!kdown && !kup) {
			if (kleft && !kright) {
				scrolling.setAnimation(new Point2D(-TILEH, 0), SCROLL_SPEED);
				scrolling.start();
			} else if (kright && !kleft) {
				scrolling.setAnimation(new Point2D(TILEH, 0), SCROLL_SPEED);
				scrolling.start();
			} else {
				scrolling.stop();
			}
		} else {
			scrolling.stop();
		}
	}

	/**
	 * Make a simple example stage for testing purposes.
	 * */
	private Stage exampleStage() throws CorruptDataException {
		TerrainTexture black = new TerrainTexture("black", "/black.jpg");
		TerrainTexture white = new TerrainTexture("white", "/white.jpg");
		CliffTexture cliff = new CliffTexture(
			"cliffs",
			"/cliff_texture.png",
			"/cliff_texture_narrow.png");
		Tile[] data = new Tile[8 * 8];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				TerrainTexture t;
				if ((x + y) % 2 == 0) t = black; else t = white;
				int elevation = 0;
				SlopeType slope = SlopeType.NONE;
				if (x == 0 && y == 1) slope = SlopeType.N;
				if (x == 1 && y == 1) slope = SlopeType.N;
				if (x == 0 && y == 0) elevation = 1;
				if (x == 1 && y == 0) elevation = 1;

				if (x == 0 && y == 6) slope = SlopeType.S;
				if (x == 1 && y == 6) slope = SlopeType.S;
				if (x == 0 && y == 7) elevation = 1;
				if (x == 1 && y == 7) elevation = 1;

				if (x == 6 && y == 0) slope = SlopeType.E;
				if (x == 6 && y == 1) slope = SlopeType.E;
				if (x == 7 && y == 0) elevation = 1;
				if (x == 7 && y == 1) elevation = 1;

				if (x == 7 && y == 6) slope = SlopeType.W;
				if (x == 7 && y == 7) slope = SlopeType.W;
				if (x == 6 && y == 6) elevation = 1;
				if (x == 6 && y == 7) elevation = 1;

				if (x == 4 && y == 3) slope = SlopeType.S;
				if (x == 3 && y == 4) slope = SlopeType.E;
				if (x == 4 && y == 5) slope = SlopeType.N;
				if (x == 5 && y == 4) slope = SlopeType.W;

				data[(y * 8) + x] = new Tile(
					new MapPoint(x, y), elevation,
					slope, false,
					StartZoneType.NONE, t, cliff);
			}
		}
		StageInfo terrain = new StageInfo(8, 8, data);
		return new Stage(terrain);
	}
}

