package sonar.core.handlers.inventories.handling;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleChangeableHandler implements IItemHandler {

   final World world;
   final BlockPos pos;
   final EnumFacing face;
   TileEntity tile;
   IItemHandler handler;

    public SimpleChangeableHandler(World world, BlockPos pos, EnumFacing face){
        this.world = world;
        this.pos = pos;
        this.face = face;
        this.handler = getItemHandler();
    }

    @Nullable
    public IItemHandler getItemHandler(){
       if(handler == null || tile == null || tile.isInvalid()){
           tile = world.getTileEntity(pos);
           if(tile != null && tile.hasCapability(ItemTransferHelper.ITEM_HANDLER_CAPABILITY, face)){
               handler = tile.getCapability(ItemTransferHelper.ITEM_HANDLER_CAPABILITY, face);
           }
       }
       return handler;
    }

    @Override
    public int getSlots() {
        return handler != null ? handler.getSlots() : 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler != null ? handler.getStackInSlot(slot) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return handler != null ? handler.insertItem(slot, stack, simulate) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return handler != null ? handler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler != null ? handler.getSlotLimit(slot) : 0;
    }

    public boolean isValid(){
        return getItemHandler() != null;
    }
}
