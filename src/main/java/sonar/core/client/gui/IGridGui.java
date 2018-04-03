package sonar.core.client.gui;

public interface IGridGui<T> {

	float getCurrentScroll(SelectionGrid grid);

	void onGridClicked(int gridID, T element, int x, int y, int pos, int button, boolean empty);

	void renderGridElement(int gridID, T element, int x, int y, int slot);

	void renderElementToolTip(int gridID, T element, int x, int y);

	void startToolTipRender(int gridID, T selection, int x, int y);

	default void preRender(int gridID) {}

	default void postRender(int gridID) {}
}
