package sonar.core.client.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import sonar.core.utils.helpers.RenderHelper;

public abstract class SonarTERender extends TileEntitySpecialRenderer {
	public ModelBase model;
	public String texture;

	public SonarTERender(ModelBase model, String texture) {
		this.model = model;
		this.texture = texture;
	}

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
		RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
		model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		renderExtras(entity, x, y, z, f);
		RenderHelper.finishRender();
	}

	/** for extra rotations and translations to be added, or rendering effects */
	public void renderExtras(TileEntity entity, double x, double y, double z, float f) {

	}

}
