package sonar.core.integration.planting;

import sonar.core.helpers.RegistryHelper;
import sonar.core.integration.planting.vanilla.Harvester;

public class HarvesterRegistry extends RegistryHelper<IHarvester> {

	@Override
	public void register() {
		registerObject(new Harvester());
	}

	@Override
	public String registeryType() {
		return "Harvesters";
	}

}
