package sonar.core.registries;

import sonar.core.api.energy.EnergyHandler;
import sonar.core.handlers.energy.EUProvider;
import sonar.core.handlers.energy.MekanismProvider;
/*
import sonar.core.handlers.energy.AEProvider;
import sonar.core.handlers.energy.EUProvider;
import sonar.core.handlers.energy.MekanismProvider;
*/
import sonar.core.handlers.energy.RFHandler;
import sonar.core.handlers.energy.SonarHandler;
import sonar.core.handlers.energy.TeslaHandler;
import sonar.core.helpers.RegistryHelper;

public class EnergyProviderRegistry extends RegistryHelper<EnergyHandler> {

	@Override
	public void register() {
		/*
		registerObject(new AEProvider());
		*/
		registerObject(new MekanismProvider());
		registerObject(new SonarHandler());
		registerObject(new RFHandler());
		registerObject(new TeslaHandler());
		registerObject(new EUProvider());
	}

	@Override
	public String registeryType() {
		return "Energy Provider";
	}
}