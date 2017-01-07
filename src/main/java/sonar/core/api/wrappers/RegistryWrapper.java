package sonar.core.api.wrappers;

import sonar.core.api.energy.EnergyType;

public class RegistryWrapper {

	/** used for registering Energy Types, this should have a unique storage suffix!
	 * @param info {@link EnergyType} to register */
	public void registerEnergyType(EnergyType type) {
	}

	/** gets the {@link EnergyType} for the given storage suffix
	 * @param id the id to check
	 * @return the {@link EnergyType}, can be null */
	public EnergyType getEnergyType(String storage) {
		return null;
	}
}
