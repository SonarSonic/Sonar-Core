package sonar.core;

import sonar.core.api.EnergyHandler;
import sonar.core.api.EnergyType;
import sonar.core.api.FluidHandler;
import sonar.core.api.InventoryHandler;
import sonar.core.api.RegistryWrapper;

public class SonarRegistry extends RegistryWrapper {

	public void registerEnergyType(EnergyType type) {
		SonarCore.energyTypes.registerObject(type);
	}

	public void registerInventoryHandler(InventoryHandler provider) {
		SonarCore.inventoryProviders.registerObject(provider);
	}

	public void registerFluidHandler(FluidHandler provider) {
		SonarCore.fluidProviders.registerObject(provider);
	}

	public void registerEnergyHandler(EnergyHandler provider) {
		SonarCore.energyProviders.registerObject(provider);
	}

	public InventoryHandler getInventoryHandler(int id) {
		return SonarCore.inventoryProviders.getRegisteredObject(id);
	}

	public EnergyType getEnergyType(String storage) {
		return SonarCore.energyTypes.getEnergyType(storage);
	}

	public FluidHandler getFluidHandler(int id) {
		return SonarCore.fluidProviders.getRegisteredObject(id);
	}

	public EnergyHandler getEnergyHandler(int id) {
		return SonarCore.energyProviders.getRegisteredObject(id);
	}

	public int getInventorHandlerID(String name) {
		return SonarCore.inventoryProviders.getObjectID(name);
	}

	public int getFluidHandlerID(String name) {
		return SonarCore.fluidProviders.getObjectID(name);
	}

	public int getEnergyHandlerID(String name) {
		return SonarCore.energyProviders.getObjectID(name);
	}

}
