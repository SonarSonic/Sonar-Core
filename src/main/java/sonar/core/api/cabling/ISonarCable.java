package sonar.core.api.cabling;

import sonar.core.api.utils.BlockCoords;

public interface ISonarCable {
	
    BlockCoords getCoords();
	
    int registryID();
}
