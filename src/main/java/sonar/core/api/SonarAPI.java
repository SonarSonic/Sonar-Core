package sonar.core.api;

import net.minecraftforge.fml.common.Loader;
import sonar.core.api.wrappers.EnergyWrapper;
import sonar.core.api.wrappers.FluidWrapper;
import sonar.core.api.wrappers.RegistryWrapper;

/**
 * Use this for all your interaction with the mod. This will be initilized by Sonar Core if it is loaded. Make sure you only register stuff once Sonar Core is loaded therefore in the FMLPostInitializationEvent
 */
public final class SonarAPI {

	public static final String MODID = "sonarcore";
	public static final String NAME = "sonarapi";
	public static final String VERSION = "1.0.1";

	private static RegistryWrapper registry = new RegistryWrapper();
	private static FluidWrapper fluids = new FluidWrapper();
	private static EnergyWrapper energy = new  EnergyWrapper();

	public static void init() {
		if (Loader.isModLoaded("SonarCore")|| Loader.isModLoaded("sonarcore")) {
			try {
				registry = (RegistryWrapper) Class.forName("sonar.core.SonarRegistry").newInstance();
				fluids = (FluidWrapper) Class.forName("sonar.core.helpers.FluidHelper").newInstance();
				energy = (EnergyWrapper) Class.forName("sonar.core.helpers.EnergyHelper").newInstance();
			} catch (Exception exception) {
				System.err.println(NAME + " : FAILED TO INITILISE API" + exception.getMessage());
			}
		}
	}

	public static RegistryWrapper getRegistry() {
		return registry;
	}

	public static FluidWrapper getFluidHelper() {
		return fluids;
	}
	
	public static EnergyWrapper getEnergyHelper() {
		return energy;
	}
}
