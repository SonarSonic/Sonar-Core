package sonar.core.registries;

import sonar.core.api.fluids.FluidHandler;
import sonar.core.handlers.fluids.FluidCapabilityHandler;
/*
import sonar.core.handlers.fluids.AE2FluidProvider;
*/
import sonar.core.handlers.fluids.TankProvider;
import sonar.core.helpers.RegistryHelper;

public class FluidProviderRegistry extends RegistryHelper<FluidHandler> {

	@Override
	public void register() {
		/*
		if (Loader.isModLoaded("appliedenergistics2")) {
			registerObject(new AE2FluidProvider());
		}
		*/
		registerObject(new FluidCapabilityHandler());
		registerObject(new TankProvider());
	}

	@Override
	public String registeryType() {
		return "Fluid Provider";
	}
}