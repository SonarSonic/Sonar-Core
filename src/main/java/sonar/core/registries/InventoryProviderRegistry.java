package sonar.core.registries;

import sonar.core.api.InventoryHandler;
import sonar.core.handlers.inventories.AE2InventoryProvider;
import sonar.core.handlers.inventories.DSUInventoryProvider;
import sonar.core.handlers.inventories.DrawersInventoryProvider;
import sonar.core.handlers.inventories.IInventoryProvider;
import sonar.core.handlers.inventories.LPInventoryProvider;
import sonar.core.handlers.inventories.StorageChamberInventoryProvider;
import sonar.core.helpers.RegistryHelper;
import cpw.mods.fml.common.Loader;

public class InventoryProviderRegistry extends RegistryHelper<InventoryHandler> {

	@Override
	public void register() {
		if (Loader.isModLoaded("appliedenergistics2")) {
			registerObject(new AE2InventoryProvider());
		}
		registerObject(new DSUInventoryProvider());
		registerObject(new LPInventoryProvider());
		registerObject(new DrawersInventoryProvider());
		registerObject(new StorageChamberInventoryProvider());
		registerObject(new IInventoryProvider());
	}

	@Override
	public String registeryType() {
		return "Inventory Provider";
	}
}