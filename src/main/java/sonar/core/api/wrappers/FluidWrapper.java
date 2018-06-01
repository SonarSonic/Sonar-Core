package sonar.core.api.wrappers;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;
import sonar.core.handlers.fluids.FluidHelper.ITankFilter;

public class FluidWrapper {

    /**
     * convenient method, adds the given stack to the list, used by {@link FluidHandler}
     *
	 * @param list {@link StoredFluidStack} list to add to
     * @param stack {@link StoredFluidStack} to combine
     */
    public void addFluidToList(List<StoredFluidStack> list, StoredFluidStack stack) {
    }

    /**
     * convenience method, creates a {@link StoredFluidStack} of how much should be added
     */
	public StoredFluidStack getStackToAdd(long inputSize, StoredFluidStack stack, StoredFluidStack returned) {
		return null;
	}
	
	public StoredFluidStack addFluids(StoredFluidStack add, TileEntity tile, EnumFacing face, ActionType type, ITankFilter filter) {
		return null;
	}

	public StoredFluidStack removeFluids(StoredFluidStack remove, TileEntity tile, EnumFacing face, ActionType type, ITankFilter filter) {
		return null;
	}

	public void transferFluids(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, ITankFilter filter) {
		
	}
}
