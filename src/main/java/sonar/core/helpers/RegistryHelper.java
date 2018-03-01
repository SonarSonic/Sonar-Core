package sonar.core.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.THashMap;
import sonar.core.SonarCore;
import sonar.core.api.IRegistryObject;

public abstract class RegistryHelper<T extends IRegistryObject> {

	private List<T> objects = new ArrayList<>();
	private Map<String, Integer> objectIDs = new THashMap<>();
	private Map<Integer, String> objectNames = new THashMap<>();

	public abstract void register();

	public abstract String registeryType();

	public void removeAll() {
		objects.clear();
	}

	public List<T> getObjects() {
		return objects;
	}

	public T getRegisteredObject(int objectID) {
		String helperName = objectNames.get(objectID);
		if (helperName == null || helperName.isEmpty()) {
			return getDefault();
		}
		for (T provider : objects) {
			if (provider.getName().equals(helperName)) {
				return provider;
			}
		}
		return getDefault();
	}

	public T getRegisteredObject(String name) {
		if (name == null || name.isEmpty()) {
			return getDefault();
		}
		for (T provider : objects) {
			if (provider.getName().equals(name)) {
				return provider;
			}
		}
		return getDefault();
	}

	public void registerObject(T object) {
		try {
			if (!object.isLoadable()) {
				SonarCore.logger.warn(registeryType() + " wasn't loadable: " + object.getName());
				return;
			}
			if (object != null) {
				if (getRegisteredObject(object.getName()) == null) {
					objects.add(object);
					int id = objectIDs.size();
					objectIDs.put(object.getName(), id);
					objectNames.put(id, object.getName());
					SonarCore.logger.info("Loaded " + registeryType() + ": " + object.getName());
				} else {
					SonarCore.logger.warn(registeryType() + " DUPLICATE ID - skipping " + object.getName());
				}
			}
		} catch (Exception exception) {
			SonarCore.logger.warn(registeryType() + " : Exception Loading Helper: " + exception.getMessage());
		}
	}

	public int getObjectID(String name) {
		Integer id = objectIDs.get(name);
		return id != null ? id : -1;
	}

	public T getDefault() {
		return null;
	}
}