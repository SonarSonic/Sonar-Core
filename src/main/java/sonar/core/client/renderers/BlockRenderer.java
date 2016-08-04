package sonar.core.client.renderers;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.vecmath.Matrix4f;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import sonar.core.common.block.SonarBlock;
import sonar.core.common.block.properties.IBlockRotated;
import sonar.core.common.block.properties.IBlockStateSpecial;
import sonar.core.helpers.RenderHelper;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

@SideOnly(Side.CLIENT)
@MethodsReturnNonnullByDefault
public class BlockRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> implements IModel {
	private final ISonarCustomRenderer renderer;

	public BlockRenderer(ISonarCustomRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return renderer.getAllTextures();
	}

	@Override
	public BakedBlockModel<T> bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new BakedBlockModel<>(format, renderer, bakedTextureGetter, true);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	@Override
	@ParametersAreNonnullByDefault
	public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		World world = te.getWorld();
		BlockPos pos = te.getPos();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		IBlockState extendedState = block.getExtendedState(state, world, pos);

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		vertexbuffer.begin(7, DefaultVertexFormats.BLOCK);
		vertexbuffer.color(255, 255, 255, 255);

		//this.renderer.renderWorldBlock(tessellator, world, pos, x, y, z, extendedState, block, te, true, partialTicks, destroyStage);

		tessellator.draw();

		GL11.glTranslated(-x, -y, -z);
		GL11.glPopMatrix();

	}

	public static class BakedBlockModel<T extends TileEntity> implements IBakedModel {
		private final VertexFormat format;
		private final ISonarCustomRenderer renderer;
		private final Function<ResourceLocation, TextureAtlasSprite> textures;
		private final ItemRenderer itemRenderer;

		private BakedBlockModel(VertexFormat format, ISonarCustomRenderer renderer, Function<ResourceLocation, TextureAtlasSprite> textures, boolean inventory) {
			this.format = format;
			this.renderer = renderer;
			this.textures = textures;
			this.itemRenderer = inventory ? new ItemRenderer<>(this.renderer, format, textures) : null;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			List<BakedQuad> list;
			if (side == null /* && (state instanceof IBlockStateSpecial) */ && renderer instanceof ISonarModelRenderer) {
				ModelTechne model = ((ISonarModelRenderer) renderer).getModel();
				TextureAtlasSprite sprite = ModelLoader.defaultTextureGetter().apply(renderer.getAllTextures().get(0));
				list = model.getBakedQuads(format, sprite, 1.0F);
				EnumFacing face = EnumFacing.NORTH;
				if (state.getBlock() instanceof IBlockRotated) {
					face = ((IBlockRotated) state.getBlock()).getRotation(state);
				}
				if (face == EnumFacing.UP) {
					list = RenderHelper.transformQuads(list, new TransformationMatrix(-90, 1, 0, 0, RenderHelper.getOffsetForFace(face)));
				} else if (face == EnumFacing.DOWN) {
					list = RenderHelper.transformQuads(list, new TransformationMatrix(90, 1, 0, 0, RenderHelper.getOffsetForFace(face)));
				} else {
					list = RenderHelper.transformQuads(list, new TransformationMatrix(-face.getHorizontalAngle(), 0, 1, 0, RenderHelper.getOffsetForFace(face)));
				}

			} else {
				list = ImmutableList.of();
			}
			return list;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			// return renderer.doInventoryRendering();
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return renderer.getIcon();
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return itemRenderer;
		}
	}

	public static class ItemRenderer<T extends TileEntity> extends ItemOverrideList {
		private final ISonarCustomRenderer renderer;
		private final Block block;
		private final T tile;
		private final VertexFormat format;
		private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

		public ItemRenderer(ISonarCustomRenderer renderer, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
			super(ImmutableList.<ItemOverride> of());
			this.renderer = renderer;
			//this.tile = renderer.getTileEntity();
			this.tile = null;
			this.block = renderer.getBlock();
			this.format = format;
			this.bakedTextureGetter = bakedTextureGetter;
		}

		@Override
		@ParametersAreNonnullByDefault
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			return new BakedItemModel<>(world, block, tile, stack, entity, renderer, format, bakedTextureGetter);
		}
	}

	public static class BakedItemModel<T extends TileEntity> implements IBakedModel, IPerspectiveAwareModel {
		private final ISonarCustomRenderer renderer;
		private final Block block;
		private final T tile;
		private final ItemStack stack;
		private final World world;
		private final EntityLivingBase entity;
		private ItemCameraTransforms.TransformType transformType;
		private final VertexFormat format;
		private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

		private BakedItemModel(World world, Block block, T tile, ItemStack stack, EntityLivingBase entity, ISonarCustomRenderer renderer, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
			this.world = world;
			this.block = block;
			this.tile = tile;
			this.stack = stack;
			this.entity = entity;
			this.renderer = renderer;
			this.transformType = ItemCameraTransforms.TransformType.NONE;
			this.format = format;
			this.bakedTextureGetter = bakedTextureGetter;
		}

		private BakedItemModel<T> setTransformType(ItemCameraTransforms.TransformType type) {
			this.transformType = type;
			return this;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			List<BakedQuad> list;
			if (side == null) {
				ModelTechne model = ((ISonarModelRenderer) renderer).getModel();
				TextureAtlasSprite sprite = ModelLoader.defaultTextureGetter().apply(renderer.getAllTextures().get(0));
				list = model.getBakedQuads(format, sprite, 1.0F);

				if(transformType == TransformType.GUI){
					//GL11.glDisable(GL11.GL_LIGHTING);
					list = RenderHelper.transformQuads(RenderHelper.transformQuads(list, new TransformationMatrix(45, 0, 1, 0)), new TransformationMatrix(30, 1, 0, 0));
					//GL11.glEnable(GL11.GL_LIGHTING);
				}
				/*
				 * ITessellator tessellator = TessellatorBakedQuad.getInstance().setTextureFunction(bakedTextureGetter);
				 * 
				 * tessellator.startDrawingQuads(format);
				 * 
				 * this.renderer.renderInventoryBlock(tessellator, world, state, block, tile, stack, entity, transformType);
				 * 
				 * list = tessellator.getQuads(); tessellator.draw();
				 */
			} else {
				list = ImmutableList.of();
			}
			return list;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.NONE;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
			return new ImmutablePair<>(this.setTransformType(cameraTransformType), null);
		}
	}
}