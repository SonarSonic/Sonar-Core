package sonar.core.client.renderers;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISonarCustomRenderer extends ItemMeshDefinition {

	public ModelResourceLocation getBlockModelResourceLocation();

	public Block getBlock();

	public boolean hasStaticRendering();

	public TextureAtlasSprite getIcon();

	public boolean doInventoryRendering();

	//public TileEntity getTileEntity();

	public ArrayList<ResourceLocation> getAllTextures();

	
	
	//public void renderWorldBlock(Tessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block, TileEntity tile, boolean dynamicRender, float partialTick, int destroyStage);

	//public void renderInventoryBlock(Tessellator tessellator, World world, IBlockState state, Block block, TileEntity tile, ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type);
}
