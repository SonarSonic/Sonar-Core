package sonar.core.integration;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Loader;

public class SonarLoader {

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

	/** @returns if Practical Logistics is installed */
	public static boolean logisticsLoaded() {

		return Loader.isModLoaded("PracticalLogistics");
	}
	
	/** @returns if Forge Multipart is installed */
	public static boolean forgeMultipartLoaded() {

		return Loader.isModLoaded("ForgeMultipart");
	}
}
