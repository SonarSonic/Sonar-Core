package sonar.core.client.renderers;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public interface ISonarTileRenderer extends ISonarCustomRenderer {

	public Class<? extends TileEntity> getTileEntity();
	
	public TileEntitySpecialRenderer getTileRenderer();
}
