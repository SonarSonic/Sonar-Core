package sonar.core.listener;

import sonar.core.utils.IValidate;

public interface ISonarListenable<L extends ISonarListener> extends IValidate {

    ListenableList<L> getListenerList();

    default void onListenerAdded(ListenerTally<L> tally){}

    default void onListenerRemoved(ListenerTally<L> tally){}

    default void onSubListenableAdded(ISonarListenable<L> listen){}

    default void onSubListenableRemoved(ISonarListenable<L> listen){}
}