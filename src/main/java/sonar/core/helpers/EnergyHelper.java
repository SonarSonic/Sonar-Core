package sonar.core.helpers;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.api.wrappers.EnergyWrapper;
import sonar.core.energy.DischargeValues;
import sonar.core.handlers.energy.SonarHandler;
import sonar.core.network.sync.SyncEnergyStorage;

public class EnergyHelper extends EnergyWrapper {
	/** changes = returns amount remaining */

	public static final SonarHandler chargingHandler = new SonarHandler();
	
	public long receiveEnergy(TileEntity tile, long maxReceive, EnumFacing dir, ActionType type) {
		if (maxReceive != 0 && tile != null) {
			ISonarEnergyHandler handler = this.canTransferEnergy(tile, dir);
			if (handler != null) {
				return performReceive(handler, tile, maxReceive, dir, type);
			}
		}
		return 0;
	}

	/** allows specific selection of the Energy Handler rather than using the receiveEnergy method normally */
	public long performReceive(ISonarEnergyHandler handler, TileEntity tile, long maxReceive, EnumFacing dir, ActionType type) {
		long receive = StoredEnergyStack.convert(maxReceive, EnergyType.RF, handler.getProvidedType());
		StoredEnergyStack stack = handler.addEnergy(new StoredEnergyStack(EnergyType.RF).setStackSize(receive), tile, dir, type);
		long remain = stack == null ? 0 : stack.getStackSize();
		remain = StoredEnergyStack.convert(remain, handler.getProvidedType(), EnergyType.RF);
		return maxReceive - remain;
	}

	public long extractEnergy(TileEntity tile, long maxExtract, EnumFacing dir, ActionType type) {
		if (maxExtract != 0 && tile != null) {
			ISonarEnergyHandler handler = this.canTransferEnergy(tile, dir);
			if (handler != null) {
				return performExtract(handler, tile, maxExtract, dir, type);
			}
		}
		return 0;
	}

	/** allows specific selection of the Energy Handler rather than using the extractEnergy method normally */
	public long performExtract(ISonarEnergyHandler handler, TileEntity tile, long maxExtract, EnumFacing dir, ActionType type) {
		long receive = StoredEnergyStack.convert(maxExtract, EnergyType.RF, handler.getProvidedType());
		StoredEnergyStack stack = handler.removeEnergy(new StoredEnergyStack(EnergyType.RF).setStackSize(receive), tile, dir, type);

		long remain = stack == null ? 0 : stack.getStackSize();
		remain = StoredEnergyStack.convert(remain, handler.getProvidedType(), EnergyType.RF);
		return maxExtract - remain;
	}

	public long receiveEnergy(ItemStack stack, long maxReceive, ActionType type) {
		if (maxReceive != 0 && stack != null) {
			ISonarEnergyContainerHandler handler = this.canTransferEnergy(stack);
			if (handler != null) {
				long receive = StoredEnergyStack.convert(maxReceive, EnergyType.RF, handler.getProvidedType());
				StoredEnergyStack energystack = handler.addEnergy(new StoredEnergyStack(EnergyType.RF).setStackSize(receive), stack, type);

				long remain = energystack == null ? 0 : energystack.getStackSize();
				remain = StoredEnergyStack.convert(remain, handler.getProvidedType(), EnergyType.RF);
				return maxReceive - remain;
			}
		}
		return 0;
	}

	public long extractEnergy(ItemStack stack, long maxExtract, ActionType type) {
		if (maxExtract != 0 && stack != null) {
			ISonarEnergyContainerHandler handler = this.canTransferEnergy(stack);
			if (handler != null) {
				long receive = StoredEnergyStack.convert(maxExtract, EnergyType.RF, handler.getProvidedType());
				StoredEnergyStack energystack = handler.removeEnergy(new StoredEnergyStack(EnergyType.RF).setStackSize(receive), stack, type);

				long remain = energystack == null ? 0 : energystack.getStackSize();
				remain = StoredEnergyStack.convert(remain, handler.getProvidedType(), EnergyType.RF);
				return maxExtract - remain;
			}
		}
		return 0;
	}

	public StoredEnergyStack getEnergyStored(ItemStack stack, EnergyType format) {
		ISonarEnergyContainerHandler handler = this.canTransferEnergy(stack);
		if (handler != null) {
			StoredEnergyStack energy = new StoredEnergyStack(handler.getProvidedType());
			handler.getEnergy(energy, stack);
			return energy.convertEnergyType(format);
		}
		return new StoredEnergyStack(format);		
	}
	
	/** returns amount transferred **/
	public long transferEnergy(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, final long maxTransferRF) {
		if (from != null && !from.getWorld().isRemote && to != null && maxTransferRF != 0) {
			long maxTransfer = Math.min(extractEnergy(from, maxTransferRF, dirFrom, ActionType.SIMULATE), receiveEnergy(to, maxTransferRF, dirTo, ActionType.SIMULATE));
			if (maxTransfer != 0) {
				return extractEnergy(from, receiveEnergy(to, maxTransfer, dirTo, ActionType.PERFORM), dirFrom, ActionType.PERFORM);
			}
		}
		return 0;
	}

	public ItemStack chargeItem(ItemStack item, TileEntity tile, final long maxTransferRF) {
		if (tile != null && !tile.getWorld().isRemote && item != null && maxTransferRF != 0) {
			long maxTransfer = Math.min(performExtract(chargingHandler, tile, maxTransferRF, null, ActionType.SIMULATE), receiveEnergy(item, maxTransferRF, ActionType.SIMULATE));
			if (maxTransfer != 0) {
				performExtract(chargingHandler, tile, receiveEnergy(item, maxTransfer, ActionType.PERFORM), null, ActionType.PERFORM);
				return item;
			}
		}
		return item;
	}

	public ItemStack dischargeItem(ItemStack item, TileEntity tile, final long maxTransferRF) {
		if (item != null && !tile.getWorld().isRemote && tile != null && maxTransferRF != 0) {
			long maxTransfer = Math.min(extractEnergy(item, maxTransferRF, ActionType.SIMULATE), performReceive(chargingHandler, tile, maxTransferRF, null, ActionType.SIMULATE));

			if (maxTransfer != 0) {
				extractEnergy(item, performReceive(chargingHandler, tile, maxTransfer, null, ActionType.PERFORM), ActionType.PERFORM);
				return item;
			}
			//hard coded discharge values for time being
			if (tile instanceof ISonarEnergyTile) {
				SyncEnergyStorage storage = ((ISonarEnergyTile) tile).getStorage();
				int value = DischargeValues.getValueOf(item);
				if (value != 0) {
					if (storage.getEnergyStored() + value <= storage.getMaxEnergyStored()) {
						storage.setEnergyStored(storage.getEnergyStored() + value);
						item.stackSize -= 1;
						if (item.stackSize <= 0) {
							item = null;
						}
						return item;
					}
				}
			}
		}
		return item;
	}

	public ISonarEnergyHandler canTransferEnergy(TileEntity tile, EnumFacing dir) {
		List<ISonarEnergyHandler> handlers = SonarCore.energyHandlers;
		for (ISonarEnergyHandler handler : handlers) {
			if (handler.canProvideEnergy(tile, dir)) {
				return handler;
			}
		}
		return null;
	}

	public ISonarEnergyContainerHandler canTransferEnergy(ItemStack stack) {
		List<ISonarEnergyContainerHandler> handlers = SonarCore.energyContainerHandlers;
		for (ISonarEnergyContainerHandler handler : handlers) {
			if (handler.canHandleItem(stack)) {
				return handler;
			}
		}
		return null;
	}
}
