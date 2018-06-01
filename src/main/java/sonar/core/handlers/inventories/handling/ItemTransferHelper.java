package sonar.core.handlers.inventories.handling;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sonar.core.api.StorageSize;
import sonar.core.api.inventories.StoredItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class ItemTransferHelper {

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    @Nullable
    public static IItemHandler getItemHandlerOffset(World world, BlockPos pos, EnumFacing face){
        return getItemHandler(world, pos.offset(face), face.getOpposite());
    }

    @Nullable
    public static IItemHandler getItemHandler(World world, BlockPos pos, EnumFacing face){
        TileEntity tile = world.getTileEntity(pos);
        return getItemHandler(tile, face);
    }

    @Nullable
    public static IItemHandler getItemHandler(ICapabilityProvider provider, EnumFacing face){
        if(provider != null && provider.hasCapability(ITEM_HANDLER_CAPABILITY, face)){
            return provider.getCapability(ITEM_HANDLER_CAPABILITY, face);
        }
        return null;
    }

    @Nonnull
    public static IItemHandler getMainInventoryHandler(EntityPlayer player){
        return getItemHandler(player, EnumFacing.UP);
    }

    @Nonnull
    public static IItemHandler getEquipmentInventoryHandler(EntityPlayer player){
        return getItemHandler(player, EnumFacing.NORTH);
    }

    @Nonnull
    public static IItemHandler getJoinedInventoryHandler(EntityPlayer player){
        return getItemHandler(player, null);
    }

    /**if the handler is an changeable handler this will ensure the current handler is valid, also performs a null check*/
    public static boolean isInvalidItemHandler(IItemHandler handler){
        return handler == null || (handler instanceof SimpleChangeableHandler && !((SimpleChangeableHandler)handler).isValid());
    }

    public static void doSimpleTransfer(Iterable<IItemHandler> sources, Iterable<IItemHandler> destinations, Predicate<ItemStack> filter, int maximum){
        int usage = maximum == -1 ? Integer.MAX_VALUE : maximum;
        for(IItemHandler source : sources){
            if(isInvalidItemHandler(source)){
                continue;
            }
            for(int i = 0; i < source.getSlots(); i++){
                ItemStack extract = source.extractItem(i, Math.min(usage, source.getSlotLimit(i)), true);
                if(!extract.isEmpty() && filter.test(extract)){
                    int count_before = extract.getCount();
                    extract = doInsert(extract, destinations);
                    int count_change = count_before - extract.getCount();
                    usage -= count_change;
                    source.extractItem(i, Math.min(count_change, source.getSlotLimit(i)), false);
                    if(usage <= 0){
                        return;
                    }
                }
            }
        }
    }

    /**unlimited transfer*/
    public static void doSimpleTransfer(Iterable<IItemHandler> sources, Iterable<IItemHandler> destinations, Predicate<ItemStack> filter){
        for(IItemHandler source : sources){
            if(isInvalidItemHandler(source)){
                continue;
            }
            for(int i = 0; i < source.getSlots(); i++){
                ItemStack extract = source.extractItem(i, source.getSlotLimit(i), true);
                if(!extract.isEmpty() && filter.test(extract)){
                    int count_before = extract.getCount();
                    extract = doInsert(extract, destinations);
                    int count_change = count_before - extract.getCount();
                    source.extractItem(i, Math.min(count_change, source.getSlotLimit(i)), false);
                }
            }
        }
    }

    public static void doTransferFromSlot(IItemHandler source, Iterable<IItemHandler> destinations, int sourceSlot){
        if(isInvalidItemHandler(source)){
            return;
        }
        ItemStack extract = source.extractItem(sourceSlot, source.getSlotLimit(sourceSlot), true);
        if(!extract.isEmpty()){
            int count_before = extract.getCount();
            extract = doInsert(extract, destinations);
            int count_change = count_before - extract.getCount();
            source.extractItem(sourceSlot, Math.min(count_change, source.getSlotLimit(sourceSlot)), false);
        }
    }

    /**inserts the given stack into the various item handlers
     * the inserted stack will be modified*/
    public static ItemStack doInsert(ItemStack insert, Iterable<IItemHandler> destinations){
        for(IItemHandler destination : destinations){
            if(isInvalidItemHandler(destination)){
                continue;
            }
            insert = ItemHandlerHelper.insertItemStacked(destination, insert, false);
            if(insert.isEmpty()){
                return insert;
            }
        }
        return insert;
    }

    public static ItemStack doExtract(Iterable<IItemHandler> sources, Predicate<ItemStack> filter, int maximum){
        ItemStack extracted = ItemStack.EMPTY;
        SOURCES: for(IItemHandler source : sources){
            if(isInvalidItemHandler(source)){
                continue;
            }
            for(int i = 0; i < source.getSlots(); i++) {
                ItemStack extract = source.extractItem(i, Math.min(source.getSlotLimit(i), maximum - extracted.getCount()), true);
                if(!extract.isEmpty() && filter.test(extract)){
                   extract = source.extractItem(i, extract.getCount(), false);
                   if(extracted.isEmpty()){
                       extracted = extract;
                   }else{
                       extracted.grow(extract.getCount());
                   }
                   if(extracted.getCount() >= maximum){
                       break SOURCES;
                   }
                }
            }
        }
        return extracted;
    }

    @Deprecated
    public static StorageSize addInventoryToList(List<StoredItemStack> list, IInventory inv) {
        long stored = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stored += stack.getCount();
                addStackToList(list, inv.getStackInSlot(i));
            }
        }
        int max = inv.getInventoryStackLimit() * inv.getSizeInventory();
        return new StorageSize(stored, max);
    }

    @Deprecated
    public static StorageSize addItemHandlerToList(List<StoredItemStack> list, IItemHandler inv) {
        long stored = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stored += stack.getCount();
                addStackToList(list, stack);
            }
        }
        return new StorageSize(stored, inv.getSlots() * 64); // guessing the max size is 64...
    }

    @Deprecated
    public static void addStackToList(List<StoredItemStack> list, ItemStack stack) {
        int pos = 0;
        for (StoredItemStack storedStack : list) {
            if (storedStack.equalStack(stack)) {
                list.get(pos).add(stack);
                return;
            }
            pos++;
        }
        list.add(new StoredItemStack(stack));
    }

    @Deprecated
    public static void addStackToList(List<StoredItemStack> list, StoredItemStack stack) {
        if (stack == null || list == null) {
            return;
        }
        int pos = 0;
        for (StoredItemStack storedStack : list) {
            if (storedStack.equalStack(stack.item)) {
                list.get(pos).add(stack);
                return;
            }
            pos++;
        }
        list.add(stack);
    }

}
