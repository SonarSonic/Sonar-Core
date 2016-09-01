package sonar.core.helpers;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import sonar.core.client.gui.GuiSonar;
import sonar.core.client.renderers.TransformationMatrix;
import sonar.core.client.renderers.Vector;
import sonar.core.utils.IWorldPosition;

public class RenderHelper {

	public static final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
	public static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
	public static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
	public static int src = -1, dst = -1;
	protected RenderManager renderManager;

	// used with EnumFacing
	public static double[][] offsetMatrix = new double[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 1, 0, -1 }, { 1, 0, 0 }, { 0, 0, -1 } };

	public static void saveBlendState() {
		src = GL11.glGetInteger(GL11.GL_BLEND_SRC);
		dst = GL11.glGetInteger(GL11.GL_BLEND_DST);
	}

	public static void restoreBlendState() {
		if (src != -1 && dst != -1)
			GL11.glBlendFunc(src, dst);
	}

	public static int setMetaData(TileEntity tileentity) {
		int i;
		if (tileentity.getWorld() == null) {
			i = 0;
		} else {
			Block block = tileentity.getBlockType();
			i = tileentity.getBlockMetadata();
			if ((block != null) && (i == 0)) {
				i = tileentity.getBlockMetadata();
			}
		}

		return i;
	}

	public static void beginRender(double x, double y, double z, int meta, String texture) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(texture));
		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 180.0F, 0.0F, 1.0F);
		int j = 0;
		switch (meta) {
		case 2:
			j = 180;
			break;
		case 3:
			j = 0;
			break;
		case 4:
			j = 90;
			break;
		case 5:
			j = 270;
			break;
		}
		GL11.glRotatef(j, 0.0F, 1.0F, 0.0F);
		GL11.glRotated(-0.625, 0, 1, 0);
	}

	public static void finishRender() {
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	/* public static EnumFacing getHorizontal(EnumFacing forward) { if (forward == EnumFacing.NORTH) { return EnumFacing.EAST; } if (forward == EnumFacing.EAST) { return EnumFacing.SOUTH; } if (forward == EnumFacing.SOUTH) { return EnumFacing.WEST; } if (forward == EnumFacing.WEST) { return EnumFacing.NORTH; } return null;
	 * 
	 * } */
	public static void drawRect(float left, float top, float right, float bottom, int color) {
		if (left < right) {
			float i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			float j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(f, f1, f2, f3);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
		vertexbuffer.pos((double) left, (double) bottom, 0.0D).endVertex();
		vertexbuffer.pos((double) right, (double) bottom, 0.0D).endVertex();
		vertexbuffer.pos((double) right, (double) top, 0.0D).endVertex();
		vertexbuffer.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	// 1.9.4 additions

	public static void addVertexWithUV(VertexBuffer vertexbuffer, double x, double y, double z, double u, double v) {
		vertexbuffer.pos(x, y, z).tex(u, v).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
	}

	public static void renderItem(GuiSonar screen, int x, int y, ItemStack stack) {
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		screen.setZLevel(200.0F);
		itemRender.zLevel = 200.0F;
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		// itemRender.renderItemOverlayIntoGUI(getFontFromStack(stack), stack, x, y, "");
		screen.setZLevel(0.0F);
		itemRender.zLevel = 0.0F;
		GlStateManager.translate(0.0F, 0.0F, -32.0F);
	}

	// public static void renderItem(GuiSonar screen, int x, int y, ItemStack stack) {

	// }

	public static void renderStoredItemStackOverlay(ItemStack stack, long stored, int x, int y, String string) {
		if (stack != null) {
			FontRenderer font = getFontFromStack(stack);
			stack.stackSize = 1;
			if (stored > 0 || string != null) {
				String s1 = string == null ? FontHelper.formatStackSize(stored) : string;

				final float scaleFactor = 0.5F;
				final float inverseScaleFactor = 1.0f / scaleFactor;
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableBlend();
				GL11.glPushMatrix();
				GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
				final int X = (int) (((float) x + 15.0f - font.getStringWidth(s1) * scaleFactor) * inverseScaleFactor);
				final int Y = (int) (((float) y + 15.0f - 7.0f * scaleFactor) * inverseScaleFactor);
				font.drawStringWithShadow(s1, X, Y, 16777215);
				GL11.glPopMatrix();
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				GlStateManager.enableBlend();
			}
		}
	}

	public static FontRenderer getFontFromStack(ItemStack stack) {
		FontRenderer rend;
		return stack != null ? ((rend = stack.getItem().getFontRenderer(stack)) == null ? fontRenderer : rend) : fontRenderer;
	}

	public static void drawTexturedModalRect(float minX, float minY, float maxY, float width, float height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		float x = minX;
		float y = minY;
		float textureX = 0;
		float textureY = 0;
		double widthnew = (0 + (width * (2)));
		double heightnew = (0 + ((height) * (2)));

		vertexbuffer.pos((double) (x + 0), (double) (y + height), (double) 0).tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + height) * f1)).endVertex();
		vertexbuffer.pos((double) (x + width), (double) (y + height), (double) 0).tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + height) * f1)).endVertex();
		vertexbuffer.pos((double) (x + width), (double) (y + 0), (double) 0).tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
		vertexbuffer.pos((double) (x + 0), (double) (y + 0), (double) 0).tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
		tessellator.draw();

		/* Tessellator tessellator = Tessellator.getInstance(); VertexBuffer vertex = tessellator.getBuffer(); vertex.begin(7, DefaultVertexFormats.POSITION_TEX); double widthnew = (0 + (width * (2))); double heightnew = (0 + ((height) * (2))); addVertexWithUV(vertex, (minX + 0), maxY / 2, 0, 0, heightnew); addVertexWithUV(vertex, (minX + width), maxY / 2, 0, widthnew, heightnew); addVertexWithUV(vertex, (minX + width), (minY + 0), 0, widthnew, 0); addVertexWithUV(vertex, (minX + 0), (minY + 0), 0, 0, 0); tessellator.draw();
		 * 
		 * float f = 1.0F / textureWidth; float f1 = 1.0F / textureHeight; Tessellator tessellator = Tessellator.getInstance(); VertexBuffer vertexbuffer = tessellator.getBuffer(); vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX); vertexbuffer.pos((double)x, (double)(y + height), 0.0D).tex((double)(u * f), (double)((v + (float)height) * f1)).endVertex(); vertexbuffer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).endVertex(); vertexbuffer.pos((double)(x + width), (double)y, 0.0D).tex((double)((u + (float)width) * f), (double)(v * f1)).endVertex(); vertexbuffer.pos((double)x, (double)y, 0.0D).tex((double)(u * f), (double)(v * f1)).endVertex(); tessellator.draw(); */
		// Gui.drawModalRectWithCustomSizedTexture(0, 0, width, height, 16, 16, minX, minY);
	}

	public static void renderItem(ItemStack stack, World world) {
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.translate(-6.4F, -6.5F, -0.245F);
		GlStateManager.scale(0.8, 0.8, 0.01);

		renderItem(stack, ItemCameraTransforms.TransformType.NONE);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();

		/* net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting(); itemRender.renderItemAndEffectIntoGUI(stack, 0, 0); itemRender.renderItemOverlayIntoGUI(getFontFromStack(stack), stack, 0, 0, ""); net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting(); */
	}

	public static IBakedModel getBakedModel(IBlockState state) {
		BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockModelShapes models = renderer.getBlockModelShapes();
		return models.getModelForState(state);
	}

	public static void renderItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, 1.0F, -1.0F);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		IBakedModel ibakedmodel = itemRender.getItemModelMesher().getItemModel(itemStack);
		if (ibakedmodel.isGui3d()) {
			GlStateManager.scale(40 / 64F, 40 / 64F, 40 / 64F);
			GlStateManager.rotate(210.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.enableLighting();
		} else {
			// GlStateManager.scale(64.0F, 64.0F, 64.0F);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableLighting();
		}
		itemRender.renderItem(itemStack, transformType);
	}

	public static void renderItemIntoGUI(ItemStack stack, int x, int y) {
		renderItemModelIntoGUI(stack, x, y, itemRender.getItemModelWithOverrides(stack, (World) null, (EntityLivingBase) null));
	}

	protected static void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel) {
		GlStateManager.pushMatrix();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		// GL11.glMatrixMode(GL11.GL_MODELVIEW);
		// GlStateManager.enableNormalize();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		setupGuiTransform(x, y, bakedmodel.isGui3d());
		// GL11.glVertex3d(1, 1, 0.04);
		GL11.glScaled(1, 1, 0.04);
		bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
		itemRender.renderItem(stack, bakedmodel);
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		// GlStateManager.disableNormalize();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}

	private static void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d) {
		// GlStateManager.translate((float) xPosition, (float) yPosition, 100.0F + itemRender.zLevel);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(16.0F, 16.0F, 16.0F);

		if (isGui3d) {
			// GlStateManager.enableLighting();
		} else {
			GlStateManager.disableLighting();
		}
	}

	/** all credit to InfinityRaider */
	public static List<BakedQuad> transformQuads(List<BakedQuad> quads, TransformationMatrix matrix) {
		List<BakedQuad> newQuads = new ArrayList<>();
		for (BakedQuad quad : quads) {
			VertexFormat format = quad.getFormat();
			float[][][] vertexData = new float[4][format.getElementCount()][];
			// unpack and transform vertex data
			for (int v = 0; v < 4; v++) {
				for (int e = 0; e < format.getElementCount(); e++) {
					float[] data = new float[4];
					LightUtil.unpack(quad.getVertexData(), data, format, v, e);
					vertexData[v][e] = transformUnpackedVertexDataElement(matrix, format.getElement(e).getUsage(), data);
				}
			}
			// create new quad with the transformed vertex data
			newQuads.add(new UnpackedBakedQuad(vertexData, quad.getTintIndex(), EnumFacing.UP, quad.getSprite(), true, format));
		}
		return newQuads;
	}

	/** all credit to InfinityRaider */
	public static float[] transformUnpackedVertexDataElement(TransformationMatrix matrix, VertexFormatElement.EnumUsage type, float[] data) {
		switch (type) {
		case POSITION:
		case NORMAL:
			double[] pos = matrix.transform(data[0], data[1], data[2]);
			data[0] = (float) pos[0];
			data[1] = (float) pos[1];
			data[2] = (float) pos[2];
			break;
		case COLOR:
			/* data[0] = getRedValueFloat(); data[1] = getGreenValueFloat(); data[2] = getBlueValueFloat(); data[3] = getAlphaValueFloat(); */
			break;
		default:
			break;
		}
		return data;
	}

	public static Vector getOffsetForFace(EnumFacing face) {
		switch (face) {
		case DOWN:
			return new Vector(0, 0, 0);
		case EAST:
			return new Vector(0, 0, 1);
		case NORTH:
			return new Vector(1, 0, 1);
		case SOUTH:
			return new Vector(0, 0, 0);
		case UP:
			return new Vector(0, 0, 1);
		case WEST:
			return new Vector(1, 0, 0);
		default:
			return new Vector(0, 0, 0);

		}

	}

	/**compensates for the Entity View and applies the right translation. it pushes the matrix once !*/
	public static void offsetRendering(BlockPos pos, double partialTicks) {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();
		double vX = view.lastTickPosX + (view.posX - view.lastTickPosX) * (double) partialTicks;
		double vY = view.lastTickPosY + (view.posY - view.lastTickPosY) * (double) partialTicks;
		double vZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * (double) partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate(pos.getX() - vX, pos.getY() - vY, pos.getZ() - vZ);
	}

	public static void drawBoundingBox(AxisAlignedBB box, BlockPos pos, float partialTicks, float r, float g, float b, float alpha) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(5.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		if (Minecraft.getMinecraft().theWorld.getWorldBorder().contains(pos)) {
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
			double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
			GlStateManager.translate(-d0 + pos.getX(), -d1 + pos.getY(), -d2 + pos.getZ());
			drawBoundingBox(box.expandXyz(0.0020000000949949026D), r, g, b, alpha);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawBoundingBox(AxisAlignedBB box, float r, float g, float b, float alpha) {
		drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, alpha);
	}

	public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		drawBoundingBox(vertexbuffer, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, alpha);
		tessellator.draw();
	}

	public static void drawBoundingBox(VertexBuffer buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float alpha) {
		buffer.pos(minX, minY, minZ).color(r, g, b, 0.0F).endVertex();
		buffer.pos(minX, minY, minZ).color(r, g, b, alpha).endVertex();
		buffer.pos(maxX, minY, minZ).color(r, g, b, alpha).endVertex();
		buffer.pos(maxX, minY, maxZ).color(r, g, b, alpha).endVertex();
		buffer.pos(minX, minY, maxZ).color(r, g, b, alpha).endVertex();
		buffer.pos(minX, minY, minZ).color(r, g, b, alpha).endVertex();
		buffer.pos(minX, maxY, minZ).color(r, g, b, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).color(r, g, b, alpha).endVertex();
		buffer.pos(maxX, maxY, maxZ).color(r, g, b, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).color(r, g, b, alpha).endVertex();
		buffer.pos(minX, maxY, minZ).color(r, g, b, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).color(r, g, b, 0.0F).endVertex();
		buffer.pos(minX, minY, maxZ).color(r, g, b, alpha).endVertex();
		buffer.pos(maxX, maxY, maxZ).color(r, g, b, 0.0F).endVertex();
		buffer.pos(maxX, minY, maxZ).color(r, g, b, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).color(r, g, b, 0.0F).endVertex();
		buffer.pos(maxX, minY, minZ).color(r, g, b, alpha).endVertex();
		buffer.pos(maxX, minY, minZ).color(r, g, b, 0.0F).endVertex();
	}

}