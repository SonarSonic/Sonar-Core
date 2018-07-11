package sonar.core.sync;

public interface ISonarValue<T> {

    /**the current value*/
    T getValue();

    /**if the value changed this tick*/
    boolean isDirty();

    /**sets the current value, will mark it as dirty*/
    default void setValue(T set){
        if(setValueInternal(set)) {
            setDirty(true);
        }
    }

    /**sets the current value, doesn't mark it as dirty*/
    boolean setValueInternal(T set);

    /**mark this value as changed*/
    void setDirty(boolean dirty);
}
