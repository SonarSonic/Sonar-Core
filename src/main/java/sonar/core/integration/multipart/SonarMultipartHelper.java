package sonar.core.integration.multipart;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.SonarLoader;
import sonar.core.network.PacketMultipartSync;

public class SonarMultipartHelper {

	public static Object getTile(World world, BlockPos pos) {
		if (SonarLoader.mcmultipartLoaded) {
			IMultipartContainer container = MultipartHelper.getPartContainer(world, pos);
			if (container != null) {
				return container;
			}
		}
		return world.getTileEntity(pos);
	}

	public static Object getTile(int partID, World world, BlockPos pos) {
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

	public static void sendMultipartSyncAround(IMultipartContainer tile, ISlottedPart part, int spread) {
		if (!tile.getWorldIn().isRemote) {
			if (part != null && part instanceof INBTSyncable) {
				NBTTagCompound tag = ((INBTSyncable) part).writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE);
				if (!tag.hasNoTags()) {
					SonarCore.network.sendToAllAround(new PacketMultipartSync(tile.getPosIn(), tag, part.getSlotMask().iterator().next()), new TargetPoint(tile.getWorldIn().provider.getDimension(), tile.getPosIn().getX(), tile.getPosIn().getY(), tile.getPosIn().getZ(), spread));
				}
			}
		}
	}
}
