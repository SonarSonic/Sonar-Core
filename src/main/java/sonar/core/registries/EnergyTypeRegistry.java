package sonar.core.registries;

import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.RegistryHelper;

public class EnergyTypeRegistry extends RegistryHelper<EnergyType> {

	@Override
	public void register() {
		registerObject(EnergyType.FE);
		registerObject(EnergyType.TESLA);
		registerObject(EnergyType.RF);
		registerObject(EnergyType.EU);
		registerObject(EnergyType.MJ);
		registerObject(EnergyType.AE);
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
		return getDefault();
	}
	
	public EnergyType getDefault() {
		return EnergyType.FE;
	}
}
