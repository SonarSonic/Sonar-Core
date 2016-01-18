package sonar.core.integration.fmp;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.SonarAPI;
import sonar.core.integration.fmp.handlers.TileHandler;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McMetaPart;

public class FMPHelper {

	public static int getMeta(TileEntity tile) {
		Object part = FMPHelper.checkObject(tile);
		if (SonarAPI.forgeMultipartLoaded() && part instanceof McMetaPart) {
			return ((McMetaPart) part).meta;
		} else {
			return tile.getBlockMetadata();
		}
	}

	public static TileHandler getHandler(Object te) {
		te = FMPHelper.checkObject(te);
		if (te!=null && te instanceof ITileHandler) {
			return ((ITileHandler) te).getTileHandler();
		}
		return null;
	}

	public static Object getTile(World world, int x, int y, int z) {
		return world != null ? checkObject(world.getTileEntity(x, y, z)) : null;
	}

	public static Object checkObject(Object object) {
		if (object != null && SonarAPI.forgeMultipartLoaded() && object instanceof TileMultipart) {
			List<TMultiPart> list = ((TileMultipart) object).jPartList();
			if (0 < list.size()) {
				object = list.get(0);
			}

		}
		return object;
	}
}
