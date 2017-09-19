package sonar.core.client.renderers;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public interface ISonarTileRenderer extends ISonarCustomRenderer {

    Class<? extends TileEntity> getTileEntity();
	
    TileEntitySpecialRenderer getTileRenderer();
}
