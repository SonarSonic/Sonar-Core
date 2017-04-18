package sonar.core.network.sync;

import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.ISonarEnergyTile;

public class SyncSidedEnergyStorage extends SyncEnergyStorage {

	private ISonarEnergyTile tile;
	private EnumFacing currentFace = null;

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
		currentFace = facing.getOpposite();
		return this;
	}

	@Override
	public boolean canExtract() {
		return currentFace != null ? tile.getModeForSide(currentFace).canSend() : false;
	}

	@Override
	public boolean canReceive() {
		return currentFace != null ? tile.getModeForSide(currentFace).canRecieve() : false;
	}

}