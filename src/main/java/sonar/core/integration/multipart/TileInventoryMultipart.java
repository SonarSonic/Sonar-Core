package sonar.core.integration.multipart;

import sonar.core.api.inventories.ISonarInventory;
import sonar.core.api.inventories.ISonarInventoryTile;

public class TileInventoryMultipart extends TileSonarMultipart implements ISonarInventoryTile {

	protected ISonarInventory inv;

	public TileInventoryMultipart() {}

	public ISonarInventory inv() {
		return inv;
	}

}
