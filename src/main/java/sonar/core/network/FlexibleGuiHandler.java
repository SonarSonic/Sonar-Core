package sonar.core.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.integration.SonarLoader;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.utils.Pair;

public class FlexibleGuiHandler {

	public Object lastScreen;
	public Object lastContainer;
	public Pair<Object, IFlexibleGui> lastGui;
	public Map<EntityPlayer, Object> lastContainers = new HashMap();
	public Map<EntityPlayer, Pair<Object, IFlexibleGui>> lastGuis = new HashMap();
	public Map<EntityPlayer, Integer> lastID = new HashMap();

	public static String MULTIPART = "multipart", TILEENTITY = "tile", ITEM = "item", ID = "id", UUID = "uuid";

	public Pair<Object, IFlexibleGui> getFlexibleGui(int id, EntityPlayer player, World world, BlockPos pos, NBTTagCompound tag) {
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
			return new Pair(obj, ((ItemStack) obj).getItem());
		} else if (obj instanceof IFlexibleGui) {
			return new Pair(obj, ((IFlexibleGui) obj));
		}
		return null;
	}

	public void openBasicTile(boolean change, TileEntity tile, EntityPlayer player, World world, BlockPos pos, int id) {
		if (world.isRemote) {
			return;
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean(TILEENTITY, true);
		openGui(change, player, world, pos, id, tag);
	}

	public void openBasicMultipart(boolean change, UUID multipartUUID, EntityPlayer player, World world, BlockPos pos, int id) {
		if (world.isRemote) {
			return;
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean(MULTIPART, true);
		tag.setUniqueId(UUID, multipartUUID);
		openGui(change, player, world, pos, id, tag);
	}

	public void openBasicItemStack(boolean change, ItemStack stack, EntityPlayer player, World world, BlockPos pos, int id) {
		if (world.isRemote) {
			return;
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean(ITEM, true);
		openGui(change, player, world, pos, id, tag);
	}

	public void openGui(boolean change, EntityPlayer player, World world, BlockPos pos, int id, NBTTagCompound tag) {
		if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer)) {
			EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
			Pair<Object, IFlexibleGui> gui = this.getFlexibleGui(id, entityPlayerMP, world, pos, tag);
			gui.b.onGuiOpened(gui.a, id, world, entityPlayerMP, tag);
			Container container = (Container) gui.b.getServerElement(gui.a, id, world, player, tag);
			if (container != null) {
				if (!change) {
					entityPlayerMP.getNextWindowId();
					entityPlayerMP.closeContainer();
				} else {
					this.lastContainers.put(player, player.openContainer);
					this.lastGuis.put(player, gui);
					// this.lastID.put(player, player.openContainer.windowId);
				}
				int windowId = entityPlayerMP.currentWindowId;
				tag.setInteger(ID, id);
				SonarCore.network.sendTo(new PacketFlexibleOpenGui(change, pos, windowId, tag), (EntityPlayerMP) player);
				entityPlayerMP.openContainer = container;
				entityPlayerMP.openContainer.windowId = windowId;
				entityPlayerMP.openContainer.addListener(entityPlayerMP);
				// MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer)); FIXME
			}
		}
	}

	public static void changeGui(IFlexibleGui guiTile, int id, int returnID, World world, EntityPlayer player) {
		if (guiTile instanceof SonarMultipart) {
			SonarMultipart multipart = (SonarMultipart) guiTile;
			SonarCore.network.sendToServer(new PacketFlexibleChangeGui(multipart.getUUID(), multipart.getPos(), id, returnID));
		}
	}

	public static Object getLastContainer(EntityPlayer player, Side side) {

		return side.isServer() ? SonarCore.instance.guiHandler.lastContainers.get(player) : SonarCore.instance.guiHandler.lastContainer;

	}

	public static Pair<Object, IFlexibleGui> getLastGui(EntityPlayer player, Side side) {

		return side.isServer() ? SonarCore.instance.guiHandler.lastGuis.get(player) : SonarCore.instance.guiHandler.lastGui;

	}

	public static void setLastContainer(Object obj, EntityPlayer player, Side side) {
		if (side.isServer()) {
			SonarCore.instance.guiHandler.lastContainers.put(player, obj);
		} else {
			SonarCore.instance.guiHandler.lastContainer = obj;
		}
	}

	public static void setLastGui(Pair<Object, IFlexibleGui> obj, EntityPlayer player, Side side) {
		if (side.isServer()) {
			SonarCore.instance.guiHandler.lastGuis.put(player, obj);
		} else {
			SonarCore.instance.guiHandler.lastGui = obj;
		}
	}

	public static boolean closeGui(EntityPlayer player, Side side) {
		Object container = getLastContainer(player, side);
		if (container != null) {
			if (side == Side.CLIENT) {
				player.openContainer = (Container) container;
				Minecraft.getMinecraft().displayGuiScreen((GuiScreen) SonarCore.instance.guiHandler.lastScreen);
				SonarCore.instance.guiHandler.lastScreen = null;
				SonarCore.instance.guiHandler.lastContainer = null;
			} else {
				player.openContainer.onContainerClosed(player);
				Pair<Object, IFlexibleGui> gui = getLastGui(player, side);
				gui.b.onGuiOpened(gui.a, SonarCore.instance.guiHandler.lastID.get(player), player.getEntityWorld(), player, new NBTTagCompound());
				player.openContainer = (Container) container;
				SonarCore.instance.guiHandler.lastContainers.remove(player);
				// SonarCore.instance.guiHandler.lastOpened.remove(player);
				// SonarCore.instance.guiHandler.lastID.remove(player);
			}
			return true;
		}
		return false;
	}
}
