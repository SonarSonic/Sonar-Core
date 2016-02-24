package sonar.core.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class SonarButtons {

	@SideOnly(Side.CLIENT)
	public static class SonarButton extends GuiButton {

		public boolean isButtonDown = false;

		public SonarButton(int id, int x, int y, int textureX, int textureY, String display) {
			super(id, x, y, textureX, textureY, display);

		}

		public void onClicked() {
		}

		public boolean mousePressed(Minecraft minecraft, int x, int y) {
			isButtonDown = this.enabled && this.visible && x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
			return isButtonDown;
		}

		public void mouseReleased(int x, int y) {
			isButtonDown = false;
		}

	}
	
	public static class HoverButton extends SonarButton{

		public HoverButton(int id, int x, int y, int textureX, int textureY, String display) {
			super(id, x, y, textureX, textureY, display);
		}
		
		public String getHoverText(){
			return "";
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static abstract class ImageButton extends SonarButton {
		private final ResourceLocation texture;
		private final int textureX;
		private final int textureY;
		private final int sizeX, sizeY;
		private boolean bool;

		protected ImageButton(int id, int x, int y, ResourceLocation texture, int textureX, int textureY, int sizeX, int sizeY) {
			super(id, x, y, sizeX, sizeY, "");
			this.texture = texture;
			this.textureX = textureX;
			this.textureY = textureY;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
		}

		/**
		 * Draws this button to the screen.
		 */
		public void drawButton(Minecraft mc, int x, int y) {
			if (this.visible) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
				short short1 = 219;
				int k = 0;

				if (!this.enabled) {
					k += this.width * 2;
				} else if (this.bool) {
					k += this.width * 1;
				} else if (this.field_146123_n) {
					k += this.width * 3;
				}

				mc.getTextureManager().bindTexture(texture);

				this.drawTexturedModalRect(this.xPosition, this.yPosition, this.textureX, this.textureY, sizeX + 1, sizeY + 1);
			}
		}

	}

	@SideOnly(Side.CLIENT)
	public static abstract class AnimatedButton extends SonarButton {
		private final ResourceLocation texture;
		private final int sizeX, sizeY;
		private boolean bool;

		protected AnimatedButton(int id, int x, int y, ResourceLocation texture, int sizeX, int sizeY) {
			super(id, x, y, sizeX, sizeY, "");
			this.texture = texture;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
		}

		public abstract int getTextureX();

		public abstract int getTextureY();

		public void drawButton(Minecraft mc, int x, int y) {
			if (this.visible) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
				short short1 = 219;
				int k = 0;

				if (!this.enabled) {
					k += this.width * 2;
				} else if (this.bool) {
					k += this.width * 1;
				} else if (this.field_146123_n) {
					k += this.width * 3;
				}

				mc.getTextureManager().bindTexture(texture);

				this.drawTexturedModalRect(this.xPosition, this.yPosition, getTextureX(), getTextureY(), sizeX + 1, sizeY + 1);
			}
		}

	}
}
