package sonar.core.sync;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.utils.ActionType;

@Optional.InterfaceList({
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla"),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyStorage", modid = "redstoneflux")
})
public class SyncValueEnergyStorage extends EnergyStorage implements ISyncValue<Integer>, ITeslaConsumer, ITeslaProducer, ITeslaHolder, cofh.redstoneflux.api.IEnergyStorage {

    public final IValueWatcher watcher;

    public SyncValueEnergyStorage(IValueWatcher watcher){
        this(watcher, 10000);
    }

    public SyncValueEnergyStorage(IValueWatcher watcher, int capacity){
        this(watcher, capacity, capacity, capacity, 0);
    }

    public SyncValueEnergyStorage(IValueWatcher watcher, int capacity, int maxTransfer){
        this(watcher, capacity, maxTransfer, maxTransfer, 0);
    }

    public SyncValueEnergyStorage(IValueWatcher watcher, int capacity, int maxReceive, int maxExtract){
        this(watcher, capacity, maxReceive, maxExtract, 0);
    }

    public SyncValueEnergyStorage(IValueWatcher watcher, int capacity, int maxReceive, int maxExtract, int energy){
        super(capacity, maxReceive, maxExtract, energy);
        this.watcher = watcher;
        this.watcher.addSyncValue(this);
    }

    //// ENERGY STORAGE \\\\

    public SyncValueEnergyStorage setCapacity(int capacity){
        this.capacity = capacity;
        return this;
    }

    public SyncValueEnergyStorage setMaxTransfer(int maxTransfer) {
        setMaxReceive(maxTransfer);
        setMaxExtract(maxTransfer);
        return this;
    }

    public SyncValueEnergyStorage setMaxReceive(int maxReceive){
        this.maxReceive = maxReceive;
        return this;
    }

    public SyncValueEnergyStorage setMaxExtract(int maxExtract){
        this.maxExtract = maxExtract;
        return this;
    }

    public void setEnergyStored(int energy) {
        setValue(checkCapacity(energy));
    }

    public int getMaxReceive(){
        return maxReceive;
    }

    public int getMaxExtract(){
        return maxExtract;
    }

    public int checkCapacity(int energy){
        if (energy > capacity) {
            return capacity;
        } else if (energy < 0) {
            return 0;
        }
        return energy;
    }

    @Override
    public final int receiveEnergy(int maxReceive, boolean simulate){
        int prev_energy = energy;
        int received = super.receiveEnergy(maxReceive, simulate);
        if(prev_energy != energy){
            setDirty(true);
            watcher.onSyncValueChanged(this);
        }
        return received;
    }

    @Override
    public final int extractEnergy(int maxExtract, boolean simulate){
        int prev_energy = energy;
        int extracted = super.extractEnergy(maxReceive, simulate);
        if(prev_energy != energy){
            setDirty(true);
            watcher.onSyncValueChanged(this);
        }
        return extracted;
    }

    public final long removeEnergy(long maxTransferRF, ActionType actionType) {
        return extractEnergy((int)Math.min(maxTransferRF, Integer.MAX_VALUE), actionType.shouldSimulate());
    }

    public final long addEnergy(long maxTransferRF, ActionType actionType) {
        return receiveEnergy((int)Math.min(maxTransferRF, Integer.MAX_VALUE), actionType.shouldSimulate());
    }

    //// TESLA \\\\

    @Override
    public long givePower(long power, boolean simulated) {
        return receiveEnergy((int) Math.min(Integer.MAX_VALUE, power), simulated);
    }

    @Override
    public long getStoredPower() {
        return getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return getMaxEnergyStored();
    }

    @Override
    public long takePower(long power, boolean simulated) {
        return extractEnergy((int) Math.min(Integer.MAX_VALUE, power), simulated);
    }

    //// SYNCHRONISATION \\\\

    public static final ISyncHandler<Integer> handler = new EnergyStorageSyncHandler();
    public boolean isDirty;

    @Override
    public String getTagName() {
        return "energy";
    }

    @Override
    public ISyncHandler<Integer> getSyncHandler() {
        return handler;
    }

    @Override
    public Integer getValue() {
        return energy;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public boolean setValueInternal(Integer set) {
        if(energy != set) {
            energy = set;
            watcher.onSyncValueChanged(this);
            return true;
        }
        return false;
    }

    @Override
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public static class EnergyStorageSyncHandler extends SyncHandlerPrimitive.SyncHandlerInteger{

        private static final String OLD_TAG = "energyStorage";

        @Override
        public Integer load(NBTTagCompound tag, String key) {
            if(tag.hasKey(OLD_TAG)){ // to move the old Energy Storage syncing over without losing energy.
                NBTTagCompound nbt = tag.getCompoundTag(OLD_TAG);
                return nbt.getInteger("Energy");
            }
            return tag.getInteger(key);
        }

    }

}
