package sonar.core.handlers.container;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import sonar.core.api.asm.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

@EnergyContainerHandler(modid = "tesla", handlerID = TeslaItemHandler.name)
public class TeslaItemHandler implements ISonarEnergyContainerHandler {

	public static final String name = "TESLA Item Handler";

	@Override
	public boolean canHandleItem(ItemStack stack) {
		return stack.getItem() != null && (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null) || stack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null) || stack.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null));
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, ItemStack stack) {
		if (stack == null) {
			return;
		}
		if (stack.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)) {
			ITeslaHolder holder = (ITeslaHolder) stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
			if (holder != null)
				energyStack.setStorageValues(holder.getStoredPower(), holder.getCapacity());
		}
		if (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null)) {
			ITeslaConsumer consumer = (ITeslaConsumer) stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
			if (consumer != null) {
				long simulateAdd = consumer.givePower(Long.MAX_VALUE, true);
				energyStack.setMaxInput(simulateAdd);
			}
		}
		if (stack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null)) {
			ITeslaProducer producer = (ITeslaProducer) stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null);
			if (producer != null) {
				long simulateAdd = producer.takePower(Long.MAX_VALUE, true);
				energyStack.setMaxInput(simulateAdd);
			}
		}
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		if (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null)) {
			ITeslaConsumer consumer = (ITeslaConsumer) stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
			if (consumer != null) {
				int transferRF = transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE;
				transfer.stored -= consumer.givePower(transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		if (stack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null)) {
			ITeslaProducer producer = (ITeslaProducer) stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null);
			if (producer != null) {
				int transferRF = transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE;
				transfer.stored -= producer.takePower(transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("tesla");
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.TESLA;
	}

}
