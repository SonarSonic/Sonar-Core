package sonar.core.sync;

import java.util.List;

public class SyncedValueList<V> extends SonarControlledList<V> implements ISyncValue<List<V>> {

    public final ISyncHandler<List<V>> handler;
    public String key;

    public SyncedValueList(Class<V> type, IValueWatcher watcher, String key, List<V> value){
        super(type, watcher, value);
        this.handler = new SyncHandlerList<>(SyncRegistry.getHandler(type));
        this.key = key;
    }

    @Override
    public String getTagName() {
        return key;
    }

    @Override
    public ISyncHandler<List<V>> getSyncHandler() {
        return handler;
    }
}