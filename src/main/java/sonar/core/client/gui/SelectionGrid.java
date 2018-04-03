package sonar.core.client.gui;

public class SelectionGrid<T> extends GuiGridElement<T> {
	public IGridGui selectGrid;

	public SelectionGrid(IGridGui selectGrid, int gridID, int yPos, int xPos, int eWidth, int eHeight, int gWidth, int gHeight) {
		super(gridID, yPos, xPos, eWidth, eHeight, gWidth, gHeight);
		this.selectGrid = selectGrid;
	}

	@Override
	public float getCurrentScroll() {
		return selectGrid.getCurrentScroll(this);
	}

	@Override
	public void onGridClicked(T selection, int x, int y, int pos, int button, boolean empty) {
		selectGrid.onGridClicked(gridID, selection, x, y, pos, button, empty);
	}

	@Override
	public void renderGridElement(T selection, int x, int y, int slot) {
		selectGrid.renderGridElement(gridID, selection, x, y, slot);
	}

	@Override
	public void renderElementToolTip(T selection, int x, int y) {
		selectGrid.startToolTipRender(gridID, selection, x, y);
	}

	public void preRender() {
		selectGrid.preRender(gridID);
	}

	public void postRender() {
		selectGrid.postRender(gridID);
	}

}