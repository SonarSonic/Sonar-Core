package sonar.core.handlers.inventories;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import sonar.core.api.ActionType;
import sonar.core.api.InventoryHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;

public class DrawersInventoryProvider extends InventoryHandler {

	public static String name = "Storage Drawers";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile instanceof IDrawerGroup;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		if (tile instanceof IDrawerGroup) {
			IDrawerGroup drawers = (IDrawerGroup) tile;
			if (slot < drawers.getDrawerCount()) {
				IDrawer draw = drawers.getDrawer(slot);
				ItemStack item = draw.getStoredItemCopy();
				if (item != null) {
					return new StoredItemStack(item);
				} else {
					return null;
				}
			}
		}

		return null;
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		if (tile instanceof IDrawerGroup) {
			IDrawerGroup drawers = (IDrawerGroup) tile;
			long maxStorage = 0;
			long stored=0;
			for (int i = 0; i < drawers.getDrawerCount(); i++) {
				if (drawers.getDrawer(i) != null) {
					IDrawer draw = drawers.getDrawer(i);					
					ItemStack item = draw.getStoredItemCopy();
					maxStorage+=draw.getMaxCapacity();
					stored+=draw.getStoredItemCount();
					if (item != null){						
						SonarAPI.getItemHelper().addStackToList(storedStacks, new StoredItemStack(item, draw.getStoredItemCount()));
					}
				}
			}
			return new StorageSize(stored,maxStorage);
		}
		return StorageSize.EMPTY;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("StorageDrawers");
	}

	private long injectItemsIntoDrawer(IDrawer drawer, long itemCount) {
		return itemCount;
	}

	/**
	 * taken from DrawerMEInventory class in StorageDrawers <a href=
	 * "https://github.com/jaquadro/StorageDrawers/blob/master/src/com/jaquadro/minecraft/storagedrawers/integration/ae2/DrawerMEInventory.java"
	 * >Storage Drawers GitHub</a>
	 */
	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		return remove;
	}
}
