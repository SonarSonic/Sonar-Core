package sonar.core;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.wrappers.RegistryWrapper;

public class SonarRegistry extends RegistryWrapper {

	public void registerEnergyType(EnergyType type) {
		SonarCore.energyTypes.registerObject(type);
	}

	public EnergyType getEnergyType(String storage) {
		return SonarCore.energyTypes.getEnergyType(storage);
	}

}
