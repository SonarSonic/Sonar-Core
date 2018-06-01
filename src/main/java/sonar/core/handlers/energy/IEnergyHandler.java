package sonar.core.handlers.energy;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

/**used internally to unify ITEM/TILE addition/removal*/
public interface IEnergyHandler {

    EnumEnergyWrapperType getWrapperType();

    EnergyType getEnergyType();

    boolean canAddEnergy();

    boolean canRemoveEnergy();

    boolean canReadEnergy();

    /**returns how much energy was added*/
    long addEnergy(long add, ActionType actionType);

    /**returns how much energy was removed*/
    long removeEnergy(long remove, ActionType actionType);

    long getStored();

    long getCapacity();

}
