package sonar.core.registries;

import sonar.core.api.energy.EnergyContainerHandler;
import sonar.core.handlers.container.RFItemHandler;
import sonar.core.handlers.container.SonarItemHandler;
import sonar.core.handlers.container.TeslaItemHandler;
import sonar.core.helpers.RegistryHelper;

public class EnergyContainerHandlerRegistry extends RegistryHelper<EnergyContainerHandler> {

	@Override
	public void register() {
		registerObject(new SonarItemHandler());
		registerObject(new RFItemHandler());
		registerObject(new TeslaItemHandler());
	}

	@Override
	public String registeryType() {
		return "Energy Container Handler";
	}
}