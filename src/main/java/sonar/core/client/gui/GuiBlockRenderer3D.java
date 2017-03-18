package sonar.core.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.ForgeHooksClient;

public class GuiBlockRenderer3D implements IBlockAccess {

	protected static final Minecraft mc = Minecraft.getMinecraft();
	protected static IBlockState multipartState = MCMultiPartMod.multipart.getDefaultState();

	public final Vector3d origin = new Vector3d();
	public final Vector3d eye = new Vector3d();
	// public HashMap<IBlockState, BlockCoords> blocks = new HashMap();
	public HashMap<BlockPos, IBlockState> blocks = new HashMap();
	public HashMap<BlockPos, TileEntity> entities = new HashMap();
	public HashMap<BlockPos, List<MultipartStateOverride>> multiparts = new HashMap();
	public int cubeSize;

	public GuiBlockRenderer3D(int cubeSize) {
		this.cubeSize = cubeSize;
	}

	public boolean validPos(BlockPos pos) {
		int size = cubeSize / 2;
		return pos.getX() < size && pos.getX() > -size && pos.getY() < size && pos.getY() > -size && pos.getZ() < size && pos.getZ() > -size;
	}

	public void addBlock(IBlockState state, BlockPos pos) {
		if (validPos(pos)) {
			blocks.put(pos, state);
		}
	}

	public void addTileEntity(TileEntity state, BlockPos pos) {
		if (validPos(pos)) {
			entities.put(pos, state);
		}
	}

	public void addMultiparts(List<Object> parts, BlockPos pos) {
		if (validPos(pos)) {
			List<MultipartStateOverride> newParts = new ArrayList();
			for (Object part : parts) {
				if (part instanceof MultipartStateOverride) {
					newParts.add((MultipartStateOverride) part);
				} else if (part instanceof IMultipart) {
					newParts.add(new MultipartStateOverride((Multipart) part));
				}
			}
			multiparts.put(pos, newParts);
		}
	}

	public void renderInGui() {
		GlStateManager.enableCull();
		GlStateManager.enableRescaleNormal();

		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap();
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.disableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();

		Vector3d trans = new Vector3d((-origin.x) + eye.x - 8 * 0.0625, (-origin.y) + eye.y, (-origin.z) + eye.z - 8 * 0.0625);

		// GlStateManager.translate(-0.16, -0.16, -0.16);
		for (BlockRenderLayer layer : BlockRenderLayer.values()) {
			if (layer != BlockRenderLayer.TRANSLUCENT) {
				doMultipartRenderPass(trans);
			}
			ForgeHooksClient.setRenderLayer(layer);
			setGlStateForPass(layer);
			doWorldRenderPass(trans, layer);
		}

		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();
		TileEntityRendererDispatcher.instance.entityX = origin.x - eye.x;
		TileEntityRendererDispatcher.instance.entityY = origin.y - eye.y;
		TileEntityRendererDispatcher.instance.entityZ = origin.z - eye.z;
		TileEntityRendererDispatcher.staticPlayerX = origin.x - eye.x;
		TileEntityRendererDispatcher.staticPlayerY = origin.y - eye.y;
		TileEntityRendererDispatcher.staticPlayerZ = origin.z - eye.z;
		for (int pass = 0; pass < 2; pass++) {
			setGlStateForPass(pass);
			doTileEntityRenderPass(pass);
		}
		setGlStateForPass(0);

	}

	private void doTileEntityRenderPass(int pass) {
		ForgeHooksClient.setRenderPass(pass);
		for (Entry<BlockPos, TileEntity> entry : entities.entrySet()) {
			TileEntity tile = this.getTileEntity(entry.getKey());
			if (tile != null) {
				if (tile.shouldRenderInPass(pass)) {
					Vector3d at = new Vector3d(eye.x, eye.y, eye.z);
					BlockPos pos = entry.getKey();
					at.x += pos.getX() - origin.x;
					at.y += pos.getY() - origin.y;
					at.z += pos.getZ() - origin.z;
					if (tile.getClass() == TileEntityChest.class) {
						TileEntityChest chest = (TileEntityChest) tile;
						at.x -= 0.5;
						at.z -= 0.5;
						GL11.glRotated(180, 0, 1, 0);
					}
					TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, at.x, at.y, at.z, 0);
					if (tile.getClass() == TileEntityChest.class) {
						GL11.glRotated(-180, 0, 1, 0);						
					}
				}
			}
		}
	}

	private void doWorldRenderPass(Vector3d trans, BlockRenderLayer layer) {

		VertexBuffer wr = Tessellator.getInstance().getBuffer();
		wr.begin(7, DefaultVertexFormats.BLOCK);

		Tessellator.getInstance().getBuffer().setTranslation(trans.x, trans.y, trans.z);

		for (Entry<BlockPos, IBlockState> entry : blocks.entrySet()) {
			IBlockState state = entry.getValue();
			BlockPos pos = entry.getKey();
			Block block = state.getBlock();
			state = state.getActualState(this, pos);
			if (block.canRenderInLayer(state, layer)) {
				renderBlock(state, entry.getKey(), this, Tessellator.getInstance().getBuffer());
			}
		}

		Tessellator.getInstance().draw();
		Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
	}

	public void doMultipartRenderPass(Vector3d trans) {

		VertexBuffer wr = Tessellator.getInstance().getBuffer();
		wr.begin(7, DefaultVertexFormats.BLOCK);

		Tessellator.getInstance().getBuffer().setTranslation(trans.x, trans.y, trans.z);

		for (Entry<BlockPos, List<MultipartStateOverride>> entry : multiparts.entrySet()) {
			for (MultipartStateOverride part : entry.getValue()) {
				IBlockState state = part.getActualState(MultipartRegistry.getDefaultState(part.part).getBaseState(), this, entry.getKey());
				renderMultipart(state, entry.getKey(), this, Tessellator.getInstance().getBuffer());
			}
		}

		Tessellator.getInstance().draw();
		Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
	}

	/* public void renderMultipartAt(List<Multipart> parts, BlockPos pos, float partialTicks, int destroyStage) { RenderHelper.disableStandardItemLighting(); GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); GlStateManager.enableBlend(); GlStateManager.disableCull(); if (Minecraft.isAmbientOcclusionEnabled()) { GlStateManager.shadeModel(GL11.GL_SMOOTH); } else { GlStateManager.shadeModel(GL11.GL_FLAT); } for (Multipart part : parts) { try { } catch (Throwable e) { e.printStackTrace(); } } RenderHelper.enableStandardItemLighting(); } */

	public void renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, VertexBuffer worldRendererIn) {

		try {
			BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
			EnumBlockRenderType type = state.getRenderType();
			if (type != EnumBlockRenderType.MODEL) {
				blockrendererdispatcher.renderBlock(state, pos, blockAccess, worldRendererIn);
				return;
			}

			// We only want to change one param here, the check sides
			IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(state);
			state = state.getBlock().getExtendedState(state, this, pos);
			blockrendererdispatcher.getBlockModelRenderer().renderModel(blockAccess, ibakedmodel, state, pos, worldRendererIn, false);

		} catch (Throwable throwable) {
			throwable.printStackTrace();
			// Just bury a render issue here, it is only the IO screen
		}
	}

	public void renderMultipart(IBlockState state, BlockPos pos, IBlockAccess blockAccess, VertexBuffer worldRendererIn) {

		try {
			BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
			EnumBlockRenderType type = state.getRenderType();
			if (type != EnumBlockRenderType.MODEL) {
				blockrendererdispatcher.renderBlock(state, pos, blockAccess, worldRendererIn);
				return;
			}

			// We only want to change one param here, the check sides
			IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(state);
			// state = state.getBlock().getExtendedState(state, this, pos);
			blockrendererdispatcher.getBlockModelRenderer().renderModel(blockAccess, ibakedmodel, state, pos, worldRendererIn, false);

		} catch (Throwable throwable) {
			throwable.printStackTrace();
			// Just bury a render issue here, it is only the IO screen
		}
	}

	private void setGlStateForPass(BlockRenderLayer layer) {
		int pass = layer == BlockRenderLayer.TRANSLUCENT ? 1 : 0;
		setGlStateForPass(pass);
	}

	private void setGlStateForPass(int layer) {
		GlStateManager.color(1, 1, 1);
		if (layer == 0) {
			GlStateManager.enableDepth();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		} else {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.depthMask(false);

		}

	}

	/* public void renderInGui() { BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher(); GlStateManager.pushMatrix(); GlStateManager.rotate(33, 0,0, 1); GlStateManager.scale(64, 64, 64); Tessellator tessellator = Tessellator.getInstance(); VertexBuffer vertex = tessellator.getBuffer(); vertex.begin(7, DefaultVertexFormats.BLOCK); for (Entry<BlockPos, IBlockState> ren : blocks.entrySet()) { try { renderer.renderBlock(ren.getValue(), ren.getKey(), this, vertex); } catch (Exception e) { e.printStackTrace(); } } //tessellator.draw(); vertex.finishDrawing(); GlStateManager.popMatrix(); } */

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return entities.get(pos);
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return 0;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		IBlockState state = blocks.get(pos);
		return state == null ? Blocks.AIR.getDefaultState() : state;
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		return blocks.get(pos) == null;
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		return Biome.getBiome(0);
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return 0;
	}

	@Override
	public WorldType getWorldType() {
		return WorldType.DEFAULT;
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		return false;
	}

}
