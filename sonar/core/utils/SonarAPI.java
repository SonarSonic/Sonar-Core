package sonar.core.utils;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
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
	
	/** @returns if Forge Multipart is installed */
	public static boolean forgeMultipartLoaded() {

		return Loader.isModLoaded("ForgeMultipart");
	}

	public static boolean isEnabled(ItemStack stack) {
		if (calculatorLoaded()) {
			try {
				Class recipeClass = Class.forName("sonar.calculator.mod.CalculatorConfig");
				Method method = recipeClass.getMethod("isEnabled", ItemStack.class);
				return (Boolean) method.invoke(null, stack);
			} catch (Exception exception) {
				System.err.println("Sonar API: Calculator couldn't check if ItemStack was enabled " + exception.getMessage());
			}
		}
		return true;

	}
}
