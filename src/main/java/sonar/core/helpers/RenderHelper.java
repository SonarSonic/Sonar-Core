package sonar.core.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import sonar.core.client.gui.GuiSonar;

public class RenderHelper {
	
	public static final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
	public static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

	protected RenderManager renderManager;

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

	public static EnumFacing getHorizontal(EnumFacing forward) {
		if (forward == EnumFacing.NORTH) {
			return EnumFacing.EAST;
		}
		if (forward == EnumFacing.EAST) {
			return EnumFacing.SOUTH;
		}
		if (forward == EnumFacing.SOUTH) {
			return EnumFacing.WEST;
		}
		if (forward == EnumFacing.WEST) {
			return EnumFacing.NORTH;
		}
		return null;

	}

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

	/*
	 * public static boolean renderItem(ItemStack item, float bobing, float rotation, Random random, TextureManager engine, RenderBlocks renderBlocks, int count) { IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, ENTITY); if (customRenderer == null) { return false; }
	 * 
	 * if (customRenderer.shouldUseRenderHelper(ENTITY, item, ENTITY_ROTATION)) { GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F); } if (!customRenderer.shouldUseRenderHelper(ENTITY, item, ENTITY_BOBBING)) { GL11.glTranslatef(0.0F, -bobing, 0.0F); } boolean is3D = customRenderer.shouldUseRenderHelper(ENTITY, item, BLOCK_3D);
	 * 
	 * engine.bindTexture(item.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture); Block block = item.getItem() instanceof ItemBlock ? Block.getBlockFromItem(item.getItem()) : null; if (is3D || (block != null && RenderBlocks.renderItemIn3d(block.getRenderType()))) { int renderType = (block != null ? block.getRenderType() : 1); float scale = (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2 ? 0.5F : 0.25F); boolean blend = block != null && block.getRenderBlockPass() > 0;
	 * 
	 * if (RenderItem.renderInFrame) { GL11.glScalef(1.25F, 1.25F, 1.25F); GL11.glTranslatef(0.0F, 0.05F, 0.0F); GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F); }
	 * 
	 * if (blend) { GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F); GL11.glEnable(GL11.GL_BLEND); OpenGlHelper.glBlendFunc(770, 771, 1, 0); }
	 * 
	 * GL11.glScalef(scale, scale, scale);
	 * 
	 * for (int j = 0; j < count; j++) { GL11.glPushMatrix(); if (j > 0) { GL11.glTranslatef(((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / scale, ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / scale, ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / scale); } customRenderer.renderItem(ENTITY, item, renderBlocks); GL11.glPopMatrix(); }
	 * 
	 * if (blend) { GL11.glDisable(GL11.GL_BLEND); } } else { GL11.glScalef(0.5F, 0.5F, 0.5F); customRenderer.renderItem(ENTITY, item, renderBlocks); } return true; }
	 * 
	 * public static void renderItem(World world, ItemStack stack) { if (stack == null) { return; } ItemStack render = stack.copy(); if (render != null) { if (render.getItem() instanceof ItemBlock) { GL11.glRotated(90, 1, 0, 0); GL11.glTranslated(0, -0.052, -0.2); } EntityItem entityitem = new EntityItem(world, 0.0D, 0.0D, 0.0D, render); Item item = entityitem.getEntityItem().getItem(); entityitem.getEntityItem().stackSize = 1; entityitem.hoverStart = 0.0F;
	 * 
	 * RenderItem.renderInFrame = true; RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F); RenderItem.renderInFrame = false;
	 * 
	 * if (item == Items.compass) { TextureAtlasSprite textureatlassprite = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite(Items.compass.getIconIndex(entityitem.getEntityItem()).getIconName());
	 * 
	 * if (textureatlassprite.getFrameCount() > 0) { textureatlassprite.updateAnimation(); } }
	 * 
	 * } }
	 * 
	 * /** returns horizontal direction to the forward direction *
	 */
	/*
	 * 
	 * public static void renderBlockCollisions(World world, int x, int y, int z) { if (world != null) { if (world.getBlock(x, y, z) instanceof SonarBlock) { SonarBlock block = (SonarBlock) world.getBlock(x, y, z); if (block.hasSpecialCollisionBox()) { GL11.glEnable(GL11.GL_BLEND); OpenGlHelper.glBlendFunc(770, 771, 1, 0); GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F); GL11.glLineWidth(2.0F); GL11.glDisable(GL11.GL_TEXTURE_2D); GL11.glDepthMask(false);
	 * 
	 * List<AxisAlignedBB> list = new ArrayList(); block.getCollisionBoxes(world, x, y, z, list); for (AxisAlignedBB axis : list) { RenderGlobal.drawOutlinedBoundingBox(axis, -1); }
	 * 
	 * GL11.glDepthMask(true); GL11.glEnable(GL11.GL_TEXTURE_2D); GL11.glDisable(GL11.GL_BLEND); }
	 * 
	 * } } } public static void renderStoredItemStackOverlay(FontRenderer font, TextureManager tex, ItemStack stack, long stored, int x, int y, String string) { if (stack != null) { stack.stackSize = 1; Minecraft.getMinecraft().getRenderItem().renderi.renderItemOverlayIntoGUI(font, stack, x, y, FontHelper.formatStackSize(stored)); if (stored > 0 || string != null) { String s1 = string == null ? : string;
	 * 
	 * final float scaleFactor = 0.5F; final float inverseScaleFactor = 1.0f / scaleFactor; GL11.glDisable(GL11.GL_LIGHTING); GL11.glDisable(GL11.GL_DEPTH_TEST); GL11.glPushMatrix(); GL11.glScaled(scaleFactor, scaleFactor, scaleFactor); final int X = (int) (((float) x + 15.0f - font.getStringWidth(s1) * scaleFactor) * inverseScaleFactor); final int Y = (int) (((float) y + 15.0f - 7.0f * scaleFactor) * inverseScaleFactor); font.drawStringWithShadow(s1, X, Y, 16777215); GL11.glPopMatrix(); GL11.glEnable(GL11.GL_LIGHTING); GL11.glEnable(GL11.GL_DEPTH_TEST); } } } /* public static void renderFluidInGUI(FontRenderer font, TextureManager tex, FluidStack stack, long stored, int x, int y, String string) { if (stack != null) { RenderItem.getInstance().renderIcon(x, y, stack.getFluid().getIcon(), 16, 16); if (stored > 0 || string != null) { String s1 = string == null ? FontHelper.formatFluidSize(stored) : string;
	 * 
	 * final float scaleFactor = 0.5F; final float inverseScaleFactor = 1.0f / scaleFactor; GL11.glDisable(GL11.GL_LIGHTING); GL11.glDisable(GL11.GL_DEPTH_TEST); GL11.glPushMatrix(); GL11.glScaled(scaleFactor, scaleFactor, scaleFactor); final int X = (int) (((float) x + 15.0f - font.getStringWidth(s1) * scaleFactor) * inverseScaleFactor); final int Y = (int) (((float) y + 15.0f - 7.0f * scaleFactor) * inverseScaleFactor); font.drawStringWithShadow(s1, X, Y, 16777215); GL11.glPopMatrix(); GL11.glEnable(GL11.GL_LIGHTING); GL11.glEnable(GL11.GL_DEPTH_TEST); } } }
	 * 
	 * /** AE2 Rendering Method - All credit goes to them, I don't understand it
	 */

	/*
	 * public static void doRenderItem(ItemStack itemstack, World world, boolean normalSize) { if (itemstack != null) { EntityItem entityitem = new EntityItem(world, 0.0D, 0.0D, 0.0D, itemstack); entityitem.getEntityItem().stackSize = 1;
	 * 
	 * entityitem.hoverStart = 0; entityitem.age = 0; entityitem.rotationYaw = 0;
	 * 
	 * GL11.glPushMatrix(); GL11.glTranslatef(0, -0.04F, 0); GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	 * 
	 * if (itemstack.isItemEnchanted() || itemstack.getItem().requiresMultipleRenderPasses()) { GL11.glTranslatef(0.0f, -0.05f, -0.25f); GL11.glScalef(1.0f / 1.5f, 1.0f / 1.5f, 1.0f / 1.5f); GL11.glScalef(1.0f, -1.0f, normalSize ? 1.0f : 0.005f);
	 * 
	 * Block block = Block.getBlockFromItem(itemstack.getItem()); if ((itemstack.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(block.getRenderType()))) { GL11.glRotatef(25.0f, 1.0f, 0.0f, 0.0f); GL11.glRotatef(15.0f, 0.0f, 1.0f, 0.0f); GL11.glRotatef(30.0f, 0.0f, 1.0f, 0.0f); }
	 * 
	 * IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, IItemRenderer.ItemRenderType.ENTITY); if (customRenderer != null && !(itemstack.getItem() instanceof ItemBlock)) { if (customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.ENTITY, itemstack, IItemRenderer.ItemRendererHelper.BLOCK_3D)) { GL11.glTranslatef(0, -0.04F, 0); GL11.glScalef(0.7f, 0.7f, 0.7f); GL11.glRotatef(35, 1, 0, 0); GL11.glRotatef(45, 0, 1, 0); GL11.glRotatef(-90, 0, 1, 0); } } else if (itemstack.getItem() instanceof ItemBlock) { GL11.glTranslatef(0, -0.04F, 0); GL11.glScalef(1.1f, 1.1f, 1.1f); GL11.glRotatef(-90, 0, 1, 0); } else { GL11.glTranslatef(0, -0.14F, 0); GL11.glScalef(0.8f, 0.8f, 0.8f); }
	 * 
	 * RenderItem.renderInFrame = true; RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F); RenderItem.renderInFrame = false; } else { GL11.glScalef(1.0f / 42.0f, 1.0f / 42.0f, 1.0f / 42.0f); GL11.glTranslated(-8.0, -10.2, -10.4); GL11.glScalef(1.0f, 1.0f, normalSize ? 1.0f : 0.01f);
	 * 
	 * RenderItem.renderInFrame = false; final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj; if (!ForgeHooksClient.renderInventoryItem(RenderBlocks.getInstance(), Minecraft.getMinecraft().renderEngine, itemstack, true, 0, 0, 0)) { RenderItem.getInstance().renderItemIntoGUI(fr, Minecraft.getMinecraft().renderEngine, itemstack, 0, 0, false); } }
	 * 
	 * GL11.glPopMatrix(); } }
	 * 
	 * public static void drawTexturedModalRect(double minX, double minY, double maxY, double width, double height) { Tessellator tessellator = Tessellator.instance; tessellator.startDrawingQuads(); double widthnew = (0 + (width * (2))); double heightnew = (0 + ((height) * (2))); tessellator.addVertexWithUV((minX + 0), maxY / 2, 0, 0, heightnew); tessellator.addVertexWithUV((minX + width), maxY / 2, 0, widthnew, heightnew); tessellator.addVertexWithUV((minX + width), (minY + 0), 0, widthnew, 0); tessellator.addVertexWithUV((minX + 0), (minY + 0), 0, 0, 0); tessellator.draw(); }
	 * 
	 * public void drawTexturedModelRectFromIcon(int p_94065_1_, int p_94065_2_, IIcon p_94065_3_, int p_94065_4_, int p_94065_5_) { Tessellator tessellator = Tessellator.getInstance(); tessellator.startDrawingQuads(); tessellator.addVertexWithUV((double) (p_94065_1_ + 0), (double) (p_94065_2_ + p_94065_5_), 0, (double) p_94065_3_.getMinU(), (double) p_94065_3_.getMaxV()); tessellator.addVertexWithUV((double) (p_94065_1_ + p_94065_4_), (double) (p_94065_2_ + p_94065_5_), 0, (double) p_94065_3_.getMaxU(), (double) p_94065_3_.getMaxV()); tessellator.addVertexWithUV((double) (p_94065_1_ + p_94065_4_), (double) (p_94065_2_ + 0), 0, (double) p_94065_3_.getMaxU(), (double) p_94065_3_.getMinV()); tessellator.addVertexWithUV((double) (p_94065_1_ + 0), (double) (p_94065_2_ + 0), 0, (double) p_94065_3_.getMinU(), (double) p_94065_3_.getMinV()); tessellator.draw(); }
	 */

	//1.9.4 additions

	public static void addVertexWithUV(VertexBuffer vertexbuffer, double x, double y, double z, double u, double v) {
		vertexbuffer.pos(x, y, z).tex(u, v).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
	}

	/*
	 * public static void doRenderItem(ItemStack item, World world, boolean b) { // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * public static void renderItemInGui(ItemStack stack, int x, int y) {
	 * 
	 * }
	 */
	public static void renderItem(GuiSonar screen, int x, int y, ItemStack stack) {
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		screen.setZLevel(200.0F);
		itemRender.zLevel = 200.0F;
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		//itemRender.renderItemOverlayIntoGUI(getFontFromStack(stack), stack, x, y, "");
		screen.setZLevel(0.0F);
		itemRender.zLevel = 0.0F;
		//GlStateManager.translate(0.0F, 0.0F, -32.0F);
	}

	public static void renderStoredItemStackOverlay(ItemStack stack, long stored, int x, int y, String string) {
		if (stack != null) {
			FontRenderer font = getFontFromStack(stack);
			stack.stackSize = 1;
			if (stored > 0 || string != null) {
				String s1 = string == null ? FontHelper.formatStackSize(stored) : string;

				final float scaleFactor = 0.5F;
				final float inverseScaleFactor = 1.0f / scaleFactor;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glPushMatrix();
				GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
				final int X = (int) (((float) x + 15.0f - font.getStringWidth(s1) * scaleFactor) * inverseScaleFactor);
				final int Y = (int) (((float) y + 15.0f - 7.0f * scaleFactor) * inverseScaleFactor);
				font.drawStringWithShadow(s1, X, Y, 16777215);
				GL11.glPopMatrix();
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
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
        float textureX=0;
        float textureY=0;
		double widthnew = (0 + (width * (2)));
		double heightnew = (0 + ((height) * (2)));
        
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)0).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)0).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)0).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)0).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.draw();
        
        
		/*
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertex = tessellator.getBuffer();
		vertex.begin(7, DefaultVertexFormats.POSITION_TEX);
		double widthnew = (0 + (width * (2)));
		double heightnew = (0 + ((height) * (2)));
		addVertexWithUV(vertex, (minX + 0), maxY / 2, 0, 0, heightnew);
		addVertexWithUV(vertex, (minX + width), maxY / 2, 0, widthnew, heightnew);
		addVertexWithUV(vertex, (minX + width), (minY + 0), 0, widthnew, 0);
		addVertexWithUV(vertex, (minX + 0), (minY + 0), 0, 0, 0);
		tessellator.draw();
		
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)x, (double)(y + height), 0.0D).tex((double)(u * f), (double)((v + (float)height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)y, 0.0D).tex((double)((u + (float)width) * f), (double)(v * f1)).endVertex();
        vertexbuffer.pos((double)x, (double)y, 0.0D).tex((double)(u * f), (double)(v * f1)).endVertex();
        tessellator.draw();
        */
		//Gui.drawModalRectWithCustomSizedTexture(0, 0, width, height, 16, 16, minX, minY);
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
		
		/*
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
		itemRender.renderItemOverlayIntoGUI(getFontFromStack(stack), stack, 0, 0, "");
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		*/
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
			GlStateManager.scale(40/64F, 40/64F, 40/64F);
			GlStateManager.rotate(210.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.enableLighting();
		} else {
			//GlStateManager.scale(64.0F, 64.0F, 64.0F);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableLighting();
		}
		itemRender.renderItem(itemStack, transformType);
	}

}