package sonar.core.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import sonar.core.helpers.RenderHelper;

public abstract class SonarTERender extends TileEntitySpecialRenderer {
	public ModelBase model;
	public String texture;

	public SonarTERender(ModelBase model, String texture) {
		this.model = model;
		this.texture = texture;
	}

	public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(te), texture);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		RenderHelper.finishRender();
		renderExtras(te, x, y, z, alpha);
	}

	/** for extra rotations and translations to be added, or rendering
	 * effects */
	public void renderExtras(TileEntity entity, double x, double y, double z, float f) {

	}
}
