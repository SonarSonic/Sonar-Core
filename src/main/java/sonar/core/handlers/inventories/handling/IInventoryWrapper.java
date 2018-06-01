package sonar.core.handlers.inventories.handling;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.IItemHandlerModifiable;

/**only for internal use*/
public class IInventoryWrapper implements IInventory {

    public final IItemHandlerModifiable handler;
    public TileEntity tile = null;

    public IInventoryWrapper(IItemHandlerModifiable handler, TileEntity tile){
        this.handler = handler;
        this.tile = tile;
    }

    public IInventoryWrapper(IItemHandlerModifiable handler){
        this.handler = handler;
    }

    @Override
    public int getSizeInventory() {
        return handler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < getSizeInventory(); i++){
            if(!getStackInSlot(i).isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return handler.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return handler.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return handler.extractItem(index, handler.getSlotLimit(index), false);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        handler.setStackInSlot(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        if(tile != null){
            tile.markDirty();
        }
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if(tile != null){
            return player.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= 64.0D;
        }
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        ItemStack returned = handler.insertItem(index, stack.copy(), true);
        return returned.getCount() != stack.getCount();
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {}

    @Override
    public String getName() {
        return tile != null ? tile.getBlockType().getLocalizedName() :"Wrapper Item Handler";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(tile != null ? tile.getBlockType().getUnlocalizedName() :getName());
    }
}
