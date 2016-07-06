package sonar.core.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.SonarAPI;
import sonar.core.api.SonarHandler;
import sonar.core.api.utils.ActionType;

/** used for providing information on Block/TileEntity for the Info Reader to read, the Provider must be registered in the {@link SonarAPI} to be used */
public abstract class EnergyHandler extends SonarHandler {

	public int getID() {
		return SonarAPI.getRegistry().getEnergyHandlerID(getName());
	}
	/**the {@link EnergyType} this provider can handle*/
	public abstract EnergyType getProvidedType();

	/** @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @return can this provider handle energy for this side of the TileEntity */
	public abstract boolean canProvideEnergy(TileEntity tile, EnumFacing dir);

	/** used for adding an a {@link StoredEnergyStack} to the TileEntity
	 * @param add the {@link StoredEnergyStack} to add, the {@link EnergyType} will always be the provided type, therefore there is no need to convert stored amounts
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @param action should this action be simulated
	 * @return what wasn't added */
	public abstract StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action);

	/** used for removing an a {@link StoredEnergyStack} from the Inventory
	 * @param remove the {@link StoredEnergyStack} to remove, the {@link EnergyType} will always be the provided type, therefore there is no need to convert stored amounts
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @param action should this action be simulated
	 * @return what wasn't extracted */
	public abstract StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action);

	/** only called if canProvideEnergy is true
	 * @param energyStack current list of energy for the block, providers only add to this and don't remove.
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from */
	public abstract void getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir);

}