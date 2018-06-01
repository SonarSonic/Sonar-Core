package sonar.core.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import sonar.core.handlers.inventories.handling.EnumFilterType;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;
import sonar.core.utils.MachineSides;

public class TileEntitySidedInventory extends TileEntityInventory implements IMachineSides, ISidedInventory {

	public MachineSides sides = new MachineSides(MachineSideConfig.INPUT, this, MachineSideConfig.NONE);
	//
	
	public TileEntitySidedInventory() {
		super();
		super.inv.getInsertFilters().put(sides, EnumFilterType.EXTERNAL);
		super.inv.getExtractFilters().put(sides, EnumFilterType.EXTERNAL);
		syncList.addPart(sides);
	}

	@Override
	public MachineSides getSideConfigs() {
		return sides;
	}
}