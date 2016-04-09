package sonar.core.api.energy;

import sonar.core.api.SonarAPI;
import sonar.core.api.SonarHandler;
import sonar.core.api.utils.ActionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/** used for providing information on Block/TileEntity for the Info Reader to read, the Provider must be registered in the {@link SonarAPI} to be used */
public abstract class EnergyHandler extends SonarHandler {

	public int getID() {
		return SonarAPI.getRegistry().getEnergyHandlerID(getName());
	}

	/** the {@link EnergyType} this provider can handle */
	public abstract EnergyType getProvidedType();

	/** @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @return can this provider handle energy for this side of the TileEntity */
	public abstract boolean canProvideEnergy(TileEntity tile, EnumFacing dir);

	/** used for adding an a {@link StoredEnergyStack} to the TileEntity
	 * 
	 * @param add the maximum to receive, the {@link EnergyType} will always be the provided type, therefore there is no need to convert stored amounts
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @param action should this action be simulated
	 * @return what wasn't added */
	public abstract long receiveEnergy(long maxReceive, TileEntity tile, EnumFacing dir, ActionType action);

	/** used for removing an a {@link StoredEnergyStack} from the Inventory
	 * 
	 * @param remove the maximum to extract, the {@link EnergyType} will always be the provided type, therefore there is no need to convert stored amounts
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @param action should this action be simulated
	 * @return what wasn't extracted */
	public abstract long extractEnergy(long maxExtract, TileEntity tile, EnumFacing dir, ActionType action);

	/** only called if canProvideEnergy is true
	 * 
	 * @param storedStacks current list of energy for the block, providers only add to this and don't remove.
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from */
	public abstract void getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir);

}
