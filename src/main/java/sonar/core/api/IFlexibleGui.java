package sonar.core.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IFlexibleGui<T> {

    default void onGuiOpened(T obj, int id, World world, EntityPlayer player, NBTTagCompound tag){}
	
    /**
     * @param obj    either a TileEntity, Multipart or ItemStack
	 * @param id the GUI ID
	 * @param world the world instance
	 * @param player the player instance
	 * @param tag the tag for the gui
     * @return
     */
    Object getServerElement(T obj, int id, World world, EntityPlayer player, NBTTagCompound tag);

    /**
     * @param obj    either a TileEntity, Multipart or ItemStack
	 * @param id the GUI ID
	 * @param world the world instance
	 * @param player the player instance
	 * @param tag the tag for the gui
     * @return
     */
    Object getClientElement(T obj, int id, World world, EntityPlayer player, NBTTagCompound tag);
}
