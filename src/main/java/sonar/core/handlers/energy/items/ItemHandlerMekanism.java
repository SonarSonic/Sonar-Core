package sonar.core.handlers.energy.items;

import mekanism.api.energy.IEnergizedItem;
import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.api.energy.IItemEnergyHandler;
import sonar.core.api.asm.ItemEnergyHandler;

@ItemEnergyHandler(modid = "mekanism", priority = 4)
public class ItemHandlerMekanism implements IItemEnergyHandler {

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.MJ;
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergizedItem && ((IEnergizedItem)stack.getItem()).canReceive(stack);
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergizedItem && ((IEnergizedItem)stack.getItem()).canSend(stack);
    }

    @Override
    public boolean canReadEnergy(ItemStack stack) {
        return stack.getItem() instanceof IEnergizedItem;
    }

    @Override
    public long addEnergy(long add, ItemStack stack, ActionType actionType) {
        IEnergizedItem energizedItem = (IEnergizedItem)stack.getItem();
        long added = (long)Math.min(energizedItem.getMaxTransfer(stack), Math.min(energizedItem.getMaxEnergy(stack) - energizedItem.getEnergy(stack), add));
        if(!actionType.shouldSimulate()) {
            energizedItem.setEnergy(stack, energizedItem.getEnergy(stack) + added);
        }
        return added;
    }

    @Override
    public long removeEnergy(long remove, ItemStack stack, ActionType actionType) {
        IEnergizedItem energizedItem = (IEnergizedItem)stack.getItem();
        long removed = (long)Math.min(energizedItem.getMaxTransfer(stack), Math.min(energizedItem.getEnergy(stack), remove));
        if(!actionType.shouldSimulate()) {
            energizedItem.setEnergy(stack, energizedItem.getEnergy(stack) - removed);
        }
        return removed;
    }

    @Override
    public long getStored(ItemStack stack) {
        return (long)((IEnergizedItem)stack.getItem()).getEnergy(stack);
    }

    @Override
    public long getCapacity(ItemStack stack) {
        return (long)((IEnergizedItem)stack.getItem()).getMaxEnergy(stack);
    }
}
