package sonar.core.client.renderers;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nonnull;

public abstract class SonarRendererBase implements ISonarCustomRenderer {

	public final Block block;
	public final ModelResourceLocation location;
	public TextureAtlasSprite icon;
    public ArrayList<ResourceLocation> textures = new ArrayList<>();

	public SonarRendererBase(Block block, ModelResourceLocation location) {
		this.block = block;
		this.location = location;
	}

	public SonarRendererBase setBreakTexture(ResourceLocation location) {
		this.icon = this.getIcon(location);
		return this;
	}

	public SonarRendererBase addTexture(ResourceLocation location) {
		textures.add(location);
		return this;
	}

	@Nonnull
    @Override
	public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
		return getBlockModelResourceLocation();
	}

	@Override
	public ModelResourceLocation getBlockModelResourceLocation() {
		return location;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public boolean hasStaticRendering() {
		return false;
	}

	@Override
	public TextureAtlasSprite getIcon() {
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}

	@Override
	public boolean doInventoryRendering() {
		return true;
	}

	@Override
	public ArrayList<ResourceLocation> getAllTextures() {
		return textures;
	}

	public final TextureAtlasSprite getIcon(ResourceLocation loc) {
		if (loc == null)
			return ModelLoader.defaultTextureGetter().apply(loc);
		else
			return null;
	}
}
