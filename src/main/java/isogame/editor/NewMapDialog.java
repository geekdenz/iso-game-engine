package isogame.editor;

import isogame.engine.CorruptDataException;
import isogame.engine.MapPoint;
import isogame.engine.StageInfo;
import isogame.engine.TerrainTexture;
import isogame.engine.Tile;
import isogame.gui.PositiveIntegerField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Dialog box to create new terrain textures
 * */
public class NewMapDialog extends Dialog<StageInfo> {
	public NewMapDialog(TerrainTexture blank) {
		super();

		// Set up the header and footer
		this.setTitle("New map");
		this.setHeaderText("Set the dimensions of the map");
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// The dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		PositiveIntegerField width = new PositiveIntegerField();
		PositiveIntegerField height = new PositiveIntegerField();

		grid.add(new Label("Width"), 0, 0);
		grid.add(width, 1, 0);

		grid.add(new Label("Height"), 0, 1);
		grid.add(height, 1, 1);

		this.getDialogPane().setContent(grid);

		this.setResultConverter(clickedButton -> {
			if (clickedButton == ButtonType.OK) {
				int w = width.getValue();
				int h = height.getValue();
				if (w == 0 || h == 0) return null;

				Tile[] tiles = new Tile[w * h];
				for (int y = 0; y < w; y++) {
					for (int x = 0; x < h; x++) {
						tiles[(y * w) + x] = new Tile(new MapPoint(x, y), blank);
					}
				}

				try {
					return new StageInfo(w, h, tiles);
				} catch (CorruptDataException e) {
					return null;
				}
			} else {
				return null;
			}
		});
	}
}


