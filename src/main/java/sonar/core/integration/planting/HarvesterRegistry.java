package sonar.core.integration.planting;

import sonar.core.helpers.RegistryHelper;
import sonar.core.integration.planting.vanilla.Harvester;
//import sonar.core.integration.planting.vanilla.IEHempHarvester;

public class HarvesterRegistry extends RegistryHelper<IHarvester> {

	@Override
	public void register() {
		registerObject(new Harvester());
		//registerObject(new IEHempHarvester());
	}

	@Override
	public String registeryType() {
		return "Harvesters";
	}

}
