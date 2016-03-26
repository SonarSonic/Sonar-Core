package sonar.core.registries;

import sonar.core.api.EnergyType;
import sonar.core.helpers.RegistryHelper;

public class EnergyTypeRegistry extends RegistryHelper<EnergyType> {

	@Override
	public void register() {
		registerObject(EnergyType.AE);
		registerObject(EnergyType.MJ);
		registerObject(EnergyType.EU);
		registerObject(EnergyType.RF);
	}

	@Override
	public String registeryType() {
		return "Energy Type";
	}

	public EnergyType getEnergyType(String storage) {
		for (EnergyType type : getObjects()) {
			if (type.getStorageSuffix().equals(storage)) {
				return type;
			}
		}
		return null;
	}

}
