package sonar.core.client.renderers;

import java.io.IOException;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
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

import javax.annotation.Nonnull;

public class SonarCustomStateMapper extends StateMapperBase implements ICustomModelLoader, IResourceManagerReloadListener, IResourceManager {

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
            ModelResourceLocation itemModel = new ModelResourceLocation(blockModel.getResourceDomain() + ':' + blockModel.getResourcePath(), "inventories");
			//customModels.put(itemModel, instance);
			ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(block), renderer);
		}
		renderers.add(renderer);
	}

	@Nonnull
    @Override
	protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
		Block block = state.getBlock();
		if (block instanceof ISonarRendererProvider) {
			return ((ISonarRendererProvider) block).getRenderer().getBlockModelResourceLocation();
		}
		return null;
	}

	@Override
	public boolean accepts(@Nonnull ResourceLocation loc) {
		return customModels.containsKey(loc);
	}

	@Nonnull
    @Override
	public IModel loadModel(@Nonnull ResourceLocation loc) {
		return customModels.get(loc);
	}

	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

	}

	@Override
	public Set<String> getResourceDomains() {
		return null;
	}

	@Override
	public IResource getResource(ResourceLocation location) throws IOException {
		return null;
	}

	@Override
	public List<IResource> getAllResources(ResourceLocation location) throws IOException {
		return null;
	}
}
