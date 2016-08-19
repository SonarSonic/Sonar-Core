package sonar.core.inventory;

import net.minecraft.inventory.IInventory;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.network.sync.IDirtyPart;

public interface ISonarInventory extends IInventory, IDirtyPart, INBTSyncable {

	public ISonarInventory setStackLimit(int limit);

}
