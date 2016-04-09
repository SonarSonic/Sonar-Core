package sonar.core.api.inventories;

import java.util.List;

import sonar.core.api.SonarAPI;
import sonar.core.api.SonarHandler;
import sonar.core.api.utils.ActionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/** used for providing information on Inventories for the Inventory Reader to read, the Provider must be registered in the {@link SonarAPI} to be used */
public abstract class InventoryHandler extends SonarHandler {

	public static class StorageSize {

		public static final StorageSize EMPTY = new StorageSize(0, 0);

		private long stored, max;

		public StorageSize(long stored, long max) {
			this.stored = stored;
			this.max = max;
		}

		public long getStoredFluids() {
			return stored;
		}

		public long getMaxFluids() {
			return max;
		}

		public void addItems(long add) {
			stored += add;
		}

		public void addStorage(long add) {
			max += add;
		}
	}

	/** gets the ID of this handler, shouldn't be required outside of this class **/
	public int getID() {
		return SonarAPI.getRegistry().getInventorHandlerID(getName());
	}

	/** @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @return can this provider handle items for this side of the TileEntity */
	public abstract boolean canHandleItems(TileEntity tile, EnumFacing dir);

	/** used by the Inventory Reader, returns the {@link StoredItemStack} in the given slot
	 * @param slot the slot's ID, could be out of the Tile Range, ensure appropriate checks.
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @return the relevant {@link StoredItemStack} */
	public abstract StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir);

	/** used for adding an a {@link StoredItemStack} to the Inventory
	 * @param add the {@link StoredItemStack} to add
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @param action should this action be simulated
	 * @return what wasn't added */
	public abstract StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action);

	/** used for removing an a {@link StoredItemStack} from the Inventory
	 * @param remove the {@link StoredItemStack} to remove
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @param action should this action be simulated
	 * @return what wasn't extracted */
	public abstract StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action);

	/** only called if canHandleItems is true
	 * @param storedStacks current list of items for the block from this Helper, providers only add to this and don't remove.
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link EnumFacing} to check from
	 * @return an {@link StorageSize} object, ensure that capacity and stored items have been fully accounted for */
	public abstract StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir);
}
