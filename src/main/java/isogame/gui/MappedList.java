package isogame.gui;

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.List;
import java.util.stream.Collectors;

public class MappedList<E, F> extends TransformationList<E, F> {
	private Function<F, E> map;

	public MappedList(ObservableList<? extends F> source, Function<F, E> map) {
		super(source);
		this.map = map;
	}

	@Override
	public int getSourceIndex(int index) {
		return index;
	}

	@Override
	public E get(int i) {
		return map.apply(getSource().get(i));
	}

	@Override
	public int size() {
		return getSource().size();
	}

	@Override
	protected void sourceChanged(ListChangeListener.Change<? extends F> c) {
		// adapted from https://gist.github.com/TomasMikula/8883719
		fireChange(new Change<E>(this) {
			@Override
			public boolean wasAdded() {return c.wasAdded();}

			@Override
			public boolean wasRemoved() {return c.wasRemoved();}

			@Override
			public boolean wasReplaced() {return c.wasReplaced();}

			@Override
			public boolean wasUpdated() {return c.wasUpdated();}

			@Override
			public boolean wasPermutated() {return c.wasPermutated();}

			@Override
			public int getPermutation(int i) {return c.getPermutation(i);}

			@Override
			public int getFrom() {return c.getFrom();}

			@Override
			public int getTo() {return c.getTo();}

			@Override
			public boolean next() {return c.next();}

			@Override
			public void reset() {c.reset();}

			@Override
			protected int[] getPermutation() {
				// This method is only called by the superclass methods
				// wasPermutated() and getPermutation(int), which are
				// both overriden by this class. There is no other way
				// this method can be called.
				throw new AssertionError("Unreachable code");
			}

			@Override
			public List<E> getRemoved() {
				return c.getRemoved().stream().map(map).collect(Collectors.toList());
			}
		});
	}
}

