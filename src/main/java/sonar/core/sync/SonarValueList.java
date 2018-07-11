package sonar.core.sync;

import java.util.List;

public class SonarValueList<V> implements ISonarValue<List<V>> {

    public final Class<V> type;
    public final IValueWatcher watcher;
    public List<V> value;
    public boolean isDirty;

    public SonarValueList(Class<V> type, IValueWatcher watcher, List<V> value){
        this.type = type;
        this.watcher = watcher;
        this.watcher.addSyncValue(this);
        setValueInternal(value);

    }
    @Override
    public List<V> getValue() {
        return value;
    }

    @Override
    public boolean setValueInternal(List<V> set) {
        if(value != set) {
            value = set;
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