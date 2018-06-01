package sonar.core.handlers.energy;

import net.minecraftforge.energy.IEnergyStorage;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

public class EnergyStorageWrapper implements IEnergyHandler {

    public IEnergyStorage storage;
    public EnumEnergyWrapperType wrapperType;
    public EnergyType type;

    public EnergyStorageWrapper(IEnergyStorage storage, EnumEnergyWrapperType wrapperType, EnergyType type){
        this.storage = storage;
        this.wrapperType = wrapperType;
        this.type = type;
    }

    @Override
    public EnumEnergyWrapperType getWrapperType() {
        return wrapperType;
    }

    @Override
    public EnergyType getEnergyType() {
        return type;
    }

    @Override
    public boolean canAddEnergy() {
        return storage.canReceive();
    }

    @Override
    public boolean canRemoveEnergy() {
        return storage.canExtract();
    }

    @Override
    public boolean canReadEnergy() {
        return true;
    }

    @Override
    public long addEnergy(long add, ActionType actionType) {
        return storage.receiveEnergy((int)Math.min(Integer.MAX_VALUE, add), actionType.shouldSimulate());
    }

    @Override
    public long removeEnergy(long remove, ActionType actionType) {
        return storage.extractEnergy((int)Math.min(Integer.MAX_VALUE, remove), actionType.shouldSimulate());
    }

    @Override
    public long getStored() {
        return storage.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return storage.getMaxEnergyStored();
    }
}
