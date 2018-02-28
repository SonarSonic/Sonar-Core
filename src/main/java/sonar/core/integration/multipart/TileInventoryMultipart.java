package sonar.core.integration.multipart;

import sonar.core.inventory.ISonarInventory;
import sonar.core.inventory.ISonarInventoryTile;

public class TileInventoryMultipart extends TileSonarMultipart implements ISonarInventoryTile {

	protected ISonarInventory inv;

	public TileInventoryMultipart() {}

	public ISonarInventory inv() {
		return inv;
	}

}
