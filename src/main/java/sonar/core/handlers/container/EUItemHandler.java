package sonar.core.handlers.container;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import net.minecraft.item.ItemStack;
import sonar.core.api.asm.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

@EnergyContainerHandler(modid = "IC2", priority = 4)
public class EUItemHandler implements ISonarEnergyContainerHandler {

    @Override
    public boolean canHandleItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IElectricItem || stack.getItem() instanceof ISpecialElectricItem;
    }

    public static IElectricItemManager getManager(ItemStack stack) {
        if (stack.getItem() instanceof ISpecialElectricItem) {
            return ((ISpecialElectricItem) stack.getItem()).getManager(stack);
        }
        return ElectricItem.manager;
    }

    @Override
    public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
        IElectricItemManager manager = getManager(stack);
        double charge = Math.min(transfer.stored, manager.getMaxCharge(stack) - manager.getCharge(stack));
        transfer.stored -= manager.charge(stack, transfer.stored, 4, false, action.shouldSimulate());
        return transfer;
    }

    @Override
    public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
        IElectricItemManager manager = getManager(stack);
        double charge = Math.min(transfer.stored, manager.getCharge(stack));
        transfer.stored -= manager.discharge(stack, transfer.stored, 4, false, true, action.shouldSimulate());
        return transfer;
    }

    @Override
    public void getEnergy(StoredEnergyStack energyStack, ItemStack stack) {
        IElectricItemManager manager = getManager(stack);
        energyStack.setStorageValues((long) manager.getCharge(stack), (long) manager.getMaxCharge(stack));
    }

    @Override
    public EnergyType getProvidedType() {
        return EnergyType.EU;
    }

}