package isogame.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.util.Callback;
import java.util.function.Supplier;

public class TypedTextFieldTreeTableCell<S, T> extends TreeTableCell<S, T> {
	public static <S, T> Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>>
		forTreeTableColumn(
			Supplier<TypedTextField<T>> constructor,
			TreeTableViewSelectionModel<S> selection
	) {
		return (column -> new TypedTextFieldTreeTableCell<S, T>(constructor, selection));
	}

	private final Supplier<TypedTextField<T>> constructor;
	private final TreeTableViewSelectionModel<S> selection;
	public TypedTextFieldTreeTableCell(
		Supplier<TypedTextField<T>> constructor,
		TreeTableViewSelectionModel<S> selection
	) {
		this.constructor = constructor;
		this.selection = selection;
	}

	private TypedTextField<T> textField;
	private void createTextField() {
		textField = constructor.get();
		textField.setValue(getItem());
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
		textField.focusedProperty().addListener((arg0, arg1, arg2) -> {
			if (!arg2) commitEdit(textField.getValue());
		});
		ChangeListener<Number> selectionChanged = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				commitEdit(textField.getValue());
				selection.selectedIndexProperty().removeListener(this);
			}
		};

		selection.selectedIndexProperty().addListener(selectionChanged);
		textField.setOnAction(event -> commitEdit(textField.getValue()));
	}

	@Override
	public void commitEdit(T v) {
		super.commitEdit(v);
	}


	@Override
	public void startEdit() {
		if (!isEmpty()) {
			super.startEdit();
			createTextField();
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();

		setText(getItem().toString());
		setGraphic(null);
	}

	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setValue(getItem());
				}
				setText(null);
				setGraphic(textField);
			} else {
				setText(getItem() == null? "" : getItem().toString());
				setGraphic(null);
			}
		}
	}
}

