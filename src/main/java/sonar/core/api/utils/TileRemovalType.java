package sonar.core.api.utils;

public enum TileRemovalType {
	CHUNK_UNLOAD, REMOVE;
	
	public boolean isFullRemoval(){
		return this == REMOVE;
	}
}
