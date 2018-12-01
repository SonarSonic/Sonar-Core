package sonar.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiSonar extends GuiContainer implements IGuiOrigin {

	protected List<SonarTextField> fieldList = new ArrayList<>();
	private boolean shouldReset = false;

	///the gui which opened this one, generally null.
	public Object origin;

	public GuiSonar(Container container) {
		super(container);
	}

	public abstract ResourceLocation getBackground();

	//// RENDER METHODS \\\\

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (shouldReset) {
			doReset();
		}
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		fieldList.forEach(SonarTextField::drawTextBox);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float v, int i, int i1) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getBackground());
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void renderHoveredToolTip(int x, int y) {
		super.renderHoveredToolTip(x, y);
		for (GuiButton guibutton : this.buttonList) {
			if (guibutton.isMouseOver()) {
				guibutton.drawButtonForegroundLayer(x, y);
				break;
			}
		}
	}

	//// STANDARD EVENTS \\\\

	@Override
	public void mouseClicked(int i, int j, int k) throws IOException {
		super.mouseClicked(i, j, k);
		for (SonarTextField field : fieldList) {
			boolean focused = field.mouseClicked(i - guiLeft, j - guiTop, k);
			if (focused) {
				onTextFieldFocused(field);
			}
		}
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
		if (isCloseKey(i) && origin != null) {
			FMLCommonHandler.instance().showGuiScreen(origin);
			return;
		}
		super.keyTyped(c, i);
	}


	public void onTextFieldChanged(SonarTextField field) {}

	public void onTextFieldFocused(SonarTextField field) {}

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

	public SonarTextField getFocusedField() {
		for (SonarTextField f : fieldList) {
			if (f.isFocused()) {
				return f;
			}
		}
		return null;
	}

	public void bindTexture(ResourceLocation resource) {
		mc.getTextureManager().bindTexture(resource);
	}

	public void setOrigin(Object origin) {
		this.origin = origin;
	}


	public void setZLevel(float zLevel) {
		this.zLevel = zLevel;
	}

	public boolean isCloseKey(int keyCode) {
		return keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode);
	}

	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}


	//// RENDER METHODS \\\\

	public void drawSonarCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
		drawHoveringText(tabName, mouseX, mouseY);
	}

	public void drawSonarCreativeTabHoveringText(List<String> text, int mouseX, int mouseY) {
		drawHoveringText(text, mouseX, mouseY);
	}

	public void startNormalItemStackRender() {
		GlStateManager.enableDepth();
		RenderHelper.enableGUIStandardItemLighting();
		sonar.core.helpers.RenderHelper.saveBlendState();
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
	}

	public void drawNormalItemStack(ItemStack stack, int x, int y) {
		startNormalItemStackRender();
		net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = fontRenderer;
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, "");
		finishNormalItemStackRender();
	}

	public void finishNormalItemStackRender() {
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;

		sonar.core.helpers.RenderHelper.restoreBlendState();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableDepth();
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
		drawHoveringText(list, x - guiLeft, y - guiTop, font == null ? fontRenderer : font);
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
		BufferBuilder vertexbuffer = tessellator.getBuffer();
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
		GlStateManager.color(1, 1, 1, 1);
		sonar.core.helpers.RenderHelper.restoreBlendState();
	}


}
