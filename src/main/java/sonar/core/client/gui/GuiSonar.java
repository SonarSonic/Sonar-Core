package sonar.core.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.machines.IPausable;
import sonar.core.api.machines.IProcessMachine;
import sonar.core.helpers.FontHelper;
import sonar.core.upgrades.UpgradeInventory;

public abstract class GuiSonar extends GuiContainer {

	protected List<SonarTextField> fieldList = new ArrayList<>();
	public boolean shouldReset = false;

	public GuiSonar(Container container) {
		super(container);
	}

	/** override for guis which have no entity location */
	public void onButtonClicked(int i) {}

	public abstract ResourceLocation getBackground();

	public void reset() {
		shouldReset = true;
	}

	public void doReset() {
		this.buttonList.clear();
		this.fieldList.clear();
		this.initGui();
		shouldReset = false;
	}

	public void initButtons() {
		this.buttonList.clear();
	}

	// public void initGui(boolean pause) {}

	public void setZLevel(float zLevel) {
		this.zLevel = zLevel;
	}

	public SonarTextField getFocusedField() {
		for (SonarTextField f : fieldList) {
			if (f.isFocused()) {
				return f;
			}
		}
		return null;

	}

	public void drawNormalToolTip(ItemStack stack, int x, int y) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		this.renderToolTip(stack, x - guiLeft, y - guiTop);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
	}

	public void drawSpecialToolTip(List<String> list, int x, int y, FontRenderer font) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		drawHoveringText(list, x - guiLeft, y - guiTop, font == null ? fontRendererObj : font);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
	}

	public static void drawTransparentRect(int left, int top, int right, int bottom, int color) {
		if (left < right) {
			int i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			int j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		sonar.core.helpers.RenderHelper.saveBlendState();
		GlStateManager.enableBlend();
		GlStateManager.color(f, f1, f2, f3);
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
		vertexbuffer.pos((double) left, (double) bottom, 0.0D).endVertex();
		vertexbuffer.pos((double) right, (double) bottom, 0.0D).endVertex();
		vertexbuffer.pos((double) right, (double) top, 0.0D).endVertex();
		vertexbuffer.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		sonar.core.helpers.RenderHelper.restoreBlendState();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (shouldReset) {
			doReset();
		}
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		//this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		fieldList.forEach(SonarTextField::drawTextBox);
		RenderHelper.disableStandardItemLighting();

		for (GuiButton guibutton : this.buttonList) {
			if (guibutton.isMouseOver()) {
				guibutton.drawButtonForegroundLayer(x - this.guiLeft, y - this.guiTop);
				break;
			}
		}
		RenderHelper.enableGUIStandardItemLighting();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float v, int i, int i1) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getBackground());
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	public void bindTexture(ResourceLocation resource) {
		mc.getTextureManager().bindTexture(resource);
	}

	public void drawSonarCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
		drawCreativeTabHoveringText(tabName, mouseX, mouseY);
	}

	@Override
	protected void keyTyped(char c, int i) throws IOException {
		for (SonarTextField field : fieldList) {
			if (field.isFocused()) {
				if (c == 13 || c == 27) {
					field.setFocused(false);
				} else {
					field.textboxKeyTyped(c, i);
					onTextFieldChanged(field);
				}
				return;
			}
		}
		super.keyTyped(c, i);
	}

	public boolean isCloseKey(int keyCode) {
		return keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode);
	}

	public void onTextFieldChanged(SonarTextField field) {}

	@Override
	public void mouseClicked(int i, int j, int k) throws IOException {
		super.mouseClicked(i, j, k);
		fieldList.forEach(field -> field.mouseClicked(i - guiLeft, j - guiTop, k));
	}
	
	//@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	//@Override
	public int getGuiTop() {
		return guiTop;
	}

	@SideOnly(Side.CLIENT)
	public static class PauseButton extends SonarButtons.ImageButton {

		boolean paused;
		public int id;
		public IPausable machine;
		public GuiSonarTile gui;

		public PauseButton(GuiSonarTile gui, IPausable machine, int id, int x, int y, boolean paused) {
			super(id, x, y, new ResourceLocation("Calculator:textures/gui/buttons/buttons.png"), paused ? 51 : 34, 0, 16, 16);
			this.gui = gui;
			this.paused = paused;
			this.id = id;
			this.machine = machine;
		}

		@Override
		public void drawButtonForegroundLayer(int x, int y) {
			ArrayList<String> list = new ArrayList<>();
			list.add(TextFormatting.BLUE + "" + TextFormatting.UNDERLINE + (paused ? FontHelper.translate("buttons.resume") : FontHelper.translate("buttons.pause")));
			if (machine instanceof IProcessMachine) {
				list.add("Current: " + (int) ((double) ((IProcessMachine) machine).getCurrentProcessTime() / ((IProcessMachine) machine).getProcessTime() * 100) + " %");
			}
			gui.drawHoveringText(list, x, y, gui.fontRendererObj);
		}

		@Override
		public void onClicked() {
			gui.onButtonClicked(id);
			// SonarCore.network.sendToServer(new PacketByteBuf((IByteBufTile) gui.entity, gui.entity.getCoords().getBlockPos(), id));
			gui.buttonList.clear();
			gui.initGui();
		}
	}

	@SideOnly(Side.CLIENT)
	public class CircuitButton extends SonarButtons.ImageButton {
		public int id;
		public UpgradeInventory upgrades;
		public GuiSonarTile gui;

		public CircuitButton(GuiSonarTile gui, UpgradeInventory upgrades, int id, int x, int y) {
			super(id, x, y, new ResourceLocation("Calculator:textures/gui/buttons/buttons.png"), 0, 0, 16, 16);
			this.gui = gui;
			this.upgrades = upgrades;
			this.id = id;
		}

		@Override
		public void drawButtonForegroundLayer(int x, int y) {
			ArrayList<String> list = new ArrayList<>();
			list.add(TextFormatting.BLUE + "" + TextFormatting.UNDERLINE + FontHelper.translate("buttons.circuits"));
			for (Entry<String, Integer> entry : upgrades.getInstalledUpgrades().entrySet()) {
				int max = upgrades.maxUpgrades.get(entry.getKey());
				list.add(entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().toLowerCase().substring(1) + ": " + entry.getValue() + '/' + max);
			}
			drawHoveringText(list, x, y, fontRendererObj);
		}

		@Override
		public void onClicked() {
			gui.onButtonClicked(id);
			// SonarCore.network.sendToServer(new PacketByteBuf((IByteBufTile) entity, entity.getCoords().getBlockPos(), id));
		}
	}
}
