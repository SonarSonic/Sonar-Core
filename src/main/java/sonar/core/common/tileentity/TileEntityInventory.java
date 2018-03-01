package sonar.core.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.inventory.ISonarInventory;
import sonar.core.inventory.ISonarInventoryTile;

public class TileEntityInventory extends TileEntitySonar implements ISonarInventoryTile {

	protected ISonarInventory inv;

	public TileEntityInventory() {}

	public ISonarInventory inv() {
		return inv;
	}
}
