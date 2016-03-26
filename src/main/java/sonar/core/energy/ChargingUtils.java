package sonar.core.energy;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import net.minecraft.item.ItemStack;
import sonar.core.integration.SonarLoader;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;

/** used for charging/discharging stacks into energy storage */
public class ChargingUtils {

	/**
	 * @param energy ItemStack to discharge
	 * @param energyStorage storage to discharge into
	 * @return if stack has a discharge value
	 */
	public static boolean canDischarge(ItemStack energy, EnergyStorage energyStorage) {
		if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored())
			return false;

		if (energy == null) {
			return false;
		}

		if (energy.getItem() instanceof IEnergyContainerItem) {
			return true;
		}

		if (SonarLoader.ic2Loaded()) {
			if (energy.getItem() instanceof IElectricItem) {
				return true;
			}
		}
		if (DischargeValues.getValueOf(energy) > 0) {
			return true;
		}

		return false;

	}

	/**
	 * @param energy ItemStack to charge
	 * @param energyStorage storage to charge from
	 * @return if stack can be charged
	 */
	public static boolean canCharge(ItemStack energy, EnergyStorage energyStorage) {
		if (energyStorage.getEnergyStored() == 0)
			return false;

		if (energy == null) {
			return false;
		}

		if (energy.getItem() instanceof IEnergyContainerItem) {
			return true;
		}

		if (SonarLoader.ic2Loaded()) {
			if (energy.getItem() instanceof IElectricItem) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param energy ItemStack to discharge
	 * @param energyStorage storage to discharge to
	 * @return EnergyCharge with new ItemStack and EnergyStorage
	 */
	public static EnergyCharge discharge(ItemStack energy, EnergyStorage energyStorage) {
		int max = energyStorage.getMaxEnergyStored();
		int stored = energyStorage.getEnergyStored();
		if (energy != null && stored < max) {
			if (energy.getItem() instanceof IEnergyContainerItem) {
				IEnergyContainerItem item = (IEnergyContainerItem) energy.getItem();

				int itemEnergy = item.getEnergyStored(energy);
				int toTransfer = Math.round(Math.min(itemEnergy, ((max - stored))));
				return new EnergyCharge(+item.extractEnergy(energy, toTransfer, false), energy, false);

			} else if (SonarLoader.ic2Loaded() && energy.getItem() instanceof ISpecialElectricItem) {
				ISpecialElectricItem special = (ISpecialElectricItem) energy.getItem();
				IElectricItemManager manager = special.getManager(energy);

				int itemEnergyRF = (int) manager.getCharge(energy) * 4;
				int toTransferRF = Math.round(Math.min(itemEnergyRF, ((max - stored))));
				return new EnergyCharge((int) (manager.discharge(energy, toTransferRF, 4, false, false, false) * 4), energy, false);
			} else if (SonarLoader.ic2Loaded() && energy.getItem() instanceof IElectricItem) {
				IElectricItem item = (IElectricItem) energy.getItem();
				IElectricItemManager manager = ElectricItem.manager;

				int itemEnergyRF = (int) manager.getCharge(energy) * 4;
				int toTransferRF = Math.round(Math.min(itemEnergyRF, ((energyStorage.getMaxEnergyStored() - stored))));
				return new EnergyCharge((int) (manager.discharge(energy, toTransferRF, 4, false, false, false) * 4), energy, false);
			} else if (stored + DischargeValues.getValueOf(energy) <= energyStorage.getMaxEnergyStored()) {
				return new EnergyCharge(+DischargeValues.getValueOf(energy), energy, true);
			}
		}
		return new EnergyCharge(0, energy, false);
	}

	/**
	 * @param energy ItemStack to charge
	 * @param energyStorage storage to charge from
	 * @return EnergyCharge with new ItemStack and EnergyStorage
	 */
	public static EnergyCharge charge(ItemStack energy, EnergyStorage energyStorage, int maxTransfer) {
		int max = energyStorage.getMaxEnergyStored();
		int stored = energyStorage.getEnergyStored();
		if (energy != null && stored > 0) {
			if (energy.getItem() instanceof IEnergyContainerItem) {
				IEnergyContainerItem item = (IEnergyContainerItem) energy.getItem();
				int itemEnergy = (int) Math.round(Math.min(Math.sqrt(item.getMaxEnergyStored(energy)), item.getMaxEnergyStored(energy) - item.getEnergyStored(energy)));
				int toTransfer = Math.min(maxTransfer, Math.round(Math.min(itemEnergy, (energyStorage.getEnergyStored()))));

				return new EnergyCharge(-(item.receiveEnergy(energy, toTransfer, false)), energy, false);
			} else if (SonarLoader.ic2Loaded() && energy.getItem() instanceof IElectricItem) {
				IElectricItem item = (IElectricItem) energy.getItem();
				IElectricItemManager manager = ElectricItem.manager;

				int itemEnergyRF = (int) Math.round(Math.min(Math.sqrt(item.getMaxCharge(energy) * 4), (item.getMaxCharge(energy) * 4) - (manager.getCharge(energy) * 4)));
				int toTransferRF = Math.min(maxTransfer, Math.round(Math.min(itemEnergyRF, stored)));

				energyStorage.setEnergyStored((int) (stored - (manager.charge(energy, toTransferRF / 4, 4, false, false) * 4)));
				return new EnergyCharge(-(int) (manager.charge(energy, toTransferRF / 4, 4, false, false) * 4), energy, false);
			} else if (SonarLoader.ic2Loaded() && energy.getItem() instanceof ISpecialElectricItem) {
				ISpecialElectricItem item = (ISpecialElectricItem) energy.getItem();
				IElectricItemManager manager = item.getManager(energy);

				int itemEnergyRF = (int) Math.round(Math.min(Math.sqrt(item.getMaxCharge(energy) * 4), (item.getMaxCharge(energy) * 4) - (manager.getCharge(energy) * 4)));
				int toTransferRF = Math.min(maxTransfer, Math.round(Math.min(itemEnergyRF, stored)));

				return new EnergyCharge(-(int) (manager.charge(energy, toTransferRF / 4, 4, false, false) * 4), energy, false);
			}
		}
		return new EnergyCharge(0, energy, false);

	}
}
