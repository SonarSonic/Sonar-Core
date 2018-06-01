package sonar.core.handlers.energy;

import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.IItemEnergyHandler;
import sonar.core.api.utils.ActionType;

public class ItemHandlingWrapper implements IEnergyHandler {

    public ItemStack stack;
    public IItemEnergyHandler handler;
    public boolean canAdd;
    public boolean canRemove;
    public boolean canRead;
    public EnergyType type;

    public ItemHandlingWrapper(ItemStack stack, IItemEnergyHandler handler){
        this.stack = stack;
        this.handler = handler;
        this.canAdd = handler.canAddEnergy(stack);
        this.canRemove = handler.canRemoveEnergy(stack);
        this.canRead = handler.canReadEnergy(stack);
        this.type = handler.getEnergyType();
    }

    public EnergyType getEnergyType(){
        return type;
    }

    public boolean canAddEnergy(){
        return canAdd;
    }

    public boolean canRemoveEnergy(){
        return canRemove;
    }

    @Override
    public boolean canReadEnergy() {
        return canRead;
    }

    public long addEnergy(long add, ActionType actionType){
        return handler.addEnergy(add, stack, actionType);
    }

    public long removeEnergy(long remove, ActionType actionType){
        return handler.removeEnergy(remove, stack, actionType);
    }

    @Override
    public long getStored() {
        return handler.getStored(stack);
    }

    @Override
    public long getCapacity() {
        return handler.getCapacity(stack);
    }
}
