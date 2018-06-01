package sonar.core.handlers.inventories.containers;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import sonar.core.common.tileentity.TileEntityLargeInventory;
import sonar.core.handlers.inventories.handling.ItemTransferHelper;
import sonar.core.handlers.inventories.handling.SlotSonarFiltered;
import sonar.core.handlers.inventories.slots.SlotChangeableStack;

import javax.annotation.Nonnull;

//TODO make compatible with all types of item transfer
//use only {@link SlotChangeableStack}
public class ContainerLargeInventory extends ContainerSync {

    public TileEntityLargeInventory tile;

    public ContainerLargeInventory(TileEntityLargeInventory tile) {
        super(tile, tile);
        this.tile = tile;
    }


    public boolean addToInventory(ItemStack stack){
        ItemStack inserted = ItemHandlerHelper.insertItemStacked(tile.inv, stack.copy(), false);
        if(inserted.getCount() != stack.getCount()){
            stack.shrink(stack.getCount() - inserted.getCount());
            detectAndSendChanges();
            return true;
        }
        return false;
    }

    public boolean removeToPlayer(EntityPlayer player, ItemStack stack, int targetSlot){
        int before = tile.inv.getStackInSlot(targetSlot).getCount();
        ItemTransferHelper.doTransferFromSlot(tile.inv, Lists.newArrayList(ItemTransferHelper.getMainInventoryHandler(player)), targetSlot);
        if(before != tile.inv.getStackInSlot(targetSlot).getCount()) {
            detectAndSendChanges();
            return true;
        }
        return false;
    }

    public boolean slotHasStack(Slot slot){
        return slot instanceof SlotSonarFiltered ? tile.inv.slots.get(slot.getSlotIndex()).getActualStored() > 0 : slot.getHasStack();
    }

    public ItemStack getRemovalStack(Slot slot){
        return slot instanceof SlotSonarFiltered ? tile.inv.slots.get(slot.getSlotIndex()).getLargeStack().getFullStack() : slot.getStack();
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index){
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slotHasStack(slot)){
            ItemStack itemstack1 = getRemovalStack(slot);
            itemstack = itemstack1.copy();

            if (index < tile.inv.getSlots()){
                this.removeToPlayer(player, itemstack1, index);

                return ItemStack.EMPTY;
            }
            else if (!this.addToInventory(itemstack1)){
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()){
                slot.putStack(ItemStack.EMPTY);
            }
            else{
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    public void onContainerClosed(EntityPlayer player){
        super.onContainerClosed(player);
        tile.closeInventory(player);
    }

    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        Slot slot = slotId >= 0 && slotId < inventorySlots.size() ? inventorySlots.get(slotId) : null;
        if(slot instanceof SlotChangeableStack){
            if(clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.PICKUP_ALL){
                if(slotHasStack(slot)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
}
