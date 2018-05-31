package sonar.core.api.inventories;

import net.minecraft.item.ItemStack;

/**
 * implemented on TileEntities which drop extra items, e.g. Speed Upgrades, Energy Upgrades
 */
public interface IAdditionalInventory {

    ItemStack[] getAdditionalStacks();
}
