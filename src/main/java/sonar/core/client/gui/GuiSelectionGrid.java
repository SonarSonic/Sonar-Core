package sonar.core.client.gui;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.inventory.Container;
import sonar.core.client.gui.widgets.SonarScroller;

public abstract class GuiSelectionGrid<T> extends GuiSonar {

	public int yPos = 32;
	public int xPos = 13;
	public int eWidth = 18;
	public int eHeight = 18;
	public int gWidth = 12;
	public int gHeight = 7;
	public SonarScroller scroller;
	public SelectionGrid grid;

	public GuiSelectionGrid(Container container) {
		super(container);
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.mc.player.openContainer = this.inventorySlots;
		this.xSize = 256;
		this.ySize = 256;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		scroller = new SonarScroller(this.guiLeft + xPos + (eWidth * gWidth) + 3, this.guiTop + yPos - 1, (eHeight * gHeight) + 2, 10);
		grid = new SelectionGrid(this, 0, xPos, yPos, eWidth, eHeight, gWidth, gHeight);
	}

	@Override
	public void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if (button == 0 || button == 1) {
			grid.mouseClicked(this, x, y, button);
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		renderStrings(x, y);
		grid.renderGrid(this, x, y);
	}

	public void startToolTipRender(T selection, int x, int y) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		this.renderElementToolTip(selection, x, y);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		scroller.handleMouse(grid);
	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		grid.setList(Lists.newArrayList(this.getGridList()));
		scroller.drawScreen(x, y, grid.isScrollable());
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		/*
		drawRect(guiLeft + xPos - 1, guiTop + yPos - 1, guiLeft + xPos + (eWidth * gWidth) - 1, guiTop + yPos + (eHeight * gHeight) - 1, 0);
		drawRect(guiLeft + xPos, guiTop + yPos, guiLeft + xPos + (eWidth * gWidth) - 2, guiTop + yPos + (eHeight * gHeight) - 2, 1);

		drawRect(guiLeft + 12, guiTop + 170, guiLeft + xSize - 9, guiTop + 252, 0);
		drawRect(guiLeft + 13, guiTop + 171, guiLeft + xSize - 10, guiTop + 251, 1);
		*/
		//RenderHelper.restoreBlendState();
	}

	public abstract void onGridClicked(T element, int pos, int button, boolean empty);

	public abstract void renderGridElement(T element, int x, int y, int slot);

	public abstract void renderStrings(int x, int y);

	public abstract void renderElementToolTip(T element, int x, int y);

	public abstract List<T> getGridList();

	public void preRender() {}

	public void postRender() {}

	public static class SelectionGrid<T> extends GuiGridElement<T> {
		public GuiSelectionGrid<T> selectGrid;

		public SelectionGrid(GuiSelectionGrid<T> selectGrid, int gridID, int yPos, int xPos, int eWidth, int eHeight, int gWidth, int gHeight) {
			super(gridID, yPos, xPos, eWidth, eHeight, gWidth, gHeight);
			this.selectGrid = selectGrid;
		}

		@Override
		public float getCurrentScroll() {
			return selectGrid.scroller.getCurrentScroll();
		}

		@Override
		public void onGridClicked(T selection, int pos, int button, boolean empty) {
			selectGrid.onGridClicked(selection, pos, button, empty);
		}

		@Override
		public void renderGridElement(T selection, int x, int y, int slot) {
			selectGrid.renderGridElement(selection, x, y, slot);
		}

		@Override
		public void renderElementToolTip(T selection, int x, int y) {
			selectGrid.startToolTipRender(selection, x, y);
		}

		public void preRender() {
			selectGrid.preRender();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		}

		public void postRender() {
			selectGrid.postRender();

		}
	}

}