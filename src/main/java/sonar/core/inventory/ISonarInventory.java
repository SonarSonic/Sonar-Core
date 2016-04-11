package sonar.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;

public interface ISonarInventory extends IInventory {

	public ISonarInventory setStackLimit(int limit);

	public void readData(NBTTagCompound nbt, SyncType type);

	public void writeData(NBTTagCompound nbt, SyncType type);
	
}
