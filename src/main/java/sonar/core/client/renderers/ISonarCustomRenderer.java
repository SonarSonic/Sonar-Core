package sonar.core.client.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

public interface ISonarCustomRenderer extends ItemMeshDefinition {

    ModelResourceLocation getBlockModelResourceLocation();

    Block getBlock();

    boolean hasStaticRendering();

    TextureAtlasSprite getIcon();

    boolean doInventoryRendering();

	//public TileEntity getTileEntity();

    ArrayList<ResourceLocation> getAllTextures();
	
	//public void renderWorldBlock(Tessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block, TileEntity tile, boolean dynamicRender, float partialTick, int destroyStage);

	//public void renderInventoryBlock(Tessellator tessellator, World world, IBlockState state, Block block, TileEntity tile, ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type);
}
