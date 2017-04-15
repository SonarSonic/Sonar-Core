package sonar.core.listener;

import sonar.core.utils.IValidate;

public interface ISonarListenable<L extends ISonarListener> extends IValidate {

	public ListenableList<L> getListenerList();

	public void onListenerAdded(ListenerTally<L> tally);

	public void onListenerRemoved(ListenerTally<L> tally);

	public void onSubListenableAdded(ISonarListenable<L> listen);

	public void onSubListenableRemoved(ISonarListenable<L> listen);
}
