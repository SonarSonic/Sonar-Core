package sonar.core.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

/**make sure you still add the inventory to the synclist!*/
public interface ISonarInventoryTile extends IInventory {

	ISonarInventory inv();
	
	default List<ItemStack> slots() {
		return inv().slots();
	}
	default int getSizeInventory() {
		return inv().getSizeInventory();
	}

	default boolean isEmpty() {
		return inv().isEmpty();
	}

	default ItemStack getStackInSlot(int index) {
		return inv().getStackInSlot(index);
	}

	default ItemStack decrStackSize(int index, int count) {
		return inv().decrStackSize(index, count);
	}

	default ItemStack removeStackFromSlot(int index) {
		return inv().removeStackFromSlot(index);
	}

	default void setInventorySlotContents(int index, ItemStack stack) {
		inv().setInventorySlotContents(index, stack);
	}

	default int getInventoryStackLimit() {
		return inv().getInventoryStackLimit();
	}

	default void markDirty() {
		inv().markDirty();
	}

	default boolean isUsableByPlayer(EntityPlayer player) {
		return inv().isUseableByPlayer(player);
	}
	
	default boolean isUseableByPlayer(EntityPlayer player) {
		return inv().isUseableByPlayer(player);
	}

	default void openInventory(EntityPlayer player) {
		inv().openInventory(player);
	}

	default void closeInventory(EntityPlayer player) {
		inv().closeInventory(player);
	}

	default boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv().isItemValidForSlot(index, stack);
	}

	default int getField(int id) {
		return inv().getField(id);
	}

	default void setField(int id, int value) {
		inv().setField(id, value);
	}

	default int getFieldCount() {
		return inv().getFieldCount();
	}

	default void clear() {
		inv().clear();
	}
	
    default String getName(){
    	return inv().getName();
    }

    default boolean hasCustomName(){
    	return inv().hasCustomName();
    }

    default ITextComponent getDisplayName(){
    	return inv().getDisplayName();
    }
}
