package sonar.core.registries;

import net.minecraftforge.fml.common.Loader;
import sonar.core.api.InventoryHandler;
/*
import sonar.core.handlers.inventories.AE2InventoryProvider;
import sonar.core.handlers.inventories.LPInventoryProvider;
*/
import sonar.core.handlers.inventories.DSUInventoryProvider;
import sonar.core.handlers.inventories.DrawersInventoryProvider;
import sonar.core.handlers.inventories.IInventoryProvider;
import sonar.core.helpers.RegistryHelper;

public class InventoryProviderRegistry extends RegistryHelper<InventoryHandler> {

	@Override
	public void register() {
		/*
		if (Loader.isModLoaded("appliedenergistics2")) {
			registerObject(new AE2InventoryProvider());
		}
		registerObject(new LPInventoryProvider());
		*/
		registerObject(new DSUInventoryProvider());
		registerObject(new DrawersInventoryProvider());
		registerObject(new IInventoryProvider());
	}

	@Override
	public String registeryType() {
		return "Inventory Provider";
	}
}