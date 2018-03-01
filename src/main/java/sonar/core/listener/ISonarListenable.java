package sonar.core.listener;

import sonar.core.utils.IValidate;

public interface ISonarListenable<L extends ISonarListener> extends IValidate {

    ListenableList<L> getListenerList();

    void onListenerAdded(ListenerTally<L> tally);

    void onListenerRemoved(ListenerTally<L> tally);

    void onSubListenableAdded(ISonarListenable<L> listen);

    void onSubListenableRemoved(ISonarListenable<L> listen);
}