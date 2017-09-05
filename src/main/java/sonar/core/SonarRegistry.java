package sonar.core;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.wrappers.RegistryWrapper;

public class SonarRegistry extends RegistryWrapper {

    @Override
	public void registerEnergyType(EnergyType type) {
		SonarCore.energyTypes.registerObject(type);
	}

    @Override
	public EnergyType getEnergyType(String storage) {
		return SonarCore.energyTypes.getEnergyType(storage);
	}
}
