package sonar.core.api.nbt;

import sonar.core.api.IRegistryObject;

public interface INBTObject<T> extends IRegistryObject, INBTSaveable {

	public String getName();

	public T instance();
}
