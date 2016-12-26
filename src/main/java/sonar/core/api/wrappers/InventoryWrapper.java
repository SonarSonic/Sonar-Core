package sonar.core.api.wrappers;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import sonar.core.api.StorageSize;
import sonar.core.api.inventories.InventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.InventoryHelper.IInventoryFilter;

public class InventoryWrapper {

	/** convenience method, gets the stack to be added to the inventory from the remainder, can return null.
	 * @param inputSize
	 * @param stack
	 * @param returned
	 * @return */
	public StoredItemStack getStackToAdd(long inputSize, StoredItemStack stack, StoredItemStack returned) {
		return null;
	}

	/** convenient method, adds the given stack to the list, used by {@link InventoryHandler}
	 * @param list {@link StoredItemStack} list to add to
	 * @param stack {@link StoredItemStack} to combine */
	public void addStackToList(List<StoredItemStack> list, StoredItemStack stack) {}

	/** convenient method, adds the given inventory {@link IInventory} to the list, used by {@link InventoryHandler}
	 * @param list {@link StoredItemStack} list to add to
	 * @param inv {@link IInventory} to combine
	 * @return returns how many item */
	public StorageSize addInventoryToList(List<StoredItemStack> list, IInventory inv) {
		return StorageSize.EMPTY;
	}

	/** convenient method, adds the given ItemHandler {@link IItemHandler} to the list, used by {@link InventoryHandler}
	 * @param list {@link StoredItemStack} list to add to
	 * @param inv {@link IItemHandler} to combine
	 * @return returns how many item */
	public StorageSize addItemHandlerToList(List<StoredItemStack> list, IItemHandler inv) {
		return StorageSize.EMPTY;		
	}

	/** drops a full StoredItemStack on the floor
	 * @param drop {@link StoredItemStack} to drop
	 * @param world the world to drop it in
	 * @param x the X coordinate it will be dropped from
	 * @param y the Y coordinate it will be dropped from
	 * @param z the Z coordinate it will be dropped from
	 * @param side side to drop from */
	public void spawnStoredItemStack(StoredItemStack drop, World world, int x, int y, int z, EnumFacing side) {}

	public ItemStack addItems(TileEntity tile, StoredItemStack stack, ActionType type) {
		return null;
	}
	
	public ItemStack removeItems(TileEntity tile, StoredItemStack stack, ActionType type) {
		return null;
	}
	/**returns what was added*/
	public StoredItemStack addItems(TileEntity tile, StoredItemStack stack, EnumFacing dir, ActionType type, IInventoryFilter filter) {
		return stack;
	}
	/**returns what was extracted*/
	public StoredItemStack removeItems(TileEntity tile, StoredItemStack stack, EnumFacing dir, ActionType type, IInventoryFilter filter) {
		return stack;
	}

	public void transferItems(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, IInventoryFilter filter) {}
	
	public boolean isPlayerInventoryFull(EntityPlayer player) {
		return player.inventory.getFirstEmptyStack() == -1;
	}
}
