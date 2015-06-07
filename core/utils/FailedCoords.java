package sonar.core.utils;

public class FailedCoords extends Object {
	private boolean objectBoolean;
	private int xCoord;
	private int yCoord;
	private int zCoord;
	private String blockName;

	public FailedCoords(boolean objectReturn, int x, int y, int z, String block) {
		this.objectBoolean = objectReturn;
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
		this.blockName = block;
	}
	public boolean getBoolean(){
		return this.objectBoolean;
	}
	/**@return X Coordinate where the failure occurred*/
	public int getX(){
		return this.xCoord;
	}
	/**@return Y Coordinate where the failure occurred*/
	public int getY(){
		return this.yCoord;
	}
	/**@return Z Coordinate where the failure occurred*/
	public int getZ(){
		return this.zCoord;
	}
	/**@return name of the block which failed*/
	public String getBlock(){
		return this.blockName;
	}
}
