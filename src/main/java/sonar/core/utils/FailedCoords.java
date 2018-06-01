package sonar.core.utils;

import net.minecraft.util.math.BlockPos;
import sonar.core.api.utils.BlockCoords;

public class FailedCoords {
	private boolean objectBoolean;
	private BlockCoords coords;
	private String blockName;
	
	public FailedCoords(boolean objectReturn, BlockPos pos, String block) {
		this.objectBoolean = objectReturn;
		this.coords = new BlockCoords(pos);
		this.blockName = block;
	}

	public FailedCoords(boolean objectReturn, BlockCoords coords, String block) {
		this.objectBoolean = objectReturn;
		this.coords = coords;
		this.blockName = block;
	}

	public FailedCoords(boolean objectReturn, int x, int y, int z, String block) {
		this.objectBoolean = objectReturn;
		this.coords = new BlockCoords(x,y,z);
		this.blockName = block;
	}

	/** @return if the position failed */
	public boolean getBoolean(){
		return this.objectBoolean;
	}

    /** @return the coordinates where the failure occurred */
	public BlockCoords getCoords(){
		return this.coords;
	}

    /** @return name of the block which failed */
	public String getBlock(){
		return this.blockName;
	}
}
