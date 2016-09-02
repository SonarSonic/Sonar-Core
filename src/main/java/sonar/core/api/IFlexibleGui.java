package sonar.core.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IFlexibleGui<T> {

	/**obj will either be either a TileEntity, Multipart or ItemStack
	 * @param world TODO*/
	public Object getServerElement(T obj, int id, World world, EntityPlayer player, NBTTagCompound tag);

	/**obj will either be either a TileEntity, Multipart or ItemStack
	 * @param world TODO*/
	public Object getClientElement(T obj, int id, World world, EntityPlayer player, NBTTagCompound tag);

}
