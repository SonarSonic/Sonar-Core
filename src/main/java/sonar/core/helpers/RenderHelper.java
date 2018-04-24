package sonar.core.helpers;

//import org.lwjgl.opengl.GL11;
import static net.minecraft.client.renderer.GlStateManager.alphaFunc;
import static net.minecraft.client.renderer.GlStateManager.blendFunc;
import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.depthMask;
import static net.minecraft.client.renderer.GlStateManager.disableAlpha;
import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.disableDepth;
import static net.minecraft.client.renderer.GlStateManager.disableLighting;
import static net.minecraft.client.renderer.GlStateManager.disableRescaleNormal;
import static net.minecraft.client.renderer.GlStateManager.disableTexture2D;
import static net.minecraft.client.renderer.GlStateManager.enableAlpha;
import static net.minecraft.client.renderer.GlStateManager.enableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableDepth;
import static net.minecraft.client.renderer.GlStateManager.enableLighting;
import static net.minecraft.client.renderer.GlStateManager.enableRescaleNormal;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static net.minecraft.client.renderer.GlStateManager.glLineWidth;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.rotate;
import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;
import static net.minecraft.client.renderer.GlStateManager.tryBlendFuncSeparate;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.client.config.GuiUtils;
import sonar.core.SonarCore;
import sonar.core.client.BlockModelsCache;
import sonar.core.client.gui.GuiSonar;
import sonar.core.client.renderers.TransformationMatrix;
import sonar.core.client.renderers.Vector;

public class RenderHelper {

	public static final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
	public static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	public static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
	private static boolean blendSaved = false;
	private static int BLEND_SRC = -1, BLEND_DST = -1, ALPHA_SRC = -1, ALPHA_DST = -1;
	protected RenderManager renderManager;

	// used with EnumFacing
	public static double[][] offsetMatrix = new double[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 1, 0, -1 }, { 1, 0, 0 }, { 0, 0, -1 } };

	public static void saveBlendState() {
		BLEND_SRC = GlStateManager.glGetInteger(GL11.GL_BLEND_SRC);
		BLEND_DST = GlStateManager.glGetInteger(GL11.GL_BLEND_DST);
		// ALPHA_SRC = GlStateManager.glGetInteger(GL11.GL_SRC_ALPHA);
		// ALPHA_DST = GlStateManager.glGetInteger(GL11.GL_DST_ALPHA);
		blendSaved = true;
	}

	public static void restoreBlendState() {
		if (blendSaved) {
			GlStateManager.blendFunc(BLEND_SRC, BLEND_DST);
			blendSaved = false;
		}
	}

	public static int setMetaData(TileEntity tileentity) {
		int i;
        tileentity.getWorld();
        Block block = tileentity.getBlockType();
        i = tileentity.getBlockMetadata();
        if (i == 0) {
            i = tileentity.getBlockMetadata();
        }

        return i;
	}

	public static void beginRender(double x, double y, double z, int meta, String texture) {
		pushMatrix();
		translate((float) x, (float) y, (float) z);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(texture));
		pushMatrix();
		rotate(180.0F, 180.0F, 0.0F, 1.0F);
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
		rotate(j, 0.0F, 1.0F, 0.0F);
		rotate(-0.625F, 0F, 1F, 0F);
	}

	public static void finishRender() {
		popMatrix();
		popMatrix();
	}

	/* public static EnumFacing getHorizontal(EnumFacing forward) { if (forward == EnumFacing.NORTH) { return EnumFacing.EAST; } if (forward == EnumFacing.EAST) { return EnumFacing.SOUTH; } if (forward == EnumFacing.SOUTH) { return EnumFacing.WEST; } if (forward == EnumFacing.WEST) { return EnumFacing.NORTH; } return null; } */

	// 1.9.4 additions

	public static void addVertexWithUV(BufferBuilder vertexbuffer, double x, double y, double z, double u, double v) {
		vertexbuffer.pos(x, y, z).tex(u, v).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
	}

	public static void renderItem(GuiSonar screen, int x, int y, ItemStack stack) {
		translate(0.0F, 0.0F, 32.0F);
		screen.setZLevel(200.0F);
		itemRender.zLevel = 200.0F;
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		// itemRender.renderItemOverlayIntoGUI(getFontFromStack(stack), stack, x, y, "");
		screen.setZLevel(0.0F);
		itemRender.zLevel = 0.0F;
		translate(0.0F, 0.0F, -32.0F);
	}

	// public static void renderItem(GuiSonar screen, int x, int y, ItemStack stack) {

	// }
	public static void renderStoredItemStackOverlay(ItemStack stack, long stored, int x, int y, String string, boolean depth) {
		renderStoredItemStackOverlay(stack, stored, x, y, 16777215, string, depth);
	}

	public static void renderStoredItemStackOverlay(ItemStack stack, long stored, int x, int y, int colour, String string, boolean depth) {
		if (stack != null) {
			FontRenderer font = getFontFromStack(stack);
			stack.setCount(1);
			if (stored > 0 || string != null) {
				String s1 = string == null ? FontHelper.formatStackSize(stored) : string;

				final float scaleFactor = 0.5F;
				final float inverseScaleFactor = 1.0f / scaleFactor;
				pushMatrix();
				// disableLighting();
				if (depth)
					disableDepth();

				scale(scaleFactor, scaleFactor, scaleFactor);
				final int X = (int) (((float) x + 15.0f - font.getStringWidth(s1) * scaleFactor) * inverseScaleFactor);
				final int Y = (int) (((float) y + 15.0f - 7.0f * scaleFactor) * inverseScaleFactor);
				font.drawStringWithShadow(s1, X, Y, colour);
				// enableLighting();

				if (depth)
					enableDepth();

				popMatrix();
			}
		}
	}

	public static FontRenderer getFontFromStack(ItemStack stack) {
		FontRenderer rend;
		return stack != null ? (rend = stack.getItem().getFontRenderer(stack)) == null ? fontRenderer : rend : fontRenderer;
	}

	public static int getTextFormattingColour(TextFormatting format) {
		if (!format.isColor()) {
			return 0;
		}
		return GuiUtils.colorCodes[format.getColorIndex()];
	}

	public static int getTextFormattingShadow(TextFormatting format) {
		if (!format.isColor()) {
			return 0;
		}
		return GuiUtils.colorCodes[16 + format.getColorIndex()];
	}

	public static void drawRect(float left, float top, float right, float bottom) {
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
		/*
		*/
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
		vertexbuffer.pos((double) left, (double) bottom, 0.0D).endVertex();
		vertexbuffer.pos((double) right, (double) bottom, 0.0D).endVertex();
		vertexbuffer.pos((double) right, (double) top, 0.0D).endVertex();
		vertexbuffer.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();
	}

	/** allows every value to be a double */
	public static void drawModalRectWithCustomSizedTexture(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight) {
		double f = 1.0 / textureWidth;
		double f1 = 1.0 / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + height, 0).tex(u * f, (v + height) * f1).endVertex();
		bufferbuilder.pos(x + width, y + height, 0.0D).tex((u + width) * f, (v + height) * f1).endVertex();
		bufferbuilder.pos(x + width, y, 0).tex((u + width) * f, v * f1).endVertex();
		bufferbuilder.pos(x, y, 0).tex(u * f, v * f1).endVertex();
		tessellator.draw();
	}

	public static void drawTexturedModalRect(float minX, float minY, float maxY, float width, float height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		float x = minX;
		float y = minY;
		float textureX = 0;
		float textureY = 0;
		double widthnew = 0 + width * 2;
		double heightnew = 0 + height * 2;

		vertexbuffer.pos((double) (x + 0), (double) (y + height), (double) 0).tex((double) ((textureX + 0) * f), (double) ((textureY + height) * f1)).endVertex();
		vertexbuffer.pos((double) (x + width), (double) (y + height), (double) 0).tex((double) ((textureX + width) * f), (double) ((textureY + height) * f1)).endVertex();
		vertexbuffer.pos((double) (x + width), (double) (y + 0), (double) 0).tex((double) ((textureX + width) * f), (double) ((textureY + 0) * f1)).endVertex();
		vertexbuffer.pos((double) (x + 0), (double) (y + 0), (double) 0).tex((double) ((textureX + 0) * f), (double) ((textureY + 0) * f1)).endVertex();
		tessellator.draw();

		/* Tessellator tessellator = Tessellator.getInstance(); BufferBuilder vertex = tessellator.getBuffer(); vertex.begin(7, DefaultVertexFormats.POSITION_TEX); double widthnew = (0 + (width * (2))); double heightnew = (0 + ((height) * (2))); addVertexWithUV(vertex, (minX + 0), maxY / 2, 0, 0, heightnew); addVertexWithUV(vertex, (minX + width), maxY / 2, 0, widthnew, heightnew); addVertexWithUV(vertex, (minX + width), (minY + 0), 0, widthnew, 0); addVertexWithUV(vertex, (minX + 0), (minY + 0), 0, 0, 0); tessellator.draw(); float f = 1.0F / textureWidth; float f1 = 1.0F / textureHeight; Tessellator tessellator = Tessellator.getInstance(); VertexBuffer vertexbuffer = tessellator.getBuffer(); vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX); vertexbuffer.pos((double)x, (double)(y + height), 0.0D).tex((double)(u * f), (double)((v + (float)height) * f1)).endVertex(); vertexbuffer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).endVertex(); vertexbuffer.pos((double)(x + width), (double)y, 0.0D).tex((double)((u + (float)width) * f), (double)(v * f1)).endVertex(); vertexbuffer.pos((double)x, (double)y, 0.0D).tex((double)(u * f), (double)(v * f1)).endVertex(); tessellator.draw(); */
		// Gui.drawModalRectWithCustomSizedTexture(0, 0, width, height, 16, 16, minX, minY);
	}

	public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
		float f = 1.0F / tileWidth;
		float f1 = 1.0F / tileHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double) x, (double) (y + height), 0.0D).tex((double) (u * f), (double) ((v + (float) vHeight) * f1)).endVertex();
		vertexbuffer.pos((double) (x + width), (double) (y + height), 0.0D).tex((double) ((u + (float) uWidth) * f), (double) ((v + (float) vHeight) * f1)).endVertex();
		vertexbuffer.pos((double) (x + width), (double) y, 0.0D).tex((double) ((u + (float) uWidth) * f), (double) (v * f1)).endVertex();
		vertexbuffer.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();
	}

	public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double) x, (double) (y + height), (double) 0).tex((double) ((float) textureX * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		vertexbuffer.pos((double) (x + width), (double) (y + height), (double) 0).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		vertexbuffer.pos((double) (x + width), (double) y, (double) 0).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) textureY * 0.00390625F)).endVertex();
		vertexbuffer.pos((double) x, (double) y, (double) 0).tex((double) ((float) textureX * 0.00390625F), (double) ((float) textureY * 0.00390625F)).endVertex();
		tessellator.draw();
	}

	public static void drawSizedIconWithoutColor(int x, int y, int width, int height, float zLevel) {
		pushMatrix();
		enableBlend();
		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		color(1F, 1F, 1F, 1F);
		scale(0.5D, 0.5D, 0.5D);
		translate(x, y, zLevel);
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		disableLighting();
		Tessellator tessellator = Tessellator.getInstance();
		tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
		tessellator.getBuffer().pos(x, y + height, zLevel).tex(0D, 1D).endVertex();
		tessellator.getBuffer().pos(x + width, y + height, zLevel).tex(1D, 1D).endVertex();
		tessellator.getBuffer().pos(x + width, y, zLevel).tex(1D, 0D).endVertex();
		tessellator.getBuffer().pos(x, y, zLevel).tex(0D, 0D).endVertex();
		tessellator.draw();
		enableLighting();
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		disableBlend();
		popMatrix();
	}

	public static void renderItem(ItemStack stack, World world) {
		pushMatrix();
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		translate(-6.4F, -6.5F, -0.245F);
		scale(0.8, 0.8, 0.01);

		renderItem(stack, ItemCameraTransforms.TransformType.NONE);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		popMatrix();

		/* net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting(); itemRender.renderItemAndEffectIntoGUI(stack, 0, 0); itemRender.renderItemOverlayIntoGUI(getFontFromStack(stack), stack, 0, 0, ""); net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting(); */
	}

	public static IBakedModel getBakedModel(IBlockState state) {
		BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockModelShapes models = renderer.getBlockModelShapes();
		return models.getModelForState(state);
	}

	public static void renderItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
		translate(8.0F, 8.0F, 0.0F);
		scale(1.0F, 1.0F, -1.0F);
		scale(0.5F, 0.5F, 0.5F);
		IBakedModel ibakedmodel = itemRender.getItemModelMesher().getItemModel(itemStack);
		if (ibakedmodel.isGui3d()) {
			scale(40 / 64F, 40 / 64F, 40 / 64F);
			rotate(210.0F, 1.0F, 0.0F, 0.0F);
			rotate(-135.0F, 0.0F, 1.0F, 0.0F);
			enableLighting();
		} else {
			// GlStateManager.scale(64.0F, 64.0F, 64.0F);
			rotate(180.0F, 1.0F, 0.0F, 0.0F);
			disableLighting();
		}
		itemRender.renderItem(itemStack, transformType);
	}

	public static void renderItemIntoGUI(ItemStack stack, int x, int y) {
		IBakedModel model = BlockModelsCache.INSTANCE.getOrLoadModel(stack);
		if (model != null) {
			renderItemModelIntoGUI(stack, x, y, model);
		}
	}

	public static void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel) {
		pushMatrix();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		enableRescaleNormal();
		enableAlpha();
		alphaFunc(516, 0.1F);
		enableBlend();

		blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		color(1.0F, 1.0F, 1.0F, 1.0F);
		setupGuiTransform(x, y, bakedmodel.isGui3d());
		GlStateManager.scale(1, 1, 0.04);
		bakedmodel = ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
		itemRender.renderItem(stack, bakedmodel);

		disableBlend();
		disableAlpha();
		disableRescaleNormal();
		disableLighting();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		popMatrix();
	}

	public static void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d) {
		// GlStateManager.translate((float) xPosition, (float) yPosition, 100.0F + itemRender.zLevel);
		translate(8.0F, 8.0F, 0.0F);
		scale(1.0F, -1.0F, 1.0F);
		scale(16.0F, 16.0F, 16.0F);

		if (!isGui3d) {
			disableLighting();
		} // else GlStateManager.enableLighting();
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

	/** compensates for the Entity View and applies the right translation. it pushes the matrix once ! */
	public static void offsetRendering(BlockPos pos, double partialTicks) {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();
		double vX = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
		double vY = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
		double vZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;
		pushMatrix();
		translate(pos.getX() - vX, pos.getY() - vY, pos.getZ() - vZ);
	}

	public static void drawBoundingBox(AxisAlignedBB box, BlockPos pos, float partialTicks, float r, float g, float b, float alpha) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		enableBlend();
		tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		glLineWidth(5.0F);
		disableTexture2D();
		depthMask(false);
		if (Minecraft.getMinecraft().world.getWorldBorder().contains(pos)) {
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
			double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
			translate(-d0 + pos.getX(), -d1 + pos.getY(), -d2 + pos.getZ());
			double val = 0.0020000000949949026D;
			drawBoundingBox(box.expand(val, val, val), r, g, b, alpha);
		}

		depthMask(true);
		enableTexture2D();
		disableBlend();
	}

	public static void drawBoundingBox(AxisAlignedBB box, float r, float g, float b, float alpha) {
		drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, alpha);
	}

	public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		drawBoundingBox(vertexbuffer, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, alpha);
		tessellator.draw();
	}

	public static void drawBoundingBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float alpha) {
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

	public static void resetLanguageRegistry() {
		SonarCore.logger.info("Resetting Language");
		Minecraft.getMinecraft().getLanguageManager().onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
		SonarCore.logger.info("Reset Language");
	}
}