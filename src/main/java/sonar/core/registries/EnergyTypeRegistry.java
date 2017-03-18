package sonar.core.registries;

import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.RegistryHelper;

public class EnergyTypeRegistry extends RegistryHelper<EnergyType> {

	@Override
	public void register() {
		registerObject(EnergyType.AE);
		registerObject(EnergyType.MJ);
		registerObject(EnergyType.EU);
		registerObject(EnergyType.TESLA);
		registerObject(EnergyType.RF);
		registerObject(EnergyType.FE);
	}

	@Override
	public String registeryType() {
		return "Energy Type";
	}

	public EnergyType getEnergyType(String storage) {
		for (EnergyType type : getObjects()) {
			if (type.getName().equals(storage)) {
				return type;
			}
		}
		return null;
	}

}
