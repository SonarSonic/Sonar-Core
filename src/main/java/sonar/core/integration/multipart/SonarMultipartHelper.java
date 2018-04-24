package sonar.core.integration.multipart;

import java.util.Optional;
import java.util.function.Function;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.IPartSlot;
import mcmultipart.api.world.IMultipartBlockAccess;
import mcmultipart.api.world.IMultipartWorld;
import mcmultipart.slot.SlotRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.SonarLoader;
import sonar.core.network.PacketByteBuf;
import sonar.core.network.PacketByteBufMultipart;
import sonar.core.network.PacketMultipartSync;
import sonar.core.network.PacketTileSync;
import sonar.core.network.PacketTileSyncUpdate;
import sonar.core.network.utils.IByteBufTile;

public class SonarMultipartHelper {

	public static Optional<IMultipart> getMultipart(IBlockAccess world, BlockPos pos, IPartSlot slot, Function<IMultipart, Boolean> valid) {
		Optional<IMultipart> multipart = MultipartHelper.getPart(world, pos, slot);
		return multipart.isPresent() && valid.apply(multipart.get()) ? multipart : Optional.empty();
	}

	public static Optional<IMultipartTile> getMultipartTile(IBlockAccess world, BlockPos pos, IPartSlot slot, Function<IMultipartTile, Boolean> valid) {
		Optional<IMultipartTile> multipart = MultipartHelper.getPartTile(world, pos, slot);
		return multipart.isPresent() && valid.apply(multipart.get()) ? multipart : Optional.empty();
	}

	public static Optional<IMultipartTile> getMultipartTileFromSlotID(IBlockAccess world, BlockPos pos, int id) {
		return getMultipartTile(world, pos, SlotRegistry.INSTANCE.getSlotFromID(id), part -> true);
	}

	public static World unwrapWorld(World world) {
		if (world instanceof IMultipartWorld) {
			return ((IMultipartWorld) world).getActualWorld();
		}
		return world;
	}

	public static IBlockAccess unwrapBlockAccess(IBlockAccess world) {
		if (world instanceof IMultipartBlockAccess) {
			return ((IMultipartBlockAccess) world).getActualWorld();
		}
		return world;
	}
	
	public static Object getTile(World world, BlockPos pos) {
		if (SonarLoader.mcmultipartLoaded) {
			Optional<IMultipartContainer> container = MultipartHelper.getContainer(world, pos);
			if (container.isPresent()) {
				return container;
			}
		}
		return world.getTileEntity(pos);
	}

	/* public static IMultipart getPartFromHash(int hashCode, World world, BlockPos pos) { Object object = getTile(world, pos); if (SonarLoader.mcmultipartLoaded && object instanceof IMultipartContainer) { return getPartFromHash(hashCode, (IMultipartContainer) object); } return null; } public static IMultipart getPartFromHash(int hashCode, IMultipartContainer container) { for (IMultipart part : container.getParts()) { if (part != null && container.getPartID(part).hashCode() == hashCode) { return part; } } return null; } public static IMultipart getPart(UUID partUUID, World world, BlockPos pos) { Object object = getTile(world, pos); if (SonarLoader.mcmultipartLoaded && object instanceof IMultipartContainer) { IMultipartContainer container = (IMultipartContainer) object; IMultipart part = container.getPartFromID(partUUID); if (part != null) { return (IMultipart) part; } } return (IMultipart) null; } */
	public static boolean sendMultipartSyncToPlayer(TileSonarMultipart part, EntityPlayerMP player) {
		if (part != null && part.getWorld() != null && !part.getWorld().isRemote) {
			NBTTagCompound tag = part.writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendTo(buildSyncPacket(part, tag, SyncType.SYNC_OVERRIDE), player);
				return true;
			}
		}
		return false;
	}

	public static boolean sendMultipartSyncAround(TileSonarMultipart part, int spread) {
		if (part != null && part.getWorld() != null && !part.getWorld().isRemote) {
			NBTTagCompound tag = part.writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendToAllAround(buildSyncPacket(part, tag, SyncType.SYNC_OVERRIDE), new TargetPoint(part.getWorld().provider.getDimension(), part.getPos().getX(), part.getPos().getY(), part.getPos().getZ(), spread));
				return true;
			}
		}
		return false;
	}

	public static boolean sendMultipartUpdateSyncAround(TileSonarMultipart part, int spread) {
		if (part != null && part.getWorld() != null && !part.getWorld().isRemote) {
			NBTTagCompound tag = part.writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendToAllAround(buildSyncPacketUpdate(part, tag, SyncType.SYNC_OVERRIDE), new TargetPoint(part.getWorld().provider.getDimension(), part.getPos().getX(), part.getPos().getY(), part.getPos().getZ(), spread));
				return true;
			}
		}
		return false;
	}

	public static boolean sendMultipartSyncToServer(TileSonarMultipart part) {
		if (part != null && part.getWorld() != null && part.getWorld().isRemote) {
			NBTTagCompound tag = part.writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendToServer(buildSyncPacket(part, tag, SyncType.SYNC_OVERRIDE));
				return true;
			}
		}
		return false;
	}

	public static boolean sendMultipartPacketAround(TileSonarMultipart part, int id, int spread) {
		if (part != null && !part.getWorld().isRemote && part instanceof IByteBufTile) {
			SonarCore.network.sendToAllAround(buildBufPacket(part, id), new TargetPoint(part.getWorld().provider.getDimension(), part.getPos().getX(), part.getPos().getY(), part.getPos().getZ(), spread));
			return true;
		}
		return false;
	}

	public static boolean sendMultipartPacketToServer(TileSonarMultipart part, int id) {
		if (part != null && part.getWorld().isRemote && part instanceof IByteBufTile) {
			SonarCore.network.sendToServer(buildBufPacket(part, id));
			return true;
		}
		return false;
	}

	public static IMessage buildRequestSyncPacket(TileSonarMultipart part, NBTTagCompound tag, SyncType type) {
		int slotID = part.getSlotID();
		if (slotID == -1) { // if slotID == -1 we assume the Multipart is still a single TileEntity
			return new PacketTileSync(part.getPos(), tag, type);
		}
		return new PacketMultipartSync(part.getPos(), tag, type, slotID);
	}

	public static IMessage buildSyncPacketUpdate(TileSonarMultipart part, NBTTagCompound tag, SyncType type) {
		int slotID = part.getSlotID();
		if (slotID == -1) { // if slotID == -1 we assume the Multipart is still a single TileEntity
			return new PacketTileSyncUpdate(part.getPos(), tag, type);
		}
		return new PacketMultipartSync(part.getPos(), tag, type, slotID);
	}

	public static IMessage buildSyncPacket(TileSonarMultipart part, NBTTagCompound tag, SyncType type) {
		int slotID = part.getSlotID();
		if (slotID == -1) { // if slotID == -1 we assume the Multipart is still a single TileEntity
			return new PacketTileSync(part.getPos(), tag, type);
		}
		return new PacketMultipartSync(part.getPos(), tag, type, slotID);
	}

	public static IMessage buildBufPacket(TileSonarMultipart part, int id) {
		int slotID = part.getSlotID();
		if (slotID == -1) { // if slotID == -1 we assume the Multipart is still a single TileEntity
			return new PacketByteBuf((IByteBufTile) part, part.getPos(), id);
		}
		return new PacketByteBufMultipart(slotID, (IByteBufTile) part, part.getPos(), id);
	}

}