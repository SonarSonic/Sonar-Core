package sonar.core.api;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.InventoryHandler.StorageSize;
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

	/** drops a full StoredItemStack on the floor
	 * @param drop {@link StoredItemStack} to drop
	 * @param world the world to drop it in
	 * @param x the X coordinate it will be dropped from
	 * @param y the Y coordinate it will be dropped from
	 * @param z the Z coordinate it will be dropped from
	 * @param side side to drop from */
	public void spawnStoredItemStack(StoredItemStack drop, World world, int x, int y, int z, ForgeDirection side) {
	}

	public StoredItemStack addItems(TileEntity tile, StoredItemStack stack, ForgeDirection dir, ActionType type, IInventoryFilter filter) {
		return stack;
	}

	public StoredItemStack removeItems(TileEntity tile, StoredItemStack stack, ForgeDirection dir, ActionType type, IInventoryFilter filter) {
		return null;
	}

	public void transferItems(TileEntity from, TileEntity to, ForgeDirection dirFrom, ForgeDirection dirTo, IInventoryFilter filter) {}
	
	public boolean isPlayerInventoryFull(EntityPlayer player) {
		return player.inventory.getFirstEmptyStack() == -1;
	}
}
