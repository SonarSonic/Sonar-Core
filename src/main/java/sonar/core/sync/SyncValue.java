package sonar.core.sync;

public class SyncValue<T> extends SonarValue<T> implements ISyncValue<T> {

    public final ISyncHandler<T> handler;
    public String key;

    public SyncValue(Class<T> type, IValueWatcher watcher, String key){
        this(type, watcher, key, null);
    }

    public SyncValue(Class<T> type, IValueWatcher watcher, String key, T value){
        super(type, watcher, value);
        this.handler = findHandler();
        this.key = key;
    }

    public ISyncHandler<T> findHandler(){
        return SyncRegistry.getHandler(type);
    }

    @Override
    public String getTagName() {
        return key;
    }

    @Override
    public ISyncHandler<T> getSyncHandler() {
        return handler;
    }
}
