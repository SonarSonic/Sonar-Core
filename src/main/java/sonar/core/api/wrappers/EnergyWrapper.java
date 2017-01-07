package sonar.core.api.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

public class EnergyWrapper {

	/** convenience method, gets the energy to be added from the remainder, can return null.
	 * 
	 * @param inputSize maximum size to add
	 * @param stack the original stack
	 * @param returned the returned stack
	 * @return */
	public StoredEnergyStack getStackToAdd(long inputSize, StoredEnergyStack stack, StoredEnergyStack returned) {
		return null;
	}

	/** for adding energy to a TileEntity from a certain side, compatible with all Energy Types available in SonarCore, can be Simulated if required
	 * 
	 * @param tile the {@link TileEntity} to add to
	 * @param maxExtract the amount of energy to add in RF/TESLA
	 * @param dir the {@link EnumFacing} to add from
	 * @param type the {@link ActionType} should this action be performed or simulated
	 * @return the amount of energy added, could be 0 */
	public long receiveEnergy(TileEntity tile, long maxReceive, EnumFacing dir, ActionType type) {
		return 0;
	}

	/** for extracting energy from a TileEntity from a certain side, compatible with all Energy Types available in SonarCore, can be Simulated if required
	 * 
	 * @param tile the {@link TileEntity} to extract from
	 * @param maxExtract the amount of energy to extract in RF/TESLA
	 * @param dir the {@link EnumFacing} to extract from
	 * @param type the {@link ActionType} should this action be performed or simulated
	 * @return the amount of energy extracted, could be 0 */
	public long extractEnergy(TileEntity tile, long maxExtract, EnumFacing dir, ActionType type) {
		return 0;
	}

	/** for adding energy to a ItemStack, compatible with all Energy Types available in SonarCore, can be Simulated if required
	 * 
	 * @param tile the {@link ItemStack} to add to
	 * @param maxExtract the amount of energy to add in RF/TESLA
	 * @param type the {@link ActionType} should this action be performed or simulated
	 * @return the amount of energy added, could be 0 */
	public long receiveEnergy(ItemStack stack, long maxReceive, ActionType type) {
		return 0;
	}

	/** for extracting energy from a ItemStack, compatible with all Energy Types available in SonarCore, can be Simulated if required
	 * 
	 * @param tile the {@link ItemStack} to extract from
	 * @param maxExtract the amount of energy to extract in RF/TESLA
	 * @param type the {@link ActionType} should this action be performed or simulated
	 * @return the amount of energy extracted, could be 0 */
	public long extractEnergy(ItemStack stack, long maxExtract, ActionType type) {
		return 0;
	}

	/** used for a direct transfer from one TileEntity to another (convenience method), compatible with all Energy Types available in SonarCore, can be Simulated if required
	 * 
	 * @param from the {@link TileEntity} to extract from
	 * @param to the {@link TileEntity} to add to
	 * @param dirFrom the {@link EnumFacing} to extract from
	 * @param dirTo the {@link EnumFacing} to add to
	 * @param maxTransferRF the amount of energy to transfer in RF/TESLA
	 * @return the amount of energy successfully transferred */
	public long transferEnergy(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, final long maxTransferRF) {
		return 0;
	}

	/** used for charging items (convenience method), compatible with all Energy Types available in SonarCore, can be Simulated if required
	 * 
	 * @param item the {@link ItemStack} to charge
	 * @param tile the {@link TileEntity} to charge from. It must be a {@link ISonarEnergyTile} for which siding is irrelevant
	 * @param dir the {@link EnumFacing} to extract from
	 * @param maxTransferRF the amount of energy to transfer in RF/TESLA
	 * @return the charged item */
	public ItemStack chargeItem(ItemStack item, TileEntity tile, final long maxTransferRF) {
		return item;
	}

	/** used for discharging items (convenience method), compatible with all Energy Types available in SonarCore, can be Simulated if required
	 * 
	 * @param item the {@link ItemStack} to discharge
	 * @param tile the {@link TileEntity} to discharge to. It must be a {@link ISonarEnergyTile} for which siding is irrelevant
	 * @param dir the {@link EnumFacing} to add to
	 * @param maxTransferRF the amount of energy to transfer in RF/TESLA
	 * @return the discharged item */
	public ItemStack dischargeItem(ItemStack item, TileEntity tile, final long maxTransferRF) {
		return item;
	}

	public StoredEnergyStack getEnergyStored(ItemStack stack, EnergyType format) {
		return new StoredEnergyStack(format);
	}

	/** gets the {@link StandardEnergyHandler} to use with a specific TileEntity from a specific side */
	public ISonarEnergyHandler canTransferEnergy(TileEntity tile, EnumFacing dir) {
		return null;
	}

	/** gets the {@link EnergyContainerHandler} to use with a specific ItemStack */
	public ISonarEnergyContainerHandler canTransferEnergy(ItemStack stack) {
		return null;
	}
}