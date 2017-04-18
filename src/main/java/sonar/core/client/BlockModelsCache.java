package sonar.core.client;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import sonar.core.helpers.RenderHelper;

///AE3 Code: All credit goes to Elix_x https://github.com/AEModernMCPort/Applied-Energistics-3/blob/codename-andromeda/src/main/java/appeng/core/lib/client/render/model/ModelsCache.java
public enum BlockModelsCache implements IResourceManagerReloadListener {

	INSTANCE;

	public static final IModelState DEFAULTMODELSTATE = opt -> Optional.absent();
	public static final VertexFormat DEFAULTVERTEXFORMAT = DefaultVertexFormats.BLOCK;
	public static final Function<ResourceLocation, TextureAtlasSprite> DEFAULTTEXTUREGETTER = texture -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());

	private final Map<ResourceLocation, IModel> cache = new HashMap<>();
	private final Map<ResourceLocation, IBakedModel> bakedCache = new HashMap<>();

	public IModel getOrLoadModel(ResourceLocation location) {
		IModel model = cache.get(location);
		if (model == null) {
			try {
				model = ModelLoaderRegistry.getModel(location);
			} catch (Exception e) {
				e.printStackTrace();
				model = ModelLoaderRegistry.getMissingModel();
			}
			cache.put(location, model);
		}
		return model;
	}

	public IBakedModel getModel(ResourceLocation key) {
		return bakedCache.get(key);
	}

	public IBakedModel getOrLoadModel(ResourceLocation key, ResourceLocation location, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		IBakedModel model = getModel(key);
		if (model == null) {
			model = getOrLoadModel(location).bake(state, format, textureGetter);
			bakedCache.put(key, model);
		}
		return model;
	}

	public IBakedModel getOrLoadModel(ItemStack stack) {
		ResourceLocation location = stack.getItem().getRegistryName();
		String hashCode = (stack.hasTagCompound() ? "" + stack.getTagCompound().hashCode() : "");
		ResourceLocation key = new ResourceLocation(location.toString() + stack.getMetadata() + hashCode);

		IBakedModel model = getModel(key);
		if (model == null) {
			model = RenderHelper.itemRender.getItemModelWithOverrides(stack, null, null);
			bakedCache.put(key, model);
		}
		return model;
	}

	public IBakedModel getOrLoadModel(ResourceLocation key, ResourceLocation location, IModelState state, VertexFormat format) {
		return getOrLoadModel(key, location, state, format, DEFAULTTEXTUREGETTER);
	}

	public IBakedModel getOrLoadModel(ResourceLocation key, ResourceLocation location, IModelState state, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(key, location, state, DEFAULTVERTEXFORMAT, textureGetter);
	}

	public IBakedModel getOrLoadModel(ResourceLocation key, ResourceLocation location, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(key, location, DEFAULTMODELSTATE, format, textureGetter);
	}

	public IBakedModel getOrLoadModel(ResourceLocation key, ResourceLocation location, IModelState state) {
		return getOrLoadModel(key, location, state, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	public IBakedModel getOrLoadModel(ResourceLocation key, ResourceLocation location, VertexFormat format) {
		return getOrLoadModel(key, location, DEFAULTMODELSTATE, format, DEFAULTTEXTUREGETTER);
	}

	public IBakedModel getOrLoadModel(ResourceLocation key, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		return getOrLoadModel(key, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, textureGetter);
	}

	public IBakedModel getOrLoadModel(ResourceLocation key, ResourceLocation location) {
		return getOrLoadModel(key, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	public IBakedModel getOrLoadBakedModel(ResourceLocation location) {
		return getOrLoadModel(location, location, DEFAULTMODELSTATE, DEFAULTVERTEXFORMAT, DEFAULTTEXTUREGETTER);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		cache.clear();
		bakedCache.clear();
	}

}