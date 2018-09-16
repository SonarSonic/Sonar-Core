package sonar.core.network.sync;

import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.ISonarEnergyTile;

public class SyncSidedEnergyStorage extends SyncEnergyStorage {

	private ISonarEnergyTile tile;

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

	@Override
	public boolean canExtract(EnumFacing face) {
        return face == null || tile.getModeForSide(face).canSend();
	}

	@Override
	public boolean canReceive(EnumFacing face) {
        return face == null || tile.getModeForSide(face).canRecieve();
	}
}