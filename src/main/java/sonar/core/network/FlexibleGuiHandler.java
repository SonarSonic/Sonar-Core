package sonar.core.network;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.integration.SonarLoader;
import sonar.core.integration.multipart.SonarMultipartHelper;

public class FlexibleGuiHandler {

	public static String MULTIPART = "multipart", TILEENTITY = "tile", ITEM = "item", ID = "id", UUID = "uuid";

	public Object getServerElement(int id, EntityPlayer player, World world, BlockPos pos, NBTTagCompound tag) {
		Object obj = null;
		if (SonarLoader.mcmultipartLoaded && tag.getBoolean(MULTIPART)) {
			UUID uuid = tag.getUniqueId(UUID);
			obj = SonarMultipartHelper.getPart(uuid, world, pos);
		} else if (tag.getBoolean(TILEENTITY)) {
			obj = world.getTileEntity(pos);
		} else if (tag.getBoolean(ITEM)) {
			obj = player.getHeldItemMainhand();
		}
		if (obj == null) {
			return null;
		}
		if (obj instanceof ItemStack && ((ItemStack) obj).getItem() instanceof IFlexibleGui) {
			return ((IFlexibleGui) ((ItemStack) obj).getItem()).getServerElement(obj, id, world, player, tag);
		} else if (obj instanceof IFlexibleGui) {
			return ((IFlexibleGui) obj).getServerElement(obj, id, world, player, tag);
		}
		return null;
	}

	public Object getClientElement(int id, EntityPlayer player, World world, BlockPos pos, NBTTagCompound tag) {
		Object obj = null;
		if (SonarLoader.mcmultipartLoaded && tag.getBoolean(MULTIPART)) {
			UUID uuid = tag.getUniqueId(UUID);
			obj = SonarMultipartHelper.getPart(uuid, world, pos);
		} else if (tag.getBoolean(TILEENTITY)) {
			obj = world.getTileEntity(pos);
		} else if (tag.getBoolean(ITEM)) {
			obj = player.getHeldItemMainhand();
		}

		if (obj instanceof ItemStack && ((ItemStack) obj).getItem() instanceof IFlexibleGui) {
			return ((IFlexibleGui) ((ItemStack) obj).getItem()).getClientElement(obj, id, world, player, tag);
		} else if (obj instanceof IFlexibleGui) {
			return ((IFlexibleGui) obj).getClientElement(obj, id, world, player, tag);
		}
		return null;
	}

	public void openBasicTile(TileEntity tile, EntityPlayer player, World world, BlockPos pos, int id) {
		if (world.isRemote) {
			return;
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean(TILEENTITY, true);
		openGui(player, world, pos, id, tag);
	}

	public void openBasicMultipart(UUID multipartUUID, EntityPlayer player, World world, BlockPos pos, int id) {
		if (world.isRemote) {
			return;
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean(MULTIPART, true);
		tag.setUniqueId(UUID, multipartUUID);
		openGui(player, world, pos, id, tag);
	}

	public void openBasicItemStack(ItemStack stack, EntityPlayer player, World world, BlockPos pos, int id) {
		if (world.isRemote) {
			return;
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean(ITEM, true);
		openGui(player, world, pos, id, tag);
	}

	public void openGui(EntityPlayer player, World world, BlockPos pos, int id, NBTTagCompound tag) {
		if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer)) {
			EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
			Container container = (Container) getServerElement(id, player, world, pos, tag);
			if (container != null) {
				entityPlayerMP.getNextWindowId();
				entityPlayerMP.closeContainer();
				int windowId = entityPlayerMP.currentWindowId;
				tag.setInteger(ID, id);
				SonarCore.network.sendTo(new PacketFlexibleOpenGui(pos, windowId, tag), (EntityPlayerMP) player);
				entityPlayerMP.openContainer = container;
				entityPlayerMP.openContainer.windowId = windowId;
				entityPlayerMP.openContainer.addListener(entityPlayerMP);
				MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
			}
		}

	}
}
