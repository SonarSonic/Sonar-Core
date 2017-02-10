package sonar.core.handlers.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;

@FluidHandler(modid="sonarcore", handlerID = FluidCapabilityHandler.name, priority = 0)
public class FluidCapabilityHandler implements ISonarFluidHandler {

	public static final String name = "Fluid Capability Handler";
	
	@Override
	public boolean canHandleFluids(TileEntity tile, EnumFacing dir) {
		return tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir);
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir);
		add.stored -= handler.fill(add.getFullStack(), !action.shouldSimulate());
		return add;
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir);
		FluidStack drained = null;
		remove.stored -= (drained = handler.drain(remove.getFullStack(), !action.shouldSimulate())) != null ? drained.amount : 0;
		return remove;
	}

	@Override
	public StorageSize getFluids(List<StoredFluidStack> fluids, TileEntity tile, EnumFacing dir) {
		IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir);
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
