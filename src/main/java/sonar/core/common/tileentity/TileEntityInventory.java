package sonar.core.common.tileentity;

import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.handlers.inventories.SonarInventoryTile;

import java.util.List;

public class TileEntityInventory extends TileEntitySonar implements ISonarInventoryTile {

	public final SonarInventoryTile inv = new SonarInventoryTile(this);
	{
		syncList.addPart(inv);
	}

	public TileEntityInventory() {}

	public ISonarInventory inv() {
		return inv;
	}

	public List<ItemStack> slots(){
		return inv.slots();
	}

}
