package sonar.core.helpers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import sonar.core.SonarCore;
import sonar.core.api.StorageSize;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;
import sonar.core.api.wrappers.FluidWrapper;

import java.util.ArrayList;
import java.util.List;

public class FluidHelper extends FluidWrapper {

    @Override
	public void addFluidToList(List<StoredFluidStack> list, StoredFluidStack stack) {
		int pos = 0;
		for (StoredFluidStack storedTank : list) {
			if (storedTank.equalStack(stack.fluid)) {
				list.get(pos).add(stack);
				return;
			}
			pos++;
		}
		list.add(stack);
	}

    @Override
	public StoredFluidStack getStackToAdd(long inputSize, StoredFluidStack stack, StoredFluidStack returned) {
		StoredFluidStack simulateStack = null;
		if (returned == null || returned.stored == 0) {
			simulateStack = new StoredFluidStack(stack.getFullStack(), inputSize, stack.capacity);
		} else {
			simulateStack = new StoredFluidStack(stack.getFullStack(), inputSize - returned.stored, stack.capacity);
		}
		return simulateStack;
	}

    @Override
	public StoredFluidStack removeFluids(StoredFluidStack remove, TileEntity tile, EnumFacing face, ActionType type, ITankFilter filter) {
		if (filter == null || filter.allowed(remove.fluid)) {
			for (ISonarFluidHandler provider : SonarCore.fluidHandlers) {
				if (provider.canHandleFluids(tile, face)) {
					remove = provider.removeStack(remove, tile, face, type);
					if (remove == null) {
						return null;
					}
					break;
				}
			}
		}
		return remove;
	}

    @Override
	public StoredFluidStack addFluids(StoredFluidStack add, TileEntity tile, EnumFacing face, ActionType type, ITankFilter filter) {
		if (filter == null || filter.allowed(add.fluid)) {
			for (ISonarFluidHandler provider : SonarCore.fluidHandlers) {
				if (provider.canHandleFluids(tile, face)) {
					add = provider.addStack(add, tile, face, type);
					if (add == null) {
						return null;
					}
					break;
				}
			}
		}
		return add;
	}

    @Override
	public void transferFluids(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, ITankFilter filter) {
		if (from != null && to != null) {
            ArrayList<StoredFluidStack> stacks = new ArrayList<>();
			List<ISonarFluidHandler> handlers = SonarCore.fluidHandlers;
			for (ISonarFluidHandler handler : handlers) {
				if (handler.canHandleFluids(from, dirFrom)) {
					handler.getFluids(stacks, from, dirFrom);
					break;
				}
			}
			if (stacks.isEmpty()) {
				return;
			}
			for (StoredFluidStack stack : stacks) {
				StoredFluidStack removed = removeFluids(stack.copy(), from, dirFrom, ActionType.SIMULATE, filter);
				removed = getStackToAdd(stack.getStackSize(), stack.copy(), removed);
				if (removed != null) {
					StoredFluidStack add = addFluids(removed.copy(), to, dirTo, ActionType.SIMULATE, filter);
					add = getStackToAdd(removed.getStackSize(), removed.copy(), add);
					if (add != null) {
						removeFluids(add.copy(), from, dirFrom, ActionType.PERFORM, filter);
						addFluids(removed.copy(), to, dirTo, ActionType.PERFORM, filter);
					}
				}
			}
		}
	}

    public interface ITankFilter {
        boolean allowed(FluidStack stack);
    }

    public static StoredFluidStack addStack(StoredFluidStack add, IFluidHandler handler, EnumFacing dir, ActionType action) {
        add.stored -= handler.fill(add.getFullStack(), !action.shouldSimulate());
        return add;
    }

    public static StoredFluidStack removeStack(StoredFluidStack remove, IFluidHandler handler, EnumFacing dir, ActionType action) {
        FluidStack drained = null;
        remove.stored -= (drained = handler.drain(remove.getFullStack(), !action.shouldSimulate())) != null ? drained.amount : 0;
        return remove;
    }

    public static StorageSize getFluids(List<StoredFluidStack> fluids, IFluidHandler handler, EnumFacing dir) {
        long stored = 0;
        long maxStorage = 0;
        IFluidTankProperties[] tankInfo = handler.getTankProperties();
        if (tankInfo != null) {
            for (IFluidTankProperties info : tankInfo) {
                FluidStack contents = info.getContents();
                if (contents != null && contents.amount != 0) {
                    stored += contents.amount;
                    fluids.add(new StoredFluidStack(contents, info.getCapacity()));
                }
                maxStorage += info.getCapacity();
            }
        }
        return new StorageSize(stored, maxStorage);
	}
}