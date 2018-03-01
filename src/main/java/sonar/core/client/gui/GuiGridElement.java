package sonar.core.client.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import sonar.core.utils.Pair;

public abstract class GuiGridElement<T> {

	public List<T> gridList = new ArrayList<>();
	public int yPos, xPos;
	public int eWidth, eHeight;
	public int gWidth, gHeight;
	public int gridID;

	public GuiGridElement(int gridID, int xPos, int yPos, int eWidth, int eHeight, int gWidth, int gHeight) {
		this.gridID = gridID;
		this.xPos = xPos;
		this.yPos = yPos;
		this.eWidth = eWidth;
		this.eHeight = eHeight;
		this.gWidth = gWidth;
		this.gHeight = gHeight;
	}

	public abstract float getCurrentScroll();

	public abstract void onGridClicked(T selection, int pos, int button, boolean empty);

	public abstract void renderGridElement(T selection, int x, int y, int slot);

	public abstract void renderElementToolTip(T selection, int x, int y);

	public GuiGridElement<T> setList(List<T> gridList) {
		this.gridList = gridList;
		return this;
	}

	public void preRender() {}

	public void postRender() {}

	public List<T> getGridList() {
		return gridList;
	}

	public int getGridSize() {
		return gridList.size();
	}

	public boolean isScrollable() {
		return getGridSize() > gWidth * gHeight;
	}

	public void renderGrid(GuiSonar gui, int x, int y) {
		if (gridList.isEmpty()) {
			return;
		}
		int start = (int) (gridList.size() / gWidth * getCurrentScroll());
		int i = start * gWidth;
		int finish = Math.min(i + gWidth * gHeight, gridList.size());
		int X = (x - gui.getGuiLeft() - xPos) / eWidth;
		int Y = (y - gui.getGuiTop() - yPos) / eHeight;

		preRender();
		for (int yPos = 0; yPos < gHeight; yPos++) {
			for (int xPos = 0; xPos < gWidth; xPos++) {
				if (i < finish) {
					T selection = gridList.get(i);
					if (selection != null) {
						renderGridElement(selection, xPos, yPos, i);
					}
				}
				i++;
			}
		}
		postRender();

		if (x - gui.getGuiLeft() >= xPos && x - gui.getGuiLeft() <= xPos + gWidth * eWidth && y - gui.getGuiTop() >= yPos && y - gui.getGuiTop() <= yPos + gHeight * eHeight) {
			int pos = start * gWidth + X + Y * gWidth;
			if (pos < gridList.size()) {
				renderElementToolTip(gridList.get(pos), x - gui.getGuiLeft(), y - gui.getGuiTop());
			}
		}

	}

	public @Nullable Pair<T, Integer> getElementHovered(GuiSonar gui, int x, int y) {
		List<T> list = gridList;
		int start = (int) (gridList.size() / gWidth * getCurrentScroll());
		int X = (x - gui.getGuiLeft() - xPos) / eWidth;
		int Y = (y - gui.getGuiTop() - yPos) / eHeight;
		if (x - gui.getGuiLeft() >= xPos && x - gui.getGuiLeft() <= xPos + gWidth * eWidth && y - gui.getGuiTop() >= yPos && y - gui.getGuiTop() <= yPos + gHeight * eHeight) {
			int i = start * gWidth + gWidth * Y + X;
			if (i < gridList.size()) {
				T e = gridList.get(i);
				if (e != null) {
					return new Pair(e, i);
				}
			}
			return new Pair(null, -1);
		}
		return new Pair(null, -2);
	}

	public void mouseClicked(GuiSonar gui, int x, int y, int button) {
		Pair<T, Integer> e = getElementHovered(gui, x, y);
		if (e.b != -2)
			onGridClicked(e.a, e.b, button, e.a == null);
	}
}