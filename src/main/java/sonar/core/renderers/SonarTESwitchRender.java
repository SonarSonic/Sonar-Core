package sonar.core.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public abstract class SonarTESwitchRender extends TileEntitySpecialRenderer {
	public ModelBase model;
	public String on, off;

	public SonarTESwitchRender(ModelBase model, String on, String off) {
		this.model = model;
		this.on = on;
		this.off = off;
	}

	/*@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f, int par) {
		if (entity!=null && entity.getWorld() != null) {
			if (isActive(entity)) {
				RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), on);
			} else {
				RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), off);
			}
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			renderExtras(entity, x, y, z, f);
			RenderHelper.finishRender();
		} else {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), on);
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			renderExtras(entity, x, y, z, f);
			RenderHelper.finishRender();
		}
	}*/

    /**
     * for extra rotations and translations to be added, or rendering effects
     */
	public void renderExtras(TileEntity entity, double x, double y, double z, float f) {

	}

	public abstract boolean isActive(TileEntity entity);
}
