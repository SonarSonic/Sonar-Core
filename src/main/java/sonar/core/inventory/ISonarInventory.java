package sonar.core.inventory;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.network.sync.IDirtyPart;

public interface ISonarInventory extends IInventory, IDirtyPart, INBTSyncable, IItemHandler {

    ISonarInventory setStackLimit(int limit);
    IItemHandler getItemHandler(EnumFacing side);
	List<ItemStack> slots();
}
