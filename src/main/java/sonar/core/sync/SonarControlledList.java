package sonar.core.sync;

import sonar.core.utils.SimpleObservableList;

import javax.annotation.Nullable;
import java.util.List;

/** the list provided is wrapped with an observable list and changes can then be observed */
public class SonarControlledList<V> implements ISonarValue<List<V>>, SimpleObservableList.IListWatcher<V> {

    public final Class<V> type;
    public final IValueWatcher watcher;
    public SimpleObservableList<V> value = new SimpleObservableList<>();

    public boolean isDirty;

    public SonarControlledList(Class<V> type, IValueWatcher watcher, List<V> value) {
        this.type = type;
        this.watcher = watcher;
        this.watcher.addSyncValue(this);
        setValueInternal(value);
        addWatcher(this);
    }

    public final void addWatcher(SimpleObservableList.IListWatcher<V> listener){
        value.addWatcher(listener);
    }

    public final void removeWatcher(SimpleObservableList.IListWatcher<V> listener){
        value.removeWatcher(listener);
    }

    @Override
    public List<V> getValue() {
        return value;
    }

    @Override
    public boolean setValueInternal(List<V> set) {
        value.addAll(set);
        return true;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    @Override
    public void onElementAdded(@Nullable V added) {
        setDirty(true);
    }

    @Override
    public void onElementRemoved(@Nullable V added) {
        setDirty(true);
    }

    @Override
    public void onListChanged() {
        setDirty(true);
    }
}