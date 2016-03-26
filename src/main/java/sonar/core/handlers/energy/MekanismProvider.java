package sonar.core.handlers.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import sonar.core.api.ActionType;
import sonar.core.api.EnergyHandler;
import sonar.core.api.EnergyType;
import sonar.core.api.StoredEnergyStack;

public class MekanismProvider extends EnergyHandler {

	public static String name = "Mekanism-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return tile instanceof IStrictEnergyStorage;
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		if (tile instanceof IStrictEnergyStorage) {
			IStrictEnergyStorage storage = (IStrictEnergyStorage) tile;
			energyStack.setStorageValues((long) (storage.getEnergy() / 10), (long) (storage.getMaxEnergy() / 10));
		}

	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof IStrictEnergyAcceptor) {
			IStrictEnergyAcceptor acceptor = (IStrictEnergyAcceptor) tile;
			if (acceptor.canReceiveEnergy(dir)) {
				transfer.stored -= acceptor.transferEnergyToAcceptor(dir, transfer.stored);
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof IStrictEnergyStorage) {
			IStrictEnergyStorage storage = (IStrictEnergyStorage) tile;
			double maxRemove = Math.min(transfer.stored, storage.getEnergy());
			transfer.stored -= maxRemove;
			storage.setEnergy(storage.getEnergy() - maxRemove);
		}
		return transfer;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("Mekanism");
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.MJ;
	}

}
