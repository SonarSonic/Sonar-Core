package sonar.core.sync;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ValueWatcher implements IValueWatcher {

    public List<ISonarValue> watched_values = new ArrayList<>();
    public List<IValueWatcher> subWatchers = new ArrayList<>();
    public boolean dirty;

    public ValueWatcher(){}

    public ValueWatcher(IValueWatcher subWatcher){
        addSubWatcher(subWatcher);
    }

    @Override
    public void addSyncValue(ISonarValue value) {
        Preconditions.checkState(!watched_values.contains(value));
        watched_values.add(value);
        subWatchers.forEach(watcher -> watcher.addSyncValue(value));
    }

    @Override
    public void removeSyncValue(ISonarValue value) {
        Preconditions.checkState(watched_values.contains(value));
        watched_values.remove(value);
        subWatchers.forEach(watcher -> watcher.removeSyncValue(value));
    }

    @Override
    public void onSyncValueChanged(ISonarValue value) {
        Preconditions.checkState(watched_values.contains(value));
        setDirty(true);
        subWatchers.forEach(watcher -> watcher.onSyncValueChanged(value));
    }

    public void addSubWatcher(IValueWatcher watcher){
        subWatchers.add(watcher);
    }

    public void removeSubWatcher(IValueWatcher watcher){
        subWatchers.remove(watcher);
    }

    public void forEachSyncable(Consumer<ISyncValue> action){
        watched_values.forEach(v -> {
            if(v instanceof ISyncValue){
                action.accept((ISyncValue)v);
            }
        });
    }

    public void setDirty(boolean isDirty){
        dirty = isDirty;
    }

    public boolean isDirty(){
        return dirty;
    }
}
