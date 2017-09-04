package sonar.core.api.nbt;

import sonar.core.api.IRegistryObject;

public interface INBTObject<T> extends IRegistryObject, INBTSaveable {

    @Override
    String getName();

    T instance();
}
