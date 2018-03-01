package sonar.core.client.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SonarCustomStateMapper extends StateMapperBase implements ICustomModelLoader {

	public final ArrayList<ISonarCustomRenderer> renderers;
	public final Map<ResourceLocation, BlockRenderer<? extends TileEntity>> customModels;

	public SonarCustomStateMapper() {
        this.renderers = new ArrayList<>();
        this.customModels = new HashMap<>();
		ModelLoaderRegistry.registerLoader(this);
	}

	@SideOnly(Side.CLIENT)
	public void registerCustomBlockRenderer(ISonarCustomRenderer renderer) {
		Block block = renderer.getBlock();
		BlockRenderer instance = new BlockRenderer<>(renderer);
		ModelResourceLocation blockModel = renderer.getBlockModelResourceLocation();

		//if (renderer.hasStaticRendering()) {
			ModelLoader.setCustomStateMapper(block, this);
			customModels.put(blockModel, instance);
		//}

		//TileEntity tile = renderer.getTileEntity();
		if (renderer instanceof ISonarTileRenderer) {
			ClientRegistry.bindTileEntitySpecialRenderer(((ISonarTileRenderer) renderer).getTileEntity(), instance);
		}
		if (renderer.doInventoryRendering()) {
            ModelResourceLocation itemModel = new ModelResourceLocation(blockModel.getResourceDomain() + ':' + blockModel.getResourcePath(), "inventory");
			//customModels.put(itemModel, instance);
			ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(block), renderer);
		}
		renderers.add(renderer);
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		Block block = state.getBlock();
		if (block instanceof ISonarRendererProvider) {
			return ((ISonarRendererProvider) block).getRenderer().getBlockModelResourceLocation();
		}
		return null;
	}

	@Override
	public boolean accepts(ResourceLocation loc) {
		return customModels.containsKey(loc);
	}

	@Override
	public IModel loadModel(ResourceLocation loc) throws Exception {
		return customModels.get(loc);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {

	}
}
