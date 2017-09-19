package sonar.core.energy;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

public class GenericTeslaHandler {

	public static boolean canProvideEnergy(ICapabilityProvider tile, EnumFacing dir) {
		return tile != null && (tile.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir) || tile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir) || tile.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, dir));
	}

	public static StoredEnergyStack getEnergy(StoredEnergyStack energyStack, ICapabilityProvider tile, EnumFacing dir) {
		if (tile == null) {
			return energyStack;
		}
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, dir)) {
            ITeslaHolder holder = tile.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, dir);
			if (holder != null)
				energyStack.setStorageValues(holder.getStoredPower(), holder.getCapacity());
		}
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir)) {
            ITeslaConsumer consumer = tile.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir);
			if (consumer != null) {
				long simulateAdd = consumer.givePower(Long.MAX_VALUE, true);
				energyStack.setMaxInput(simulateAdd);
			}
		}
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir)) {
            ITeslaProducer producer = tile.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir);
			if (producer != null) {
				long simulateAdd = producer.takePower(Long.MAX_VALUE, true);
				energyStack.setMaxInput(simulateAdd);
			}
		}
		return energyStack;
	}

	public static StoredEnergyStack addEnergy(StoredEnergyStack transfer, ICapabilityProvider tile, EnumFacing dir, ActionType action) {
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir)) {
            ITeslaConsumer consumer = tile.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir);
			if (consumer != null) {
				int transferRF = transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE;
				transfer.stored -= consumer.givePower(transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	public static StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ICapabilityProvider tile, EnumFacing dir, ActionType action) {
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir)) {
            ITeslaProducer producer = tile.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir);
			if (producer != null) {
				int transferRF = transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE;
				transfer.stored -= producer.takePower(transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}
}