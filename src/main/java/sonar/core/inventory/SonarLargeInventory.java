package sonar.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import sonar.core.api.inventories.ISonarLargeInventory;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.inventory.handling.EnumFilterType;
import sonar.core.inventory.handling.IInventoryWrapper;
import sonar.core.inventory.handling.filters.IExtractFilter;
import sonar.core.inventory.handling.filters.IInsertFilter;
import sonar.core.inventory.handling.filters.SlotHelper;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncableListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SonarLargeInventory implements ISonarLargeInventory {

    private NonNullList<IItemHandlerModifiable> sided_handlers = SonarInventorySideWrapper.initWrappers(this);
    private Map<IInsertFilter, EnumFilterType> insert_filters = new HashMap<>();
    private Map<IExtractFilter, EnumFilterType> extract_filters = new HashMap<>();
    public boolean default_external_insert_result = false;
    public boolean default_external_extract_result = false;
    public boolean default_internal_insert_result = true;
    public boolean default_internal_extract_result = true;

    public NonNullList<InventoryLargeSlot> slots;
    public long stackSize;

    protected IInventory wrapped_inv = null;
    private int[] defaultSlots = null;

    public SonarLargeInventory(){
        this(1, 64);
    }

    public SonarLargeInventory(int size, long stackSize){
        this.stackSize = stackSize;
        setSize(size);
    }

    @Override
    public IInventory getWrapperInventory() {
        return wrapped_inv == null ? wrapped_inv = new IInventoryWrapper(this) : wrapped_inv;
    }

    @Override
    public IItemHandlerModifiable getItemHandler(EnumFacing side) {
        return SonarInventorySideWrapper.getHandlerForSide(sided_handlers, side);
    }

    @Override
    public int[] getDefaultSlots() {
        if(defaultSlots == null){
            defaultSlots = new int[getSlots()];
            for(int i =0; i< defaultSlots.length; i++){
                defaultSlots[i] = i;
            }
        }
        return defaultSlots;
    }

    public void setSize(int size){
        this.slots = NonNullList.create();
        for(int i = 0; i < size ; i++){
            slots.add(new InventoryLargeSlot(this, i));
        }
    }

    public void setStackSize(int stackSize){
        this.stackSize = stackSize;
    }

    @Override
    public int getSlots() {
        return slots.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return slots.get(slot).getAccessStack();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        validateSlotIndex(slot);
        if(checkInsert(slot, stack, null, EnumFilterType.INTERNAL)) {
            return slots.get(slot).insertItem(stack, simulate);
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlotIndex(slot);
        if(checkExtract(slot, amount, null, EnumFilterType.INTERNAL)) {
            return slots.get(slot).extractItem(amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Map<IInsertFilter, EnumFilterType> getInsertFilters() {
        return insert_filters;
    }

    @Override
    public Map<IExtractFilter, EnumFilterType> getExtractFilters() {
        return extract_filters;
    }

    @Override
    public boolean checkInsert(int slot, @Nonnull ItemStack stack, @Nullable EnumFacing face, EnumFilterType internal){
        boolean insert = internal.matches(EnumFilterType.INTERNAL) ? default_internal_insert_result : default_external_insert_result;
        return SlotHelper.checkInsert(slot, stack, face, internal, this, insert) && slots.get(slot).checkInsert(stack, face, internal);
    }

    @Override
    public boolean checkExtract(int slot, int count, @Nullable EnumFacing face, EnumFilterType internal){
        boolean extract = internal.matches(EnumFilterType.INTERNAL) ? default_internal_extract_result : default_external_extract_result;
        return SlotHelper.checkExtract(slot, count, face, internal, this, extract) && slots.get(slot).checkExtract(count, face, internal);
    }

    @Override
    public List<ItemStack> getDrops() {
        List<ItemStack> toDrop = new ArrayList<>();
        slots.forEach(s -> toDrop.addAll(s.getDrops()));
        return toDrop;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        slots.get(slot).setStackInSlot(stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return (int) Math.min(64, stackSize);
    }

    private void validateSlotIndex(int slot){
        if (slot < 0 || slot >= slots.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slots.size() + ")");
        }
    }

    @Override
    public void readData(NBTTagCompound nbt, NBTHelper.SyncType type) {
        if (type.isType(NBTHelper.SyncType.SAVE, NBTHelper.SyncType.DEFAULT_SYNC)) {
            if (nbt.hasKey(getTagName())) {// legacy support
                NBTTagList list = nbt.getTagList(getTagName(), 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound compound = list.getCompoundTagAt(i);
                    int b = compound.getInteger("Slot");
                    if (b >= 0 && b < slots.size()) {
                        slots.get(b).readData(compound, type);
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeData(NBTTagCompound nbt, NBTHelper.SyncType type) {
        if (type.isType(NBTHelper.SyncType.SAVE, NBTHelper.SyncType.DEFAULT_SYNC)) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < slots.size(); i++) {
                NBTTagCompound compound = new NBTTagCompound();
                slots.get(i).writeData(compound, type);
                if(!compound.hasNoTags()) {
                    compound.setInteger("Slot", i);
                    list.appendTag(compound);
                }
            }
            if(!list.hasNoTags()) {
                nbt.setTag(getTagName(), list);
            }
        }
        return nbt;
    }

    protected void onContentsChanged(int slot){
        markChanged();
    }

    //// SYNCING \\\\

    protected ISyncableListener listener;

    @Override
    public IDirtyPart setListener(ISyncableListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public ISyncableListener getListener() {
        return listener;
    }

    public void markChanged() {
        if (listener != null)
            listener.markChanged(this);
    }

    @Override
    public String getTagName() {
        return "Items";
    }

    @Override
    public boolean canSync(NBTHelper.SyncType sync) {
        return sync.isType(getSyncTypes());
    }

    public NBTHelper.SyncType[] getSyncTypes() {
        return new NBTHelper.SyncType[] { NBTHelper.SyncType.SAVE, NBTHelper.SyncType.DEFAULT_SYNC };
    }

    @Override
    public long getStackSize(int slot) {
        return slots.get(slot).getActualStored();
    }

    @Override
    public StoredItemStack getStoredStack(int slot) {
        return slots.get(slot).getLargeStack();
    }

    public static class InventoryLargeSlot implements INBTSyncable {

        public final SonarLargeInventory inventory;
        public final int slot;

        public InventoryLargeSlot(SonarLargeInventory inventory, int slot){
            this.inventory = inventory;
            this.slot = slot;
        }

        private ItemStack stored_stack = ItemStack.EMPTY;
        private ItemStack access_stack = ItemStack.EMPTY;
        ////the active size without the access stack size
        private long active_size = 0;

        private void updateState(){

            if(stored_stack.isEmpty() || getActualStored() <= 0){
                stored_stack = ItemStack.EMPTY;
                access_stack = ItemStack.EMPTY;
                active_size = 0;
            }else {
                active_size += access_stack.getCount();
                access_stack = ItemStack.EMPTY;
                access_stack = createAccessStack();
                active_size -= access_stack.getCount();
            }
            inventory.markChanged();
        }

        private ItemStack createAccessStack(){
            long toFill = inventory.stackSize - getActualStored();
            if(toFill < stored_stack.getMaxStackSize()){
                ItemStack access = stored_stack.copy();
                access.setCount(Math.min(stored_stack.getMaxStackSize(), inventory.getSlotLimit(slot)) - (int)toFill);
                return access;
            }
            return ItemStack.EMPTY;
        }

        public long getActualStored(){
            return active_size + access_stack.getCount();
        }

        public StoredItemStack getLargeStack(){
            return new StoredItemStack(stored_stack, getActualStored());
        }

        public ItemStack getAccessStack(){
            return access_stack;
        }

        public List<ItemStack> getDrops(){
            List<ItemStack> drops = new ArrayList<>();
            long drop = getActualStored();
            while(drop > 0){
                int change = (int) Math.min(stored_stack.getMaxStackSize(), drop);
                ItemHandlerHelper.copyStackWithSize(stored_stack, change);
                drop-=change;
            }
            return drops;
        }

        public void setStackInSlot(@Nonnull ItemStack stack) {

            if(this.stored_stack.isEmpty() && !stack.isEmpty()){
                this.stored_stack = stack.copy();
            }
            access_stack = stack;
            updateState();
            inventory.onContentsChanged(slot);
        }

        @Override
        public void readData(NBTTagCompound nbt, NBTHelper.SyncType type) {
            stored_stack = new ItemStack(nbt);
           active_size = nbt.getLong("stored");
            access_stack = ItemStack.EMPTY;
            updateState();
        }

        @Override
        public NBTTagCompound writeData(NBTTagCompound nbt, NBTHelper.SyncType type) {
            stored_stack.writeToNBT(nbt);
            nbt.setLong("stored", getActualStored());
            return nbt;
        }

        private void doExtract(int extract){
            active_size -= extract;
            updateState();
            inventory.onContentsChanged(slot);
        }

        private void doInsert(int insert){
            active_size += insert;
            updateState();
            inventory.onContentsChanged(slot);
        }

        @Nonnull
        public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate) {
            if(stack.isEmpty() || !stored_stack.isEmpty() && !StoredItemStack.isEqualStack(stack, stored_stack)){
                return stack;
            }
            int canInsert = Math.min(stack.getCount(), (int)Math.min(Integer.MAX_VALUE, inventory.stackSize - getActualStored()));
            if(canInsert != 0){
                if(!simulate){
                    if(stored_stack.isEmpty()){
                        stored_stack = stack.copy();
                    }
                    doInsert(canInsert);
                }
                return ItemHandlerHelper.copyStackWithSize(stored_stack, stack.getCount() - canInsert);
            }
            return stack;
        }

        @Nonnull
        public ItemStack extractItem(int amount, boolean simulate) {
            if(getActualStored() <= 0 || amount <= 0 || stored_stack.isEmpty()){
                return ItemStack.EMPTY;
            }
            int canExtract = (int)Math.min(Math.min(amount, stored_stack.getMaxStackSize()), getActualStored());
            ItemStack extracted = ItemHandlerHelper.copyStackWithSize(stored_stack, canExtract);
            if(!simulate){
                doExtract(canExtract);
            }
            return extracted;
        }

        public boolean checkInsert(@Nonnull ItemStack stack, @Nullable EnumFacing face, EnumFilterType internal){
            return stored_stack.isEmpty() || StoredItemStack.isEqualStack(stack, stored_stack) && inventory.stackSize > getActualStored();
        }

        public boolean checkExtract(int count, @Nullable EnumFacing face, EnumFilterType internal){
            return getActualStored() > 0;
        }
    }
}
