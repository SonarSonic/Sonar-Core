package sonar.core.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFlexibleGui<T> {

	/**obj will either be either a TileEntity, Multipart or ItemStack*/
	public Object getServerElement(int id, EntityPlayer player, T obj, NBTTagCompound tag);

	/**obj will either be either a TileEntity, Multipart or ItemStack*/
	public Object getClientElement(int id, EntityPlayer player, T obj, NBTTagCompound tag);

}
