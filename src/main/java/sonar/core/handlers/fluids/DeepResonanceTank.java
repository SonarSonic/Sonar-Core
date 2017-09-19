package sonar.core.handlers.fluids;

import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.tanks.TankGrid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.FluidHelper;

import java.util.List;

@FluidHandler(modid = "deepresonance", priority = -1)
public class DeepResonanceTank implements ISonarFluidHandler {
    @Override
    public boolean canHandleFluids(TileEntity tile, EnumFacing dir) {
        return tile instanceof TileTank;
    }

    @Override
    public StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, EnumFacing dir, ActionType action) {
        TileTank tank = (TileTank) tile;
        TankGrid tanks = tank.getTank();
        if (tanks != null) {
            return FluidHelper.addStack(add, tanks, dir, action);
        }
        return add;
    }

    @Override
    public StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
        TileTank tank = (TileTank) tile;
        TankGrid tanks = tank.getTank();
        if (tanks != null) {
            return FluidHelper.removeStack(remove, tanks, dir, action);
        }
        return remove;
    }

    @Override
    public StorageSize getFluids(List<StoredFluidStack> fluids, TileEntity tile, EnumFacing dir) {
        TileTank tank = (TileTank) tile;
        TankGrid tanks = tank.getTank();
        if (tanks != null) {
            return FluidHelper.getFluids(fluids, tanks, dir);
        }
        return new StorageSize(0, 0);
    }
}