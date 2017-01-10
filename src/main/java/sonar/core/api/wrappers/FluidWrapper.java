package sonar.core.api.wrappers;

import java.util.List;

import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.inventories.StoredItemStack;

public class FluidWrapper {

	/** convenient method, adds the given stack to the list, used by {@link FluidHandler}
	 * @param list {@link StoredFluidStack} list to add to
	 * @param stack {@link StoredFluidStack} to combine */
	public void addFluidToList(List<StoredFluidStack> list, StoredFluidStack stack) {}

	/** convenience method, creates a {@link StoredFluidStack} of how much should be added */
	public StoredFluidStack getStackToAdd(long inputSize, StoredFluidStack stack, StoredItemStack returned) {
		return null;
	}

}
