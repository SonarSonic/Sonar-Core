package sonar.core.sync;

import java.util.List;

///TODO
public interface ISyncListWatcher {

    <V> void addedValue(int index, List<V> list, V value);

    <V> void removedValue(int index, List<V> list, V value);

    <V> void changedValue(int index, List<V> list, V value);

    <V> void clearList(int index, List<V> list, V value);

}
