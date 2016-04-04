package sonar.core.handlers.inventories;
/*
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import sonar.core.api.ActionType;
import sonar.core.api.InventoryHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;
import sonar.core.integration.AE2Helper;

public class AE2InventoryProvider extends InventoryHandler {

	public static String name = "AE2-Inventory";

	@Override
	public String getName() {
		return name;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile instanceof IGridProxyable;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IStorageGrid storage = proxy.getProxy().getStorage();
			IItemList<IAEItemStack> items = storage.getItemInventory().getStorageList();
			if (items == null) {
				return null;
			}
			int current = 0;
			for (IAEItemStack item : items) {
				if (current == slot) {
					return AE2Helper.convertAEItemStack(item);
				}
				current++;
			}
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		long maxStorage = 0;
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IStorageGrid storage = proxy.getProxy().getStorage();
			IItemList<IAEItemStack> items = storage.getItemInventory().getStorageList();
			if (items == null) {
				return StorageSize.EMPTY;
			}
			for (IAEItemStack item : items) {
				SonarAPI.getItemHelper().addStackToList(storedStacks, AE2Helper.convertAEItemStack(item));
				maxStorage += item.getStackSize();
			}
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return new StorageSize(maxStorage, maxStorage);
	}

	public IItemList<IAEItemStack> getItemList(TileEntity tile, EnumFacing dir) {
		IStorageMonitorable monitor = ((ITileStorageMonitorable) tile).getMonitorable(dir, new MachineSource(((IActionHost) tile)));
		if (monitor != null) {
			IMEMonitor<IAEItemStack> stacks = monitor.getItemInventory();
			IItemList<IAEItemStack> items = stacks.getAvailableItems(AEApi.instance().storage().createItemList());
			return items;
		}
		return null;
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IStorageGrid storage = proxy.getProxy().getStorage();
			IAEItemStack stack = storage.getItemInventory().injectItems(AE2Helper.convertStoredItemStack(add), AE2Helper.getActionable(action), new MachineSource(((IActionHost) tile)));
			if (stack == null || stack.getStackSize() == 0) {
				return null;
			}
			return AE2Helper.convertAEItemStack(stack);
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IStorageGrid storage = proxy.getProxy().getStorage();
			StoredItemStack stack = SonarAPI.getItemHelper().getStackToAdd(remove.stored, remove, AE2Helper.convertAEItemStack(storage.getItemInventory().extractItems(AE2Helper.convertStoredItemStack(remove), AE2Helper.getActionable(action), new MachineSource(((IActionHost) tile)))));
			if (stack == null || stack.getStackSize() == 0) {
				return null;
			}
			return stack;
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return remove;
	}

}
*/