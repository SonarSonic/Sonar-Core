package sonar.core.utils;

import cpw.mods.fml.common.Loader;

public class SonarAPI {

	/** @returns if Industrial Craft is installed */
	public static boolean ic2Loaded() {

		return Loader.isModLoaded("IC2");
	}

	/** @returns if Waila is installed */
	public static boolean wailaLoaded() {

		return Loader.isModLoaded("Waila");
	}

	/** @returns if Calculator is installed */
	public static boolean calculatorLoaded() {

		return Loader.isModLoaded("Calculator");
	}

	/** @returns if Optics is installed */
	public static boolean opticsLoaded() {

		return Loader.isModLoaded("Optics");
	}

	/** @returns if Statistical Energistics is installed */
	public static boolean statisticalLoaded() {

		return Loader.isModLoaded("StatisticalEnergistics");
	}
}
