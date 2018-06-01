package sonar.core.handlers.energy.tiles;

import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyOutputter;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.CapabilityUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.asm.TileEnergyHandler;

import javax.annotation.Nonnull;

@TileEnergyHandler(modid = "mekanism", priority = 4)
public class TileHandlerMekanism implements ITileEnergyHandler {

	@Override
	public EnergyType getEnergyType() {
		return EnergyType.MJ;
	}

	@Override
	public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing dir) {
		return canAddEnergy(tile, dir) || canRemoveEnergy(tile, dir);
	}

	@Override
	public boolean canAddEnergy(TileEntity tile, EnumFacing dir) {
		if(CapabilityUtils.hasCapability(tile, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, dir)){
			return CapabilityUtils.getCapability(tile, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, dir).canReceiveEnergy(dir);
		}
		return false;
	}

	@Override
	public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir) {
		if(CapabilityUtils.hasCapability(tile, Capabilities.ENERGY_OUTPUTTER_CAPABILITY, dir)){
			return CapabilityUtils.getCapability(tile, Capabilities.ENERGY_OUTPUTTER_CAPABILITY, dir).canOutputEnergy(dir);
		}
		return false;
	}

	@Override
	public boolean canReadEnergy(TileEntity tile, EnumFacing dir) {
		return CapabilityUtils.hasCapability(tile, Capabilities.ENERGY_STORAGE_CAPABILITY, dir);
	}

	@Override
	public long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IStrictEnergyAcceptor accept =  CapabilityUtils.getCapability(tile, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, dir);
		return (long) accept.acceptEnergy(dir, add, actionType.shouldSimulate());
	}

	@Override
	public long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IStrictEnergyOutputter accept =  CapabilityUtils.getCapability(tile, Capabilities.ENERGY_OUTPUTTER_CAPABILITY, dir);
		return (long) accept.pullEnergy(dir, remove, actionType.shouldSimulate());
	}

	@Override
	public long getStored(TileEntity tile, EnumFacing dir) {
		return (long)CapabilityUtils.getCapability(tile, Capabilities.ENERGY_STORAGE_CAPABILITY, dir).getEnergy();
	}

	@Override
	public long getCapacity(TileEntity tile, EnumFacing dir) {
		return (long)CapabilityUtils.getCapability(tile, Capabilities.ENERGY_STORAGE_CAPABILITY, dir).getMaxEnergy();
	}

}
