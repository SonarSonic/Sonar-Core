package sonar.core.api.blocks;

/**
 * used on blocks which can connect to Connected Block types
 */
public interface IConnectedBlock {
	
    /**
     * @return list of possible connections, see ConnectedBlock for connection types
     */
    int[] getConnections();
}
