package sonar.core.integration.multipart;

import java.util.UUID;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.SonarLoader;
import sonar.core.network.PacketByteBufMultipart;
import sonar.core.network.PacketMultipartSync;
import sonar.core.network.utils.IByteBufTile;

public class SonarMultipartHelper {

	public static AdvancedRayTraceResultPart collisionRayTrace(IMultipartContainer container, Vec3d start, Vec3d end) {
		double dist = Double.POSITIVE_INFINITY;
		AdvancedRayTraceResultPart current = null;
		for (IMultipart p : container.getParts()) {
			AdvancedRayTraceResultPart result = p.collisionRayTrace(start, end);
			if (result == null) continue;
			double d = result.squareDistanceTo(start);
			if (d <= dist) {
				dist = d;
				current = result;
			}
		}
		return current;
	}

	public static Object getTile(World world, BlockPos pos) {
		if (SonarLoader.mcmultipartLoaded) {
			IMultipartContainer container = MultipartHelper.getPartContainer(world, pos);
			if (container != null) {
				return container;
			}
		}
		return world.getTileEntity(pos);
	}

	@Deprecated
	public static Object getPart(int partID, World world, BlockPos pos) {
		Object object = getTile(world, pos);
		if (SonarLoader.mcmultipartLoaded && object instanceof IMultipartContainer) {
			IMultipartContainer container = (IMultipartContainer) object;
			IMultipart part = container.getPartInSlot(PartSlot.VALUES[partID]);
			if (part != null) {
				return part;
			}
		}
		return object;
	}

	public static IMultipart getPartFromHash(int hashCode, World world, BlockPos pos) {
		Object object = getTile(world, pos);
		if (SonarLoader.mcmultipartLoaded && object instanceof IMultipartContainer) {
			return getPartFromHash(hashCode, (IMultipartContainer) object);
		}
		return null;
	}

	public static IMultipart getPartFromHash(int hashCode, IMultipartContainer container) {
		for (IMultipart part : container.getParts()) {
			if (part != null && container.getPartID(part).hashCode() == hashCode) {
				return part;
			}
		}
		return null;
	}

	public static IMultipart getPart(UUID partUUID, World world, BlockPos pos) {
		Object object = getTile(world, pos);
		if (SonarLoader.mcmultipartLoaded && object instanceof IMultipartContainer) {
			IMultipartContainer container = (IMultipartContainer) object;
			IMultipart part = container.getPartFromID(partUUID);
			if (part != null) {
				return (IMultipart) part;
			}
		}
		return (IMultipart) null;
	}

	public static boolean sendMultipartSyncAround(SonarMultipart part, int spread) {
		if (!part.getWorld().isRemote && part != null && part instanceof INBTSyncable) {
			NBTTagCompound tag = ((INBTSyncable) part).writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendToAllAround(buildSyncPacket(part, tag), new TargetPoint(part.getWorld().provider.getDimension(), part.getPos().getX(), part.getPos().getY(), part.getPos().getZ(), spread));
				return true;
			}
		}
		return false;
	}

	public static boolean sendMultipartSyncToServer(SonarMultipart part) {
		if (part.getWorld().isRemote && part != null && part instanceof INBTSyncable) {
			NBTTagCompound tag = ((INBTSyncable) part).writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendToServer(buildSyncPacket(part, tag));
				return true;
			}
		}
		return false;
	}

	public static boolean sendMultipartPacketAround(SonarMultipart part, int id, int spread) {
		if (!part.getWorld().isRemote && part != null && part instanceof IByteBufTile) {
			SonarCore.network.sendToAllAround(buildBufPacket(part, id), new TargetPoint(part.getWorld().provider.getDimension(), part.getPos().getX(), part.getPos().getY(), part.getPos().getZ(), spread));
			return true;
		}
		return false;
	}

	public static boolean sendMultipartPacketToServer(SonarMultipart part, int id) {
		if (part.getWorld().isRemote && part != null && part instanceof IByteBufTile) {
			SonarCore.network.sendToServer(buildBufPacket(part, id));
			return true;
		}
		return false;
	}

	public static PacketMultipartSync buildSyncPacket(SonarMultipart part, NBTTagCompound tag) {
		return new PacketMultipartSync(part.getPos(), tag, part.getUUID());
	}

	public static PacketByteBufMultipart buildBufPacket(SonarMultipart part, int id) {
		return new PacketByteBufMultipart(part.getUUID(), (IByteBufTile) part, part.getPos(), id);
	}

}
