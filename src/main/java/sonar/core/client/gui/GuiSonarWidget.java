package sonar.core.client.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import sonar.core.SonarCore;
import sonar.core.client.gui.widgets.IWidget;
import sonar.core.helpers.FontHelper;
import sonar.core.network.PacketByteBufServer;
import sonar.core.network.utils.IByteBufTile;

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

	public GuiSonarWidget(Container container, TileEntity entity) {
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
