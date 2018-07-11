package sonar.core.sync;

import javax.annotation.Nullable;

public interface ISyncValueFactory {

    @Nullable
    ISyncValue createValue(Class type, IValueWatcher watcher, String key);
}
