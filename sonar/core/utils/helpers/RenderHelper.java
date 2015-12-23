package sonar.core.utils.helpers;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.ENTITY_BOBBING;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.ENTITY_ROTATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import sonar.core.common.block.SonarBlock;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.core.utils.SonarAPI;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderHelper {

	private static final ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
	protected RenderManager renderManager;

	public static int setMetaData(TileEntity tileentity) {
		int i;
		if (tileentity.getWorldObj() == null) {
			i = 0;
		} else {
			Block block = tileentity.getBlockType();
			i = tileentity.getBlockMetadata();
			if ((block != null) && (i == 0)) {
				i = tileentity.getBlockMetadata();
			}
		}
		if (SonarAPI.forgeMultipartLoaded() && tileentity != null && tileentity.getWorldObj() != null && tileentity instanceof TileMultipart) {
			TMultiPart part = ((TileMultipart) tileentity).jPartList().get(0);
			if (part != null && part instanceof SonarTilePart) {
				i = ((SonarTilePart) part).meta;
			}
		}

		return i;
	}

	public static void beginRender(double x, double y, double z, int meta, String texture) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(texture));
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

	public static boolean renderItem(ItemStack item, float bobing, float rotation, Random random, TextureManager engine, RenderBlocks renderBlocks, int count) {
		IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, ENTITY);
		if (customRenderer == null) {
			return false;
		}

		if (customRenderer.shouldUseRenderHelper(ENTITY, item, ENTITY_ROTATION)) {
			GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		}
		if (!customRenderer.shouldUseRenderHelper(ENTITY, item, ENTITY_BOBBING)) {
			GL11.glTranslatef(0.0F, -bobing, 0.0F);
		}
		boolean is3D = customRenderer.shouldUseRenderHelper(ENTITY, item, BLOCK_3D);

		engine.bindTexture(item.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
		Block block = item.getItem() instanceof ItemBlock ? Block.getBlockFromItem(item.getItem()) : null;
		if (is3D || (block != null && RenderBlocks.renderItemIn3d(block.getRenderType()))) {
			int renderType = (block != null ? block.getRenderType() : 1);
			float scale = (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2 ? 0.5F : 0.25F);
			boolean blend = block != null && block.getRenderBlockPass() > 0;

			if (RenderItem.renderInFrame) {
				GL11.glScalef(1.25F, 1.25F, 1.25F);
				GL11.glTranslatef(0.0F, 0.05F, 0.0F);
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			}

			if (blend) {
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			}

			GL11.glScalef(scale, scale, scale);

			for (int j = 0; j < count; j++) {
				GL11.glPushMatrix();
				if (j > 0) {
					GL11.glTranslatef(((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / scale, ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / scale, ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / scale);
				}
				customRenderer.renderItem(ENTITY, item, renderBlocks);
				GL11.glPopMatrix();
			}

			if (blend) {
				GL11.glDisable(GL11.GL_BLEND);
			}
		} else {
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			customRenderer.renderItem(ENTITY, item, renderBlocks);
		}
		return true;
	}

	public static void renderItem(World world, ItemStack stack) {
		if (stack == null) {
			return;
		}
		ItemStack render = stack.copy();
		if (render != null) {
			if (render.getItem() instanceof ItemBlock) {
				GL11.glRotated(90, 1, 0, 0);
				GL11.glTranslated(0, -0.052, -0.2);
			}
			EntityItem entityitem = new EntityItem(world, 0.0D, 0.0D, 0.0D, render);
			Item item = entityitem.getEntityItem().getItem();
			entityitem.getEntityItem().stackSize = 1;
			entityitem.hoverStart = 0.0F;

			RenderItem.renderInFrame = true;
			RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
			RenderItem.renderInFrame = false;

			if (item == Items.compass) {
				TextureAtlasSprite textureatlassprite = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite(Items.compass.getIconIndex(entityitem.getEntityItem()).getIconName());

				if (textureatlassprite.getFrameCount() > 0) {
					textureatlassprite.updateAnimation();
				}
			}

		}
	}

	/** returns horizontal direction to the forward direction **/
	public static ForgeDirection getHorizontal(ForgeDirection forward) {
		if (forward == ForgeDirection.NORTH) {
			return ForgeDirection.EAST;
		}
		if (forward == ForgeDirection.EAST) {
			return ForgeDirection.SOUTH;
		}
		if (forward == ForgeDirection.SOUTH) {
			return ForgeDirection.WEST;
		}
		if (forward == ForgeDirection.WEST) {
			return ForgeDirection.NORTH;
		}
		return null;

	}

	public static void renderBlockCollisions(World world, int x, int y, int z) {
		if (world != null) {
			System.out.print(world.getBlock(x, y, z));
			if (world.getBlock(x, y, z) instanceof SonarBlock) {
				SonarBlock block = (SonarBlock) world.getBlock(x, y, z);
				if (block.hasSpecialCollisionBox()) {
					GL11.glEnable(GL11.GL_BLEND);
					OpenGlHelper.glBlendFunc(770, 771, 1, 0);
					GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
					GL11.glLineWidth(2.0F);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDepthMask(false);

					List<AxisAlignedBB> list = new ArrayList();
					block.getCollisionBoxes(world, x, y, z, list);
					for (AxisAlignedBB axis : list) {
						RenderGlobal.drawOutlinedBoundingBox(axis, -1);
					}

					GL11.glDepthMask(true);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_BLEND);
				}

			}
		}
	}

}
