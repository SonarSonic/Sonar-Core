package sonar.core.handlers.inventories;
/*
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import sonar.core.api.ActionType;
import sonar.core.api.InventoryHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;

public class LPInventoryProvider extends InventoryHandler {

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile instanceof ILPPipeTile;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		List<ItemStack> items = getStackList(tile);
		if (slot < items.size()) {
			return new StoredItemStack(items.get(slot));
		}
		return null;
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		List<ItemStack> items = getStackList(tile);
		if (items == null || items.isEmpty()) {
			return StorageSize.EMPTY;
		}
		long maxStorage = 0;
		for (ItemStack stack : items) {
			SonarAPI.getItemHelper().addStackToList(storedStacks, new StoredItemStack(stack));
			maxStorage +=stack.stackSize;
		}
		return new StorageSize(maxStorage,maxStorage);
	}

	public List<ItemStack> getStackList(TileEntity tile) {
		if (tile instanceof ILPPipeTile) {
			ILPPipe pipe = ((ILPPipeTile) tile).getLPPipe();
			if (pipe instanceof IRequestAPI) {
				IRequestAPI request = (IRequestAPI) pipe;
				List<ItemStack> items = request.getProvidedItems();
				return items;
			}
		}
		return Lists.newArrayList();
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("LogisticsPipes");
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof ILPPipeTile) {
			ILPPipe pipe = ((ILPPipeTile) tile).getLPPipe();
			if (pipe instanceof IRequestAPI) {
				IRequestAPI request = (IRequestAPI) pipe;
				if (!action.shouldSimulate()) {
					List<ItemStack> missing = request.performRequest(remove.getFullStack());
					if (missing == null) {
						return null;
					}
					long removed = remove.stored;
					for (ItemStack stack : missing) {
						removed -= stack.stackSize;
					}
					remove.setStackSize(removed);
				} else {
					SimulationResult result = request.simulateRequest(remove.getFullStack());
					if (result.missing == null) {
						return null;
					}
					long removed = remove.stored;
					for (ItemStack stack : result.missing) {
						removed -= stack.stackSize;
					}
					remove.setStackSize(removed);
				}
			}
		}
		return remove;
	}
}
*/