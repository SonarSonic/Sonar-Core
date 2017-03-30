package sonar.core.handlers.inventories;

import java.util.Collection;
import java.util.List;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.InventoryHandler;
import sonar.core.api.inventories.ISonarInventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;

@InventoryHandler(modid = "refinedstorage", handlerID = RefinedStorageHandler.name, priority = 4)
public class RefinedStorageHandler implements ISonarInventoryHandler {

	public static final String name = "RefinedStorage";

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile instanceof INetworkNode;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {

		return null;// need implementing
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		INetworkNode node = (INetworkNode) tile;
		INetworkMaster network = node.getNetwork();
		if (network != null) {
			int toAdd = (int) Math.min(Integer.MAX_VALUE, add.stored);
			ItemStack stack = network.insertItem(add.getFullStack(), toAdd, action.shouldSimulate());
			add.stored -= stack == null ? toAdd : toAdd - stack.getCount();
		}
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		INetworkNode node = (INetworkNode) tile;
		INetworkMaster network = node.getNetwork();
		if (network != null) {
			int toRemove = (int) Math.min(Integer.MAX_VALUE, remove.stored);
			ItemStack stack = network.extractItem(remove.getFullStack(), toRemove, action.shouldSimulate());
			remove.stored -= stack == null ? 0 : stack.getCount();
		}
		return remove;
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		INetworkNode node = (INetworkNode) tile;
		INetworkMaster network = node.getNetwork();
		if (network != null) {
			Collection<ItemStack> stacks = network.getItemStorageCache().getList().getStacks();
			for (ItemStack stack : stacks) {
				SonarAPI.getItemHelper().addStackToList(storedStacks, new StoredItemStack(stack));
			}
		}
		return new StorageSize(0, 0); // doesn't show storage yet
	}

}
