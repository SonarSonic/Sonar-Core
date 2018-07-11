package sonar.core.sync;

public interface ISpecialSyncValue extends ISyncValue {

    boolean canSyncType();
}
