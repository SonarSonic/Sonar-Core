package sonar.core.utils.helpers;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.utils.INBTObject;
import sonar.core.utils.IRegistryObject;

public abstract class RegistryHelper<T extends IRegistryObject> {

	private List<T> objects = new ArrayList();
	private Map<String, Byte> objectIDs = new THashMap<String, Byte>();
	private Map<Byte, String> objectNames = new THashMap<Byte, String>();

	public abstract void register();

	public abstract String registeryType();

	public void removeAll() {
		objects.clear();
	}

	public List<T> getObjects() {
		return objects;
	}

	public T getRegisteredObject(byte objectID) {
		String helperName = objectNames.get(objectID);
		if (helperName == null || helperName.isEmpty()) {
			return null;
		}
		for (T provider : objects) {
			if (provider.getName().equals(helperName)) {
				return provider;
			}
		}
		return null;
	}

	public T getRegisteredObject(String name) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		for (T provider : objects) {
			if (provider.getName().equals(name)) {
				return provider;
			}
		}
		return null;
	}

	public void registerObject(T object) {
		if (!object.isLoadable()) {
			SonarCore.logger.warn(registeryType() + " wasn't loadable: " + object.getName());
			return;
		}
		if (object != null) {
			if (getRegisteredObject(object.getName()) == null) {
				objects.add(object);
				byte id = (byte) objectIDs.size();
				objectIDs.put(object.getName(), id);
				objectNames.put(id, object.getName());
				SonarCore.logger.info("Loaded " + registeryType() + ": " + object.getName());
			} else {
				SonarCore.logger.warn(registeryType() + " DUPLICATE ID - skipping " + object.getName());
			}
		}
	}

	public byte getObjectID(String name) {
		byte id = objectIDs.get(name);
		return id;
	}
	
}