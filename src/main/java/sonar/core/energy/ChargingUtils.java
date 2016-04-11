package sonar.core.energy;

import net.minecraft.item.ItemStack;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;

/** used for charging/discharging stacks into energy storage */
public class ChargingUtils {

	/** @param energy ItemStack to discharge
	 * @param energyStorage storage to discharge into
	 * @return if stack has a discharge value */
	public static boolean canDischarge(ItemStack energy, EnergyStorage energyStorage) {
		if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored())
			return false;

		if (energy == null) {
			return false;
		}

		if (energy.getItem() instanceof IEnergyContainerItem) {
			return true;
		}
		if (DischargeValues.getValueOf(energy) > 0) {
			return true;
		}

		return false;

	}

	/** @param energy ItemStack to charge
	 * @param energyStorage storage to charge from
	 * @return if stack can be charged */
	public static boolean canCharge(ItemStack energy, EnergyStorage energyStorage) {
		if (energyStorage.getEnergyStored() == 0)
			return false;

		if (energy == null) {
			return false;
		}

		if (energy.getItem() instanceof IEnergyContainerItem) {
			return true;
		}
		return false;
	}

	/** @param energy ItemStack to discharge
	 * @param energyStorage storage to discharge to
	 * @return EnergyCharge with new ItemStack and EnergyStorage */
	public static EnergyCharge discharge(ItemStack energy, EnergyStorage energyStorage) {
		int max = energyStorage.getMaxEnergyStored();
		int stored = energyStorage.getEnergyStored();
		if (energy != null && stored < max) {
			if (energy.getItem() instanceof IEnergyContainerItem) {
				IEnergyContainerItem item = (IEnergyContainerItem) energy.getItem();
				
				int itemEnergy = Math.min(energyStorage.getMaxReceive(), item.getEnergyStored(energy));
				int toTransfer = Math.round(Math.min(itemEnergy, ((max - stored))));
				return new EnergyCharge(+item.extractEnergy(energy, toTransfer, false), energy, false);
			} else if (stored + DischargeValues.getValueOf(energy) <= energyStorage.getMaxEnergyStored()) {
				return new EnergyCharge(+DischargeValues.getValueOf(energy), energy, true);
			}
		}
		return new EnergyCharge(0, energy, false);
	}

	/** @param energy ItemStack to charge
	 * @param energyStorage storage to charge from
	 * @return EnergyCharge with new ItemStack and EnergyStorage */
	public static EnergyCharge charge(ItemStack energy, EnergyStorage energyStorage, int maxTransfer) {
		int max = energyStorage.getMaxEnergyStored();
		int stored = energyStorage.getEnergyStored();
		if (energy != null && stored > 0) {
			if (energy.getItem() instanceof IEnergyContainerItem) {
				IEnergyContainerItem item = (IEnergyContainerItem) energy.getItem();
				int itemEnergy = (int) Math.round(Math.min(Math.sqrt(item.getMaxEnergyStored(energy)), item.getMaxEnergyStored(energy) - item.getEnergyStored(energy)));
				int toTransfer = Math.min(maxTransfer, Math.round(Math.min(itemEnergy, (energyStorage.getEnergyStored()))));

				return new EnergyCharge(-(item.receiveEnergy(energy, toTransfer, false)), energy, false);
			}
		}
		return new EnergyCharge(0, energy, false);

	}
}
