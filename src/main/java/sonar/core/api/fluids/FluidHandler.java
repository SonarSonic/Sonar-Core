package sonar.core.api.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.SonarAPI;
import sonar.core.api.SonarHandler;
import sonar.core.api.StorageSize;
import sonar.core.api.utils.ActionType;

/** used for providing information on Fluids stored in TileEntities for the Fluid Reader to read, the Provider must be registered in the {@link SonarAPI} to be used */
public abstract class FluidHandler extends SonarHandler {

	public int getID() {
		return SonarAPI.getRegistry().getFluidHandlerID(getName());
	}

	/** @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @return can this provider handle fluids for this side of the TileEntity */
	public abstract boolean canHandleFluids(TileEntity tile, EnumFacing dir);

	/** used for adding an a {@link StoredFluidStack} to the Fluid Inventory
	 * @param add the {@link StoredFluidStack} to add
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @param action should this action be simulated
	 * @return what wasn't added */
	public abstract StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, EnumFacing dir, ActionType action);

	/** used for removing an a {@link StoredFluidStack} from the Fluid Inventory
	 * @param remove the {@link StoredFluidStack} to remove
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @param action should this action be simulated
	 * @return what wasn't extracted */
	public abstract StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, EnumFacing dir, ActionType action);

	/** only called if canHandleFluids is true
	 * @param storedStacks current list of fluids for the block from this Helper, providers only add to this and don't remove.
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @return an {@link StorageSize} object, ensure that capacity and stored fluids have been fully accounted for */
	public abstract StorageSize getFluids(List<StoredFluidStack> fluids, TileEntity tile, EnumFacing dir);

}
