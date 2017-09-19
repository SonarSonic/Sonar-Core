package sonar.core.integration;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

public class SonarLoader {

    public static boolean wailaLoaded;
    public static boolean teslaLoaded;
    public static boolean calculatorLoaded;
    public static boolean logisticsLoaded;
    public static boolean fluxedRedstone;
    public static boolean mcmultipartLoaded;
    public static boolean ic2loaded;
    public static boolean jeiloaded;
    public static boolean rfLoaded;

	public static void initLoader() {
		mcmultipartLoaded = Loader.isModLoaded("mcmultipart");
		wailaLoaded = Loader.isModLoaded("Waila") || Loader.isModLoaded("Waila".toLowerCase());
		teslaLoaded = Loader.isModLoaded("Tesla") || Loader.isModLoaded("tesla");
		calculatorLoaded = Loader.isModLoaded("calculator") || Loader.isModLoaded("Calculator");
		logisticsLoaded = Loader.isModLoaded("PracticalLogistics2") || Loader.isModLoaded("practicallogistics2");
		fluxedRedstone = Loader.isModLoaded("fluxedredstone");
        rfLoaded = Loader.isModLoaded("redstoneflux");
		ic2loaded = Loader.isModLoaded("IC2") || Loader.isModLoaded("IC2".toLowerCase());
        jeiloaded = Loader.isModLoaded("jei") || Loader.isModLoaded("JEI");
    }

    public static boolean jeiLoaded() {
        return jeiloaded;
	}

    /**
     * @returns if Industrial Craft is installed
     */
	public static boolean ic2Loaded() {
		return ic2loaded;
	}

    /**
     * @returns if Waila is installed
     */
	public static boolean wailaLoaded() {
		return wailaLoaded;
	}

    /**
     * @returns if Calculator is installed
     */
	public static boolean calculatorLoaded() {
		return calculatorLoaded;
	}

    /**
     * @returns if Optics is installed
     */
	public static boolean opticsLoaded() {
		return Loader.isModLoaded("Optics");
	}

	/** @returns if Forge Multipart is installed */
	public static boolean forgeMultipartLoaded() {
		return Loader.isModLoaded("ForgeMultipart");
	}

    /**
     * @returns if Practical Logistics is installed
     */
    public static boolean logisticsLoaded() {
        return logisticsLoaded;
    }

	public static boolean isEnabled(ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}
		if (calculatorLoaded()) {
			try {
				Class recipeClass = Class.forName("sonar.calculator.mod.CalculatorConfig");
				Method method = recipeClass.getMethod("isEnabled", ItemStack.class);
				return (Boolean) method.invoke(null, stack);
			} catch (Exception exception) {
				return false;
				// System.err.println("SonarCore: Calculator couldn't check if ItemStack was enabled " + exception.getMessage());
			}
		}
		if (logisticsLoaded()) {
			try {
				Class recipeClass = Class.forName("sonar.logistics.LogisticsConfig");
				Method method = recipeClass.getMethod("isEnabled", ItemStack.class);
				return (Boolean) method.invoke(null, stack);
			} catch (Exception exception) {
				return false;
				// System.err.println("SonarCore: PracticalLogistics couldn't check if ItemStack was enabled " + exception.getMessage());
			}
		}
		return true;
	}
}
