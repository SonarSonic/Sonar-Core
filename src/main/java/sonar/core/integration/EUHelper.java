package sonar.core.integration;

public class EUHelper {

	public static double getVoltage(int tier) {
		switch (tier) {
		case 1:
			return 32;
		case 2:
			return 128;
		case 3:
			return 512;
		case 4:
			return 2048;
		default:
			return 8192;
		}
	}
}
