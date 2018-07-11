package sonar.core.sync;

public interface IValueWatcher {

    default void addSyncValue(ISonarValue value){}

    default void removeSyncValue(ISonarValue value){}

    default void onSyncValueChanged(ISonarValue value){}

}

