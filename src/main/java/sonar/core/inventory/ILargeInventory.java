package sonar.core.inventory;

import net.minecraft.inventory.IInventory;

public interface ILargeInventory extends IInventory {

	public SonarLargeInventory getTileInv();
}
