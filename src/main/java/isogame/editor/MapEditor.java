package isogame.editor;
 
import javafx.application.Application;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.function.Function;
 
public class MapEditor extends Application {
	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}

	@Override
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();
		Scene scene = new Scene(root, 960, 540);
		BorderPane guiRoot = new BorderPane();

		try {
			File dataDir = getDataDir(primaryStage);
			if (dataDir == null) System.exit(1);

			Function<String, String> urlConverter = url ->
				(new File(dataDir, "gfx/" + url)).toURI().toString();

			ToggleGroup toolsGroup = new ToggleGroup();
			EditorCanvas canvas = new EditorCanvas(root, primaryStage);
			LibraryPane library = new LibraryPane(
				dataDir, urlConverter, toolsGroup, canvas);
			MainMenu menuBar = new MainMenu(library, dataDir, urlConverter, canvas);
			ToolBar toolBar = new ToolBar(canvas, toolsGroup);

			VBox top = new VBox();
			top.getChildren().addAll(menuBar, toolBar);

			guiRoot.setTop(top);
			guiRoot.setLeft(library);
			root.getChildren().addAll(canvas, guiRoot);

			canvas.widthProperty().bind(root.widthProperty());
			canvas.heightProperty().bind(root.heightProperty());

			primaryStage.setTitle("isogame map editor");
			primaryStage.setScene(scene);
			canvas.startAnimating();
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public File getDataDir(Stage primaryStage) {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select data directory...");
		File dataDir = directoryChooser.showDialog(primaryStage);
		return dataDir;
	}
}

