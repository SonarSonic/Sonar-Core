package sonar.core.api.cabling;

import sonar.core.api.utils.BlockCoords;

public interface ISonarCable {
	
	public BlockCoords getCoords();
	
	public int registryID();
}
