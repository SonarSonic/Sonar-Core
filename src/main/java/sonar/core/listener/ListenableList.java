package sonar.core.listener;

/**
 * works in the same way as {@link ListenerList} but notifies an owner {@link ISonarListenable} as listeners are added/removed
 */
public class ListenableList<L extends ISonarListener> extends ListenerList<L> {

    ISonarListenable<L> owner;

    public ListenableList(ISonarListenable<L> listen, int maxTypes) {
        super(maxTypes);
        owner = listen;
    }

    @Override
    public void onListenerAdded(ListenerTally<L> tally) {
        super.onListenerAdded(tally);
        owner.onListenerAdded(tally);
    }

    @Override
    public void onListenerRemoved(ListenerTally<L> tally) {
        super.onListenerRemoved(tally);
        owner.onListenerRemoved(tally);
    }

    @Override
    public void onSubListenableAdded(ISonarListenable<L> listen) {
        super.onSubListenableAdded(listen);
        listen.getListenerList().addMasterListenable(owner);
        owner.onSubListenableAdded(listen);
    }

    @Override
    public void onSubListenableRemoved(ISonarListenable<L> listen) {
        super.onSubListenableRemoved(listen);
        listen.getListenerList().removeMasterListenable(owner);
        owner.onSubListenableRemoved(listen);
    }

    @Override
    public void invalidateList() {
        masterLists.forEach(list -> list.getListenerList().removeSubListenable(owner));
        super.invalidateList();
    }
}