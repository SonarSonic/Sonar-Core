package sonar.core.utils;

import net.minecraft.nbt.NBTTagCompound;

/**a tile enity with an Energy Storage that keeps its energy once destroyed*/
public interface IDropTile {
	
	/**sets the Energy Storage stored*/
	public void readInfo(NBTTagCompound tag);

	/**gets the Energy Storage stored*/
	public void writeInfo(NBTTagCompound tag);;
}
