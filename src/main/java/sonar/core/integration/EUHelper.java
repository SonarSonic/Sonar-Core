package sonar.core.integration;

import ic2.api.energy.EnergyNet;

public class EUHelper {

	public static double getVoltage(int tier) {
		return EnergyNet.instance.getPowerFromTier(tier);
	}
}
