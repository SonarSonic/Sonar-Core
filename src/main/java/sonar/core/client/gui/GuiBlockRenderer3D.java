package sonar.core.client.gui;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
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
	// protected static IBlockState multipartState = MCMultiPart.multipart.getDefaultState();

	public final Vector3d origin = new Vector3d();
	public final Vector3d eye = new Vector3d();
	public List<GuiBlockRenderCache> blocks = new ArrayList<>();

	public int cubeSize;

	public GuiBlockRenderer3D(int cubeSize) {
		this.cubeSize = cubeSize;
	}

	public static class GuiBlockRenderCache {
		public BlockPos pos;
		public IBlockState state;
		public TileEntity tile;

		public GuiBlockRenderCache(BlockPos pos, IBlockState state) {
			this.pos = pos;
			this.state = state;
		}

		public GuiBlockRenderCache(BlockPos pos, IBlockState state, TileEntity tile) {
			this.pos = pos;
			this.state = state;
			this.tile = tile;
		}
	}
	
	public IBlockAccess world(){
		return this;
	}

	public boolean validPos(BlockPos pos) {
		int size = cubeSize / 2;
		return pos.getX() < size && pos.getX() > -size && pos.getY() < size && pos.getY() > -size && pos.getZ() < size && pos.getZ() > -size;
	}

	public void addBlock(BlockPos pos, IBlockState state) {
		if (validPos(pos)) {
			blocks.add(new GuiBlockRenderCache(pos, state));
		}
	}

	public void addBlock(BlockPos pos, IBlockState state, TileEntity tile) {
		if (validPos(pos)) {
			blocks.add(new GuiBlockRenderCache(pos, state, tile));
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

		Vector3d trans = new Vector3d(-origin.x + eye.x - 8 * 0.0625, -origin.y + eye.y, -origin.z + eye.z - 8 * 0.0625);

		for (BlockRenderLayer layer : BlockRenderLayer.values()) {
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
		for (GuiBlockRenderCache cache : blocks) {
			if (cache.tile != null) {
				if (cache.tile.shouldRenderInPass(pass)) {
					Vector3d at = new Vector3d(eye.x, eye.y, eye.z);
					BlockPos pos = cache.pos;
					at.x += pos.getX() - origin.x;
					at.y += pos.getY() - origin.y;
					at.z += pos.getZ() - origin.z;
					if (cache.tile.getClass() == TileEntityChest.class) {
						TileEntityChest chest = (TileEntityChest) cache.tile;
						at.x -= 0.5;
						at.z -= 0.5;
						GL11.glRotated(180, 0, 1, 0);
					}
					doSpecialRender(cache, at);
					if (cache.tile.getClass() == TileEntityChest.class) {
						GL11.glRotated(-180, 0, 1, 0);
					}
				}
			}
		}
	}

	public void doSpecialRender(GuiBlockRenderCache cache, Vector3d at) {
		TileEntityRendererDispatcher.instance.render(cache.tile, at.x, at.y, at.z, 0);
	}

	private void doWorldRenderPass(Vector3d trans, BlockRenderLayer layer) {

		BufferBuilder wr = Tessellator.getInstance().getBuffer();
		wr.begin(7, DefaultVertexFormats.BLOCK);

		Tessellator.getInstance().getBuffer().setTranslation(trans.x, trans.y, trans.z);

		for (GuiBlockRenderCache cache : blocks) {
			IBlockState state = cache.state;
			BlockPos pos = cache.pos;
			Block block = state.getBlock();
			if (block.canRenderInLayer(state, layer)) {
				renderBlock(state, pos, this, Tessellator.getInstance().getBuffer());
			}
		}

		Tessellator.getInstance().draw();
		Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
	}

	public void renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder worldRendererIn) {

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

	public void renderMultipart(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder worldRendererIn) {

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

	/* public void renderInGui() { BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher(); GlStateManager.pushMatrix(); GlStateManager.rotate(33, 0,0, 1); GlStateManager.scale(64, 64, 64); Tessellator tessellator = Tessellator.getInstance(); BufferBuilder vertex = tessellator.getBuffer(); vertex.begin(7, DefaultVertexFormats.BLOCK); for (Entry<BlockPos, IBlockState> ren : blocks.entrySet()) { try { renderer.renderBlock(ren.getValue(), ren.getKey(), this, vertex); } catch (Exception e) { e.printStackTrace(); } } //tessellator.draw(); vertex.finishDrawing(); GlStateManager.popMatrix(); } */

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		for (GuiBlockRenderCache cache : blocks) {
			if (cache.pos == pos && cache.tile != null) {
				return cache.tile;
			}
		}
		return null;
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return 0;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		for (GuiBlockRenderCache cache : blocks) {
			if (cache.pos == pos && cache.tile != null) {
				return cache.state;
			}
		}
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		IBlockState state = getBlockState(pos);
		return state == null || state.getBlock() == Blocks.AIR;
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
