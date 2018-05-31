package sonar.core.inventory.handling;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.ISonarInventoryTile;

public class SlotSonarFiltered extends Slot {

    public ISonarInventoryTile invTile;

    public SlotSonarFiltered(ISonarInventoryTile invTile, int index, int xPosition, int yPosition) {
        super(invTile, index, xPosition, yPosition);
        this.invTile = invTile;
    }

    @Override
    public int getSlotStackLimit(){
        return invTile.inv().getSlotLimit(getSlotIndex());
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if(invTile.inv().checkInsert(this.getSlotIndex(), stack, null, EnumFilterType.INTERNAL)){
            return super.isItemValid(stack);
        }
        return false;
    }
}
