package sonar.core.api;



public abstract class SonarHandler implements IRegistryObject {

	/** used when the provider is loaded normally used to check if relevant mods are loaded for APIs to work */
	public boolean isLoadable() {
		return true;
	}
}
