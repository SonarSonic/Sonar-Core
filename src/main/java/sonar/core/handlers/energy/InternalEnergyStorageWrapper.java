package sonar.core.handlers.energy;

import cofh.redstoneflux.api.IEnergyStorage;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.network.sync.SyncEnergyStorage;

@Optional.InterfaceList({
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla"),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyStorage", modid = "redstoneflux")
})

public class InternalEnergyStorageWrapper implements net.minecraftforge.energy.IEnergyStorage, IEnergyStorage, ITeslaConsumer, ITeslaProducer, ITeslaHolder, IEnergyHandler {

    public final SyncEnergyStorage storage;
    public final EnumFacing face;

    public InternalEnergyStorageWrapper(SyncEnergyStorage storage, EnumFacing face){
        this.storage = storage;
        this.face = face;
    }

    ///// * CoFH *//////
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) storage.addEnergy(maxReceive, face, ActionType.getTypeForAction(simulate));
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) storage.removeEnergy(maxExtract, face, ActionType.getTypeForAction(simulate));
    }

    public int getEnergyStored() {
        return (int) Math.min(storage.getEnergyLevel(), Integer.MAX_VALUE);
    }

    public int getMaxEnergyStored() {
        return (int) Math.min(storage.getFullCapacity(), Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract(face);
    }

    @Override
    public boolean canReceive() {
        return storage.canReceive(face);
    }

    ///// * TESLA *//////
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
        return storage.removeEnergy(Math.min(Integer.MAX_VALUE, power), face, ActionType.getTypeForAction(simulated));
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return storage.addEnergy(power, face, ActionType.getTypeForAction(simulated));
    }

    @Override
    public EnumEnergyWrapperType getWrapperType() {
        return face == null ? EnumEnergyWrapperType.INTERNAL_TILE_STORAGE : EnumEnergyWrapperType.EXTERNAL_TILE;
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.FE;
    }

    @Override
    public boolean canAddEnergy() {
        return canReceive();
    }

    @Override
    public boolean canRemoveEnergy() {
        return canExtract();
    }

    @Override
    public boolean canReadEnergy() {
        return true;
    }

    @Override
    public boolean canRenderConnection() {
        return true;
    }

    @Override
    public long addEnergy(long maxReceive, ActionType action) {
        return storage.addEnergy(maxReceive, face, action);
    }

    @Override
    public long removeEnergy(long maxExtract, ActionType action) {
        return storage.removeEnergy(maxExtract, face, action);
    }

    @Override
    public long getStored() {
        return this.getEnergyStored();
    }

    public long getEnergyLevel() {
        return storage.getEnergyLevel();
    }

    public long getFullCapacity() {
        return storage.getFullCapacity();
    }
}
