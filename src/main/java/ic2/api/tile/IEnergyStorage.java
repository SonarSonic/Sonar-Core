package ic2.api.tile;

import net.minecraft.util.EnumFacing;

/**
 * Interface implemented by the tile entity of energy storage blocks.
 */
public interface IEnergyStorage {
	/**
	 * Get the amount of energy currently stored in the block.
	 * 
	 * @return Energy stored in the block
	 */
    int getStored();

	/**
	 * Set the amount of energy currently stored in the block.
	 * 
	 * @param energy stored energy
	 */
    void setStored(int energy);

	/**
	 * Add the specified amount of energy.
	 * 
	 * Use negative values to decrease.
	 * 
	 * @param amount of energy to add
	 * @return Energy stored in the block after adding the specified amount
	 */
    int addEnergy(int amount);

	/**
	 * Get the maximum amount of energy the block can store.
	 * 
	 * @return Maximum energy stored
	 */
    int getCapacity();

	/**
	 * Get the block's energy output.
	 * 
	 * @return Energy output in EU/t
	 */
    int getOutput();

	/**
	 * Get the block's energy output.
	 * 
	 * @return Energy output in EU/t
	 */
    double getOutputEnergyUnitsPerTick();

	/**
	 * Get whether this block can have its energy used by an adjacent teleporter.
	 * 
	 * @param side side the teleporter is draining energy from
	 * @return Whether the block is teleporter compatible
	 */
    boolean isTeleporterCompatible(EnumFacing side);
}
