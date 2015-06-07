package sonar.core.utils;


/**an object with a blocks x, y and z coordinates*/
public class BlockCoords extends Object {
	private int xCoord;
	private int yCoord;
	private int zCoord;

	/**@param x block x coordinate
	 * @param y block y coordinate
	 * @param z block z coordinate*/
	public BlockCoords(int x, int y, int z) {
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}

	/**@return blocks X coordinates*/
	public int getX(){
		return this.xCoord;
	}

	/**@return blocks Y coordinates*/
	public int getY(){
		return this.yCoord;
	}

	/**@return blocks Z coordinates*/
	public int getZ(){
		return this.zCoord;
	}
	
}
