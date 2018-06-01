package sonar.core.handlers.energy.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.asm.TileEnergyHandler;

import javax.annotation.Nonnull;

@TileEnergyHandler(modid = "sonarcore", priority = 0)
public class TileHandlerForge implements ITileEnergyHandler {

	@Override
	public EnergyType getEnergyType() {
		return EnergyType.FE;
	}
	
	@Override
	public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing dir) {
		return tile.hasCapability(CapabilityEnergy.ENERGY, dir);
	}

	@Override
	public boolean canAddEnergy(TileEntity tile, EnumFacing dir) {
		if (canRenderConnection(tile, dir)) {
			IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
			return storage.canReceive();
		}
		return false;
	}

	@Override
	public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir) {
		if (canRenderConnection(tile, dir)) {
			IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
			return storage.canExtract();
		}
		return false;
	}

	@Override
	public boolean canReadEnergy(TileEntity tile, EnumFacing dir) {
		return tile.hasCapability(CapabilityEnergy.ENERGY, dir);
	}

	@Override
	public long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		return storage.receiveEnergy((int) Math.min(Integer.MAX_VALUE, add), actionType.shouldSimulate());
	}

	@Override
	public long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		return storage.extractEnergy((int) Math.min(Integer.MAX_VALUE, remove), actionType.shouldSimulate());
	}

	@Override
	public long getStored(TileEntity tile, EnumFacing dir) {
		return tile.getCapability(CapabilityEnergy.ENERGY, dir).getEnergyStored();
	}

	@Override
	public long getCapacity(TileEntity tile, EnumFacing dir) {
		return tile.getCapability(CapabilityEnergy.ENERGY, dir).getMaxEnergyStored();
	}

}
