package sonar.core.inventory;

import net.minecraft.inventory.IInventory;

/**
 * used to choose which slots are blocks
 */
public interface IDropInventory extends IInventory {

    int[] dropSlots();

    boolean canDrop();
}
