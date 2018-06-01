package sonar.core.handlers.energy.items;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.api.energy.IItemEnergyHandler;
import sonar.core.api.asm.ItemEnergyHandler;

@ItemEnergyHandler(modid = "tesla", priority = 2)
public class ItemHandlerTesla implements IItemEnergyHandler {

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.TESLA;
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null);
    }

    @Override
    public boolean canReadEnergy(ItemStack stack) {
        return stack.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
    }

    @Override
    public long addEnergy(long add, ItemStack stack, ActionType actionType) {
        ITeslaConsumer consumer = stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
        return consumer.givePower(add, actionType.shouldSimulate());
    }

    @Override
    public long removeEnergy(long remove, ItemStack stack, ActionType actionType) {
        ITeslaProducer producer = stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null);
        return producer.takePower(remove, actionType.shouldSimulate());
    }

    @Override
    public long getStored(ItemStack stack) {
        return stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null).getStoredPower();
    }

    @Override
    public long getCapacity(ItemStack stack) {
        return stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null).getCapacity();
    }
}