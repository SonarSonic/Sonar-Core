package sonar.core.handlers.energy;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Loader;
import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

public class TeslaHandler extends EnergyHandler {

	public static String name = "TESLA-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return tile != null && (tile.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir) || tile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir) || tile.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, dir));
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		if (tile == null) {
			return;
		}
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, dir)) {
			ITeslaHolder holder = (ITeslaHolder) tile.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, dir);
			if (holder != null)
				energyStack.setStorageValues(holder.getStoredPower(), holder.getCapacity());
		}
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir)) {
			ITeslaConsumer consumer = (ITeslaConsumer) tile.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir);
			if (consumer != null) {
				long simulateAdd = consumer.givePower(Long.MAX_VALUE, true);
				energyStack.setMaxInput(simulateAdd);
			}
		}
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir)) {
			ITeslaProducer producer = (ITeslaProducer) tile.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir);
			if (producer != null) {
				long simulateAdd = producer.takePower(Long.MAX_VALUE, true);
				energyStack.setMaxInput(simulateAdd);
			}
		}
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir)) {
			ITeslaConsumer consumer = (ITeslaConsumer) tile.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir);
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
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir)) {
			ITeslaProducer producer = (ITeslaProducer) tile.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir);
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
		return Loader.isModLoaded("Tesla");
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.TESLA;
	}
}