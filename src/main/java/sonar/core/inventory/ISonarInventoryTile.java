package sonar.core.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

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

	@Nonnull
    default ItemStack getStackInSlot(int index) {
		return inv().getStackInSlot(index);
	}

	@Nonnull
    default ItemStack decrStackSize(int index, int count) {
		return inv().decrStackSize(index, count);
	}

	@Nonnull
    default ItemStack removeStackFromSlot(int index) {
		return inv().removeStackFromSlot(index);
	}

	default void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
		inv().setInventorySlotContents(index, stack);
	}

	default int getInventoryStackLimit() {
		return inv().getInventoryStackLimit();
	}

	default void markDirty() {
		inv().markDirty();
	}

	default boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
		return inv().isUsableByPlayer(player);
	}

	default void openInventory(@Nonnull EntityPlayer player) {
		inv().openInventory(player);
	}

	default void closeInventory(@Nonnull EntityPlayer player) {
		inv().closeInventory(player);
	}

	default boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
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
	
    @Nonnull
    default String getName(){
    	return inv().getName();
    }

    default boolean hasCustomName(){
    	return inv().hasCustomName();
    }

    @Nonnull
    default ITextComponent getDisplayName(){
    	return inv().getDisplayName();
    }
}
