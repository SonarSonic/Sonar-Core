package sonar.core.handlers.fluids;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.FluidHelper;

import java.util.List;

@FluidHandler(modid = "sonarcore", priority = 0)
public class FluidCapabilityHandler implements ISonarFluidHandler {
	
	@Override
	public boolean canHandleFluids(TileEntity tile, EnumFacing dir) {
		return tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir);
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, EnumFacing dir, ActionType action) {
        return FluidHelper.addStack(add, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir), dir, action);
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
        return FluidHelper.removeStack(remove, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir), dir, action);
	}

	@Override
	public StorageSize getFluids(List<StoredFluidStack> fluids, TileEntity tile, EnumFacing dir) {
        return FluidHelper.getFluids(fluids, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir), dir);
				}
}
