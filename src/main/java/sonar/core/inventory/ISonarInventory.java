package sonar.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.network.sync.IDirtyPart;

public interface ISonarInventory extends IInventory, IDirtyPart, INBTSyncable, IItemHandler {

    ISonarInventory setStackLimit(int limit);

	//public ISonarInventory setHandledSide(EnumFacing side);
	
    IItemHandler getItemHandler(EnumFacing side);
}
