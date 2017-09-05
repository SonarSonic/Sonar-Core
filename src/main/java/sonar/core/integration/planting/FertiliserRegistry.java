package sonar.core.integration.planting;

import sonar.core.helpers.RegistryHelper;
import sonar.core.integration.planting.vanilla.Fertiliser;

public class FertiliserRegistry extends RegistryHelper<IFertiliser> {

	@Override
	public void register() {
		registerObject(new Fertiliser());
	}

	@Override
	public String registeryType() {
		return "Fertilisers";
	}
}
