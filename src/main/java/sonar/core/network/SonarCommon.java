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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.integration.SonarLoader;
import sonar.core.integration.multipart.SonarMultipartHelper;

public class SonarCommon {

	public Object getStateMapper() {
		return null;
	}

	public void registerRenderThings() {
	}

	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}

	public Object getServerElement(int id, EntityPlayer player, World world, BlockPos pos, NBTTagCompound tag) {
		Object obj = null;
		if (SonarLoader.mcmultipartLoaded && tag.getBoolean("isMultipart")) {
			UUID uuid = tag.getUniqueId("uuid");
			obj = SonarMultipartHelper.getPart(uuid, world, pos);
		} else if (tag.getBoolean("isTileEntity")) {
			obj = world.getTileEntity(pos);
		} else if (tag.getBoolean("isItem")) {
			obj = player.getHeldItemMainhand();
		}
		if (obj != null && obj instanceof IFlexibleGui) {
			return ((IFlexibleGui) obj).getServerElement(id, player, obj, tag);
		}
		return null;
	}

	public Object getClientElement(int id, EntityPlayer player, World world, BlockPos pos, NBTTagCompound tag) {
		Object obj = null;
		if (SonarLoader.mcmultipartLoaded && tag.getBoolean("isMultipart")) {
			UUID uuid = tag.getUniqueId("uuid");
			obj = SonarMultipartHelper.getPart(uuid, world, pos);
		} else if (tag.getBoolean("isTileEntity")) {
			obj = world.getTileEntity(pos);
		} else if (tag.getBoolean("isItem")) {
			obj = player.getHeldItemMainhand();
		}
		if (obj != null && obj instanceof IFlexibleGui) {
			return ((IFlexibleGui) obj).getClientElement(id, player, obj, tag);
		}
		return null;
	}

	public void openBasicTile(TileEntity tile, EntityPlayer player, World world, BlockPos pos, int id) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("isTileEntity", true);
		tag.setInteger("id", id);
		openGui(player, world, pos, id, tag);
	}

	public void openBasicMultipart(UUID multipartUUID, EntityPlayer player, World world, BlockPos pos, int id) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("isMultipart", true);
		tag.setUniqueId("uuid", multipartUUID);
		tag.setInteger("id", id);
		openGui(player, world, pos, id, tag);
	}

	public void openBasicItemStack(ItemStack stack, EntityPlayer player, World world, BlockPos pos, int id) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("isItem", true);
		tag.setInteger("id", id);
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
				entityPlayerMP.openContainer = container;
				entityPlayerMP.openContainer.windowId = windowId;
				entityPlayerMP.openContainer.addListener(entityPlayerMP);
				MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
				SonarCore.network.sendTo(new PacketFlexibleOpenGui(pos, windowId, tag), (EntityPlayerMP) player);
			}
		}
	}
}
