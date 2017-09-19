package sonar.core.network.sync;

public interface ISyncableListener {

    void markChanged(IDirtyPart part);
}
