package sonar.core.helpers;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.util.EnumFacing;
import sonar.core.api.cabling.ISonarCable;
import sonar.core.api.utils.BlockCoords;

public abstract class CableNetworkHelper {

	private static Map<Integer, ArrayList<BlockCoords>> cables = new THashMap<Integer, ArrayList<BlockCoords>>();
	private static Map<Integer, ArrayList<BlockCoords>> connections = new THashMap<Integer, ArrayList<BlockCoords>>();

	private static void removeAll() {
		cables.clear();
		connections.clear();
	}

	public void addCable(ISonarCable cable) {
		cables.putIfAbsent(cable.registryID(), new ArrayList());
		addCoordsToArray(cable.getCoords(), cables.get(cable.registryID()));

		for (EnumFacing face : EnumFacing.VALUES) {
			BlockCoords coords = BlockCoords.translateCoords(cable.getCoords(), face);
			if (checkConnection(coords)) {
				addCoordsToArray(coords, connections.get(cable.registryID()));
			}
		}
	}

	public void removeCable(ISonarCable cable) {
		for (EnumFacing face : EnumFacing.VALUES) {
			BlockCoords coords = BlockCoords.translateCoords(cable.getCoords(), face);
			if (checkConnection(coords)) {
				removeCoordsFromArray(coords, connections.get(cable.registryID()));
			}
		}
		removeCoordsFromArray(cable.getCoords(), cables.get(cable.registryID()));
	}

	public void addCoordsToArray(BlockCoords coords, ArrayList<BlockCoords> array) {
		for (BlockCoords coord : (ArrayList<BlockCoords>) array.clone()) {
			if (coord.equals(coords)) {
				return;
			}
		}
	}

	public void removeCoordsFromArray(BlockCoords coords, ArrayList<BlockCoords> array) {
		ArrayList<BlockCoords> toRemove = new ArrayList();
		for (BlockCoords coord : (ArrayList<BlockCoords>) array.clone()) {
			if (coord.equals(coords)) {
				toRemove.add(coord);
			}
		}
		for (BlockCoords remove : toRemove) {
			array.remove(remove);
		}
	}

	public static ArrayList<BlockCoords> getCables(int registryID) {
		return cables.get(registryID) == null ? new ArrayList() : cables.get(registryID);
	}

	public static ArrayList<BlockCoords> getConnections(int registryID) {
		return connections.get(registryID) == null ? new ArrayList() : connections.get(registryID);
	}

	public abstract boolean checkConnection(BlockCoords coord);

}