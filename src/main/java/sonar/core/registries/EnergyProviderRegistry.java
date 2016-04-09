package sonar.core.registries;

import sonar.core.api.energy.EnergyHandler;
/*
import sonar.core.handlers.energy.AEProvider;
import sonar.core.handlers.energy.EUProvider;
import sonar.core.handlers.energy.MekanismProvider;
*/
import sonar.core.handlers.energy.RFHandler;
import sonar.core.helpers.RegistryHelper;

public class EnergyProviderRegistry extends RegistryHelper<EnergyHandler> {

	@Override
	public void register() {
		/*
		registerObject(new AEProvider());
		registerObject(new MekanismProvider());
		registerObject(new EUProvider());
		*/
		registerObject(new RFHandler());
	}

	@Override
	public String registeryType() {
		return "Energy Provider";
	}
}