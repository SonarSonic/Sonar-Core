package sonar.core.handlers.energy.tiles;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.api.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.asm.TileEnergyHandler;

import javax.annotation.Nonnull;

@TileEnergyHandler(modid = "redstoneflux", priority = 3)
public class TileHandlerRedstoneFlux implements ITileEnergyHandler {

	@Override
	public EnergyType getEnergyType() {
		return EnergyType.RF;
	}

	@Override
	public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing dir) {
		return tile instanceof IEnergyConnection;
	}

	@Override
	public boolean canAddEnergy(TileEntity tile, EnumFacing dir) {
		if (canRenderConnection(tile, dir)) {
			return tile instanceof IEnergyReceiver;
		}
		return false;
	}

	@Override
	public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir) {
		if (canRenderConnection(tile, dir)) {
			return tile instanceof IEnergyProvider;
		}
		return false;
	}

	@Override
	public boolean canReadEnergy(TileEntity tile, EnumFacing dir) {
		return tile instanceof IEnergyStorage;
	}

	@Override
	public long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IEnergyReceiver receiver = (IEnergyReceiver) tile;
		return receiver.receiveEnergy(dir, (int) Math.min(Integer.MAX_VALUE, add), actionType.shouldSimulate());
	}

	@Override
	public long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType) {		
		IEnergyProvider receiver = (IEnergyProvider) tile;
		return receiver.extractEnergy(dir, (int) Math.min(Integer.MAX_VALUE, remove), actionType.shouldSimulate());
	}

	@Override
	public long getStored(TileEntity tile, EnumFacing dir) {
		return ((IEnergyStorage)tile).getEnergyStored();
	}

	@Override
	public long getCapacity(TileEntity tile, EnumFacing dir) {
		return ((IEnergyStorage)tile).getMaxEnergyStored();
	}

}
