package sonar.core.handlers.inventories;

import java.util.List;

import appeng.api.networking.security.IActionHost;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.InventoryHandler;
import sonar.core.api.inventories.ISonarInventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.AE2Helper;
import sonar.core.inventory.handling.ItemTransferHelper;
/*
@InventoryHandler(modid = "appliedenergistics2", priority = 0)
public class AE2InventoryProvider implements ISonarInventoryHandler {

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile instanceof IGridProxyable;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IItemList<IAEItemStack> items = AE2Helper.getItemChannel(proxy.getProxy().getStorage()).getStorageList();
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
			IItemList<IAEItemStack> items = AE2Helper.getItemChannel(proxy.getProxy().getStorage()).getStorageList();
			if (items == null || items.isEmpty()) {
				return StorageSize.EMPTY;
			}
			for (IAEItemStack item : items) {
				ItemTransferHelper.addStackToList(storedStacks, AE2Helper.convertAEItemStack(item));
				maxStorage += item.getStackSize();
			}
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return new StorageSize(maxStorage, maxStorage);
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
            IAEItemStack stack = AE2Helper.getItemChannel(proxy.getProxy().getStorage()).injectItems(AE2Helper.convertStoredItemStack(add), AE2Helper.getActionable(action), new MachineSource((IActionHost) tile));
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
			IMEMonitor<IAEItemStack> itemChannel = AE2Helper.getItemChannel(proxy.getProxy().getStorage());
			StoredItemStack stack = AE2Helper.convertAEItemStack().extractItems(AE2Helper.convertStoredItemStack(remove), AE2Helper.getActionable(action), new MachineSource((IActionHost) tile)));
			if (stack == null || stack.getStackSize() == 0) {
				return null;
			}
			return stack;
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return remove;
	}

    @Override
    public boolean isLargeInventory() {
        return true;
    }
}
*/