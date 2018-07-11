package sonar.core.sync;

public class SonarValue<T> implements ISonarValue<T> {

    public final Class<T> type;
    public final IValueWatcher watcher;
    public T value;
    public boolean isDirty;

    public SonarValue(Class<T> type, IValueWatcher watcher, T value){
        this.type = type;
        this.watcher = watcher;
        this.watcher.addSyncValue(this);
        setValueInternal(value);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean setValueInternal(T set) {
        if(value == null || (set != null && !set.equals(value))) {
            value = set;
            watcher.onSyncValueChanged(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
}
