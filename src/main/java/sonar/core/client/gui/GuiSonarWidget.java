package sonar.core.client.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import sonar.core.client.gui.widgets.IWidget;
import sonar.core.utils.IWorldPosition;

public abstract class GuiSonarWidget extends GuiSonar {

	private LinkedHashMap<WidgetPos, IWidget> widgets = new LinkedHashMap<WidgetPos, IWidget>();

	public class WidgetPos {
		public int x, y;

		public WidgetPos(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public void addWidget(int x, int y, IWidget widget) {
		widget.setGui(this);
		widget.setPosition(new WidgetPos(x, y));
		widgets.put(new WidgetPos(x, y), widget);
	}

	public GuiSonarWidget(Container container, IWorldPosition entity) {
		super(container, entity);
	}

	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		for (Entry<WidgetPos, IWidget> widget : widgets.entrySet()) {
			GL11.glPushMatrix();
			GL11.glTranslated(widget.getKey().x, widget.getKey().y, 0);
			widget.getValue().drawForegroundLayer(x, y);
			GL11.glPopMatrix();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float val, int x, int y) {
		super.drawGuiContainerBackgroundLayer(val, x, y);
		for (Entry<WidgetPos, IWidget> widget : widgets.entrySet()) {
			GL11.glPushMatrix();
			GL11.glTranslated(widget.getKey().x, widget.getKey().y, 0);
			widget.getValue().drawBackgroundLayer(val, x, y);
			GL11.glPopMatrix();
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (Entry<WidgetPos, IWidget> widget : widgets.entrySet()) {
			Rectangle rect = widget.getValue().getSizing();
			int x = guiLeft + mouseX;
			int y = guiTop + mouseY;
			if (x < guiLeft + rect.getWidth() && guiLeft + widget.getKey().x > x && y < guiTop + rect.getHeight() && guiTop + widget.getKey().y > y) {
				widget.getValue().setFocused(true);
				widget.getValue().onClicked(x, y, mouseButton);
			} else {
				widget.getValue().setFocused(false);
			}
		}
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		for (Entry<WidgetPos, IWidget> widget : widgets.entrySet()) {
			widget.getValue().handleMouseInput();
		}
	}

	public void keyTyped(char c, int i) throws IOException {
		super.keyTyped(c, i);
		for (Entry<WidgetPos, IWidget> widget : widgets.entrySet()) {
			if (widget.getValue().isFocused()) {
				widget.getValue().keyTyped(c, i);
				break;
			}
		}
	}
}
