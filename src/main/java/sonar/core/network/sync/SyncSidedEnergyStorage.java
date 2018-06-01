package sonar.core.network.sync;

import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.handlers.energy.EnergyTransferHandler;
import sonar.core.handlers.energy.EnumEnergyWrapperType;
import sonar.core.handlers.energy.IEnergyHandler;

public class SyncSidedEnergyStorage extends SyncEnergyStorage {

	private ISonarEnergyTile tile;
    private EnumFacing currentFace;
    private IEnergyHandler internalWrapper;

	public SyncSidedEnergyStorage(ISonarEnergyTile tile, int capacity) {
		this(tile, capacity, capacity, capacity);
	}

	public SyncSidedEnergyStorage(ISonarEnergyTile tile, int capacity, int maxTransfer) {
		this(tile, capacity, maxTransfer, maxTransfer);
	}

	public SyncSidedEnergyStorage(ISonarEnergyTile tile, int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
		this.tile = tile;
	}

	public SyncEnergyStorage setCurrentFace(EnumFacing facing) {
		currentFace = facing;
		return this;
	}

	public IEnergyHandler getInternalWrapper(){
		return internalWrapper == null ? internalWrapper = EnergyTransferHandler.INSTANCE_SC.getWrappedStorageHandler(this, EnumEnergyWrapperType.INTERNAL_TILE_STORAGE, EnergyType.FE) : internalWrapper;
	}

	@Override
	public boolean canExtract() {
        return currentFace == null || tile.getModeForSide(currentFace).canSend();
	}

	@Override
	public boolean canReceive() {
        return currentFace == null | tile.getModeForSide(currentFace).canRecieve();
	}
}