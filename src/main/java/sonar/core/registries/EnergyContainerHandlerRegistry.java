package sonar.core.registries;

import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.EnergyContainerHandler;
import sonar.core.handlers.container.RFItemHandler;
import sonar.core.handlers.container.TeslaItemHandler;
/*
import sonar.core.handlers.energy.AEProvider;
import sonar.core.handlers.energy.EUProvider;
import sonar.core.handlers.energy.MekanismProvider;
*/
import sonar.core.handlers.energy.RFHandler;
import sonar.core.handlers.energy.TeslaHandler;
import sonar.core.helpers.RegistryHelper;

public class EnergyContainerHandlerRegistry extends RegistryHelper<EnergyContainerHandler> {

	@Override
	public void register() {
		registerObject(new RFItemHandler());
		registerObject(new TeslaItemHandler());
	}

	@Override
	public String registeryType() {
		return "Energy Container Handler";
	}
}