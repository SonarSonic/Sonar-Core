package sonar.core.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.helpers.ListHelper;
import sonar.core.utils.Pair;

public class ListenerList<L extends ISonarListener> extends ArrayList<ListenerTally<L>> {

	public final int maxTypes;
	public boolean isValid = false;
	public boolean hasListeners = false;
	public ArrayList<ISonarListenable<L>> subLists = Lists.newArrayList();
	public ArrayList<ISonarListenable<L>> masterLists = Lists.newArrayList();
	public boolean[] hasTypes;

	public ListenerList(int maxTypes) {
		this.maxTypes = maxTypes;
		this.isValid = true;
		this.hasTypes = new boolean[maxTypes];
	}

	public void updateState() {
		boolean[] states = new boolean[maxTypes];
		lists: for (ListenerList<L> list : this.getValidLists()) {
			for (ListenerTally<L> tally : list) {
				boolean hasAll = true;
				for (int i = 0; i < states.length; i++) {
					if (!states[i]) {
						if (tally.getTally(i) > 0)
							states[i] = true;
						else
							hasAll = false;
					}
				}
				if (hasAll) {
					break lists;
				}
			}
		}
		hasTypes = states;
		hasListeners = checkForListeners();
		masterLists.forEach(l -> l.getListenerList().updateState());
		// notify non sub lists.
	}

	public boolean hasListeners(int type) {
		return this.hasTypes[type];
	}

	public boolean hasListeners() {
		return hasListeners;
	}

	public boolean hasListeners(List<Integer> types) {
		for (int i : types) {
			if (hasTypes[i]) {
				return true;
			}
		}
		return false;
	}

	public boolean hasListeners(int... types) {
		for (int i : types) {
			if (hasTypes[i]) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForListeners() {
		for (boolean bool : hasTypes) {
			if (bool) {
				return true;
			}
		}
		return false;
	}

	public void addSubListenable(ISonarListenable<L> sub) {
		if (!subLists.contains(sub) && sub.isValid()) {
			subLists.add(sub);
			onSubListenableAdded(sub);
			updateState();
		}
	}

	public void addMasterListenable(ISonarListenable<L> master) {
		if (!masterLists.contains(master) && master.isValid()) {
			masterLists.add(master);
		}
	}

	public void removeSubListenable(ISonarListenable<L> sub) {
		if (subLists.remove(sub)) {
			onSubListenableRemoved(sub);
			updateState();
		}
	}

	public void removeMasterListenable(ISonarListenable<L> master) {
		subLists.remove(master);
	}

	public void clearSubLists(boolean notify) {
		if (notify) {
			for (ListenerTally<L> tally : this) {
				onListenerRemoved(tally);
			}
			clearSubLists(false);
		} else {
			subLists.clear();
			masterLists.clear();
			updateState();
		}
	}

	public void validateList() {
		isValid = true;
	}

	public void invalidateList() {
		clearSubLists(true);
		clear();
		subLists.clear();
		masterLists.clear();
		isValid = false;
		hasTypes = new boolean[maxTypes];
	}

	private List<ListenerList<L>> getValidLists() {
		List<ListenerList<L>> getValidLists = new ArrayList<ListenerList<L>>();
		getValidLists.add(this);
		for (ISonarListenable<L> listenable : (List<ISonarListenable<L>>) subLists.clone()) {
			// TODO what about sub sub lists?
			if (listenable.isValid()) {
				getValidLists.add((ListenerList<L>) listenable.getListenerList());
			} else {
				subLists.remove(listenable);
			}
		}
		return getValidLists;
	}

	public void addListener(EntityPlayer listener, Enum... enums) {
		addListener((L) findListener(listener), ListHelper.getOrdinals(enums));
	}

	public void addListener(EntityPlayer listener, int... types) {
		addListener((L) findListener(listener), types);
	}

	public void addListener(L listener, Enum... enums) {
		addListener(listener, ListHelper.getOrdinals(enums));
	}

	public void addListener(L listener, int... types) {
		if (listener.isValid()) {
			Pair<ListenerTally<L>, Boolean> tally = getTally(listener, true);
			if (tally.a != null) {
				for (int t : types) {
					tally.a.tallies[t]++;
				}
				if (tally.b) {
					onListenerAdded(tally.a);
				}
				updateState();
			}
		}
	}

	public void removeListener(EntityPlayer listener, Enum... enums) {
		removeListener((L) findListener(listener), ListHelper.getOrdinals(enums));
	}

	public void removeListener(EntityPlayer listener, int... types) {
		removeListener((L) findListener(listener), types);
	}

	public void removeListener(L listener, Enum... enums) {
		removeListener(listener, ListHelper.getOrdinals(enums));
	}

	public void removeListener(L listener, int... types) {
		List<Integer> valid = getValidTypes(types);
		for (ListenerList<L> list : getValidLists()) {
			Pair<ListenerTally<L>, Boolean> tally = list.getTally(listener, false);
			if (tally.a != null) {
				for (int t : Lists.newArrayList(valid)) {
					if (tally.a.tallies[t] > 0) {
						tally.a.tallies[t]--;	
						valid.remove(Integer.valueOf(t));
					}
				}
				if (wasRemoved(tally.a)) {
					list.onListenerRemoved(tally.a);
					list.updateState();
				}
			}
		}
	}

	public void clearListener(L listener) {
		Pair<ListenerTally<L>, Boolean> tally = getTally(listener, false);
		if (tally.a != null && remove(tally.a)) {
			onListenerRemoved(tally.a);
			updateState();
		}
	}

	public PlayerListener findListener(EntityPlayer player) {
		for (ListenerList<L> list : getValidLists()) {
			for (ListenerTally<L> tally : list) {
				if (tally.listener instanceof PlayerListener && ((PlayerListener) tally.listener).player.isEntityEqual(player)) {
					return (PlayerListener) tally.listener;
				}
			}
		}
		return new PlayerListener(player);
	}

	private Pair<ListenerTally<L>, Boolean> getTally(L listener, boolean create) {
		Iterator<ListenerTally<L>> i = this.iterator();
		while (i.hasNext()) {
			ListenerTally tally = i.next();
			if (!tally.isValid()) {
				i.remove();
			} else if (listener.hashCode() == tally.listener.hashCode() || listener.equals(tally.listener)) {
				return new Pair(tally, false);
			}
		}
		if (create) {
			ListenerTally<L> created = new ListenerTally<L>(listener, maxTypes);
			add(created);
			return new Pair(created, true);
		}
		return new Pair(null, false);
	}

	public List<L> getListeners(Enum... enums) {
		return getListeners(ListHelper.getOrdinals(enums));
	}

	/** gets all listeners, including those in sublists */
	public List<L> getListeners(int... types) {
		List<Integer> valid = getValidTypes(types);
		List<L> listeners = Lists.newArrayList();
		if (valid.isEmpty() || !hasListeners(valid)) {
			return listeners;
		}
		getValidLists().forEach(list -> list.forEach(tally -> {
			if (!listeners.contains(tally.listener)) {
				for (int type : valid) {
					if (tally.getTally(type) > 0) {
						listeners.add(tally.listener);
					}
				}
			}
		}));
		return listeners;
	}

	private List<Integer> getValidTypes(int[] types) {
		List<Integer> valid = Lists.newArrayList();
		for (int t : types) {
			if (hasListeners(t)) {
				valid.add(t);
			}
		}
		return valid;
	}

	/** returns true is the tally was removed */
	private boolean wasRemoved(ListenerTally<L> tally) {
		if (!tally.isValid()) {
			return remove(tally);
		}
		return false;
	}

	public void onListenerAdded(ListenerTally<L> tally) {
	}

	public void onListenerRemoved(ListenerTally<L> tally) {
	}

	public void onSubListenableAdded(ISonarListenable<L> tally) {
	}

	public void onSubListenableRemoved(ISonarListenable<L> tally) {
	}

}
