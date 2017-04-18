package sonar.core.handlers.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;

@Deprecated
@FluidHandler(modid="sonarcore", priority = 1)
public class TankHandler implements ISonarFluidHandler {

	@Override
	public boolean canHandleFluids(TileEntity tile, EnumFacing dir) {
		return (tile instanceof IFluidHandler);
	}

	@Override
	public StorageSize getFluids(List<StoredFluidStack> fluids, TileEntity tile, EnumFacing dir) {
		if (tile instanceof IFluidHandler) {
			long stored = 0;
			long maxStorage = 0;
			IFluidHandler handler = (IFluidHandler) tile;
			FluidTankInfo[] tankInfo = handler.getTankInfo(dir);
			if (tankInfo != null) {
				int tankNumber = 0;
				for (FluidTankInfo info : tankInfo) {					
					if (info.fluid != null && info.fluid.amount != 0) {
						stored+=info.fluid.amount;
						fluids.add(new StoredFluidStack(info.fluid, info.capacity));
					}
					maxStorage+=info.capacity;
				}
			}
			return new StorageSize(stored,maxStorage);
		}
		return StorageSize.EMPTY;
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof IFluidHandler) {
			IFluidHandler handler = (IFluidHandler) tile;
			if (handler.canFill(dir, add.fluid.getFluid())) {
				int used = handler.fill(dir, add.getFullStack(), !action.shouldSimulate());
				add.stored -= used;
				if (add.stored == 0) {
					return null;
				}
			}
		}
		return add;
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof IFluidHandler) {
			IFluidHandler handler = (IFluidHandler) tile;
			if (handler.canDrain(dir, remove.fluid.getFluid())) {
				FluidStack used = handler.drain(dir, remove.getFullStack(), !action.shouldSimulate());
				if (used != null) {
					remove.stored -= used.amount;
					if (remove.stored == 0) {
						return null;
					}
				}
			}
		}
		return remove;
	}
}
