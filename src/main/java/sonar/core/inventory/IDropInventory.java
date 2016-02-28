package sonar.core.inventory;

import net.minecraft.inventory.IInventory;

/** used to choose which slots are blocks */
public interface IDropInventory extends IInventory {

	public int[] dropSlots();

	public boolean canDrop();
}
