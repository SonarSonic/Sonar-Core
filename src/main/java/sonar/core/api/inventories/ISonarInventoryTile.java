package sonar.core.api.inventories;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import sonar.core.inventory.handling.EnumFilterType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**make sure you still add the inventory to the synclist!*/
public interface ISonarInventoryTile extends IInventory, ISidedInventory {

	ISonarInventory inv();

	//default List<ItemStack> slots() { return inv().slots(); }

	default void onInventoryContentsChanged(int slot){}

	@Nullable
	default Boolean checkExtract(int slot, int amount, @Nullable EnumFacing face, EnumFilterType type){ return null; }

	@Nullable
	default Boolean checkInsert(int slot, @Nonnull ItemStack stack, @Nullable EnumFacing face, EnumFilterType type){ return null; }

	//// IInventory \\\\


	default int getSizeInventory() {
		return inv().getWrapperInventory().getSizeInventory();
	}

	default boolean isEmpty() {
		return inv().getWrapperInventory().isEmpty();
	}

	@Nonnull
    default ItemStack getStackInSlot(int index) {
		return inv().getWrapperInventory().getStackInSlot(index);
	}

	@Nonnull
    default ItemStack decrStackSize(int index, int count) {
		return inv().getWrapperInventory().decrStackSize(index, count);
	}

	@Nonnull
    default ItemStack removeStackFromSlot(int index) {
		return inv().getWrapperInventory().removeStackFromSlot(index);
	}

	default void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
		inv().getWrapperInventory().setInventorySlotContents(index, stack);
	}

	default int getInventoryStackLimit() {
		return inv().getWrapperInventory().getInventoryStackLimit();
	}

	default void markDirty() {
		inv().getWrapperInventory().markDirty();
	}

	default boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
		return inv().getWrapperInventory().isUsableByPlayer(player);
	}

	default void openInventory(@Nonnull EntityPlayer player) {
		inv().getWrapperInventory().openInventory(player);
	}

	default void closeInventory(@Nonnull EntityPlayer player) {
		inv().getWrapperInventory().closeInventory(player);
	}

	default boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
		return inv().getWrapperInventory().isItemValidForSlot(index, stack);
	}

	default int getField(int id) {
		return inv().getWrapperInventory().getField(id);
	}

	default void setField(int id, int value) {
		inv().getWrapperInventory().setField(id, value);
	}

	default int getFieldCount() {
		return inv().getWrapperInventory().getFieldCount();
	}

	default void clear() {
		inv().getWrapperInventory().clear();
	}

    @Nonnull
    default String getName(){
    	return inv().getWrapperInventory().getName();
    }

    default boolean hasCustomName(){
    	return inv().getWrapperInventory().hasCustomName();
    }

    @Nonnull
    default ITextComponent getDisplayName(){
    	return inv().getWrapperInventory().getDisplayName();
    }

    //// ISidedInventory \\\\

	default int[] getSlotsForFace(EnumFacing side){
		return inv().getDefaultSlots();
	}

	default boolean canInsertItem(int index, ItemStack stack, EnumFacing direction){
		return inv().getItemHandler(direction).insertItem(index, stack.copy(), true).getCount() != stack.getCount();
	}

	default boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return inv().getItemHandler(direction).extractItem(index, stack.getCount(), true).getCount() != stack.getCount();
	}
}
