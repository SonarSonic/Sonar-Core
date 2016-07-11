package sonar.core.upgrades;

import net.minecraft.item.Item;
import sonar.core.helpers.LinkedRegistryHelper;

public class MachineUpgradeRegistry extends LinkedRegistryHelper<String, Item> {

	@Override
	public void register() {
		//registered in Calculator.
	}

	@Override
	public String registeryType() {
		return "Machine Upgrade";
	}

}