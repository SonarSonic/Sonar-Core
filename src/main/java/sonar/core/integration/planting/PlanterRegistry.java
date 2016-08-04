package sonar.core.integration.planting;

import sonar.core.helpers.RegistryHelper;
import sonar.core.integration.planting.vanilla.Planter;

public class PlanterRegistry extends RegistryHelper<IPlanter> {

	@Override
	public void register() {
		registerObject(new Planter());		
	}

	@Override
	public String registeryType() {
		return "Planters";
	}

}
