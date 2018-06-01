package sonar.core.handlers.energy.tiles;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.asm.TileEnergyHandler;

import javax.annotation.Nonnull;

@TileEnergyHandler(modid = "ic2", priority = 4)
public class TileHandlerEnergyUnits implements ITileEnergyHandler {

	@Override
	public EnergyType getEnergyType() {
		return EnergyType.EU;
	}

	@Override
	public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing dir) {
		return tile instanceof IEnergyTile || tile instanceof IEnergyStorage;
	}

	@Override
	public boolean canAddEnergy(TileEntity tile, EnumFacing dir) {
		return tile instanceof IEnergySink || tile instanceof IEnergyStorage;
	}

	@Override
	public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir) {
		return false; // IC2 EnergyNet does removals
	}

	@Override
	public boolean canReadEnergy(TileEntity tile, EnumFacing dir) {
		return tile instanceof IEnergyStorage;
	}

	@Override
	public long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType) {
		if (tile instanceof IEnergyStorage) {
			IEnergyStorage sink = (IEnergyStorage) tile;
			int before = sink.getStored();
			if (!actionType.shouldSimulate()) {
				return sink.addEnergy((int) Math.min(sink.getCapacity() - before, add));
			} else {
				return Math.min(sink.getCapacity() - before, add);
			}
		} else if (tile instanceof IEnergySink) {
			IEnergySink sink = (IEnergySink) tile;
			double voltage = EnergyNet.instance.getPowerFromTier(sink.getSinkTier());
			double amount = Math.min(add, voltage);
			if (actionType.shouldSimulate()) {
				return (long) Math.min(amount, sink.getDemandedEnergy());
			} else {
				return (long) Math.floor(amount - sink.injectEnergy(dir, amount, voltage));
			}
		}
		return 0;
	}

	@Override
	public long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType) {
		return 0;
	}

	@Override
	public long getStored(TileEntity tile, EnumFacing dir) {
		return ((IEnergyStorage)tile).getStored();
	}

	@Override
	public long getCapacity(TileEntity tile, EnumFacing dir) {
		return ((IEnergyStorage)tile).getCapacity();
	}

}
