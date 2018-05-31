package sonar.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandlerModifiable;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.inventory.handling.EnumFilterType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SonarInventorySideWrapper implements IItemHandlerModifiable {

    public final ISonarInventory inventory;
    public final EnumFacing face;

    public static NonNullList<IItemHandlerModifiable> initWrappers(ISonarInventory inventory){
        NonNullList<IItemHandlerModifiable> list = NonNullList.create();
        for(EnumFacing face : EnumFacing.values()){
            list.add(face.ordinal(), new SonarInventorySideWrapper(inventory, face));
        }
        list.add(6, new SonarInventorySideWrapper(inventory, null));
        return list;
    }

    public static IItemHandlerModifiable getHandlerForSide(NonNullList<IItemHandlerModifiable> list, EnumFacing face){
        return list.get(face==null ? 6 : face.ordinal());
    }

    public SonarInventorySideWrapper(ISonarInventory inventory, @Nullable EnumFacing face){
        this.inventory = inventory;
        this.face = face;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(inventory.checkInsert(slot, stack, face, EnumFilterType.EXTERNAL)){
            return inventory.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(inventory.checkExtract(slot, amount, face, EnumFilterType.EXTERNAL)){
            return inventory.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(slot);
    }

}