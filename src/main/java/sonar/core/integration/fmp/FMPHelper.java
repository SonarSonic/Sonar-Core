package sonar.core.integration.fmp;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.integration.SonarLoader;
import sonar.core.integration.fmp.handlers.TileHandler;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McMetaPart;

public class FMPHelper {

	public static Object getAdjacentTile(TileEntity tile, ForgeDirection side) {
		if (tile == null || side == null) {
			return null;
		}
		BlockCoords origin = new BlockCoords(tile);
		BlockCoords target = BlockCoords.translateCoords(origin, side);
		return getTile(target.getTileEntity(tile.getWorldObj()));
	}

	public static int getMeta(TileEntity tile) {
		Object part = FMPHelper.checkObject(tile);
		if (SonarLoader.forgeMultipartLoaded() && part instanceof McMetaPart) {
			return ((McMetaPart) part).meta;
		} else {
			return tile.getBlockMetadata();
		}
	}

	public static TileHandler getHandler(Object te) {
		te = FMPHelper.checkObject(te);
		if (te != null && te instanceof ITileHandler) {
			return ((ITileHandler) te).getTileHandler();
		}
		return null;
	}

	public static Object getTile(World world, int x, int y, int z) {
		return world != null ? checkObject(world.getTileEntity(x, y, z)) : null;
	}

	public static Object getTile(Object tile) {
		tile = FMPHelper.checkObject(tile);
		return tile;
	}

	public static Object checkObject(Object object) {
		if (object != null && SonarLoader.forgeMultipartLoaded() && object instanceof TileMultipart) {
			List<TMultiPart> list = ((TileMultipart) object).jPartList();
			if (0 < list.size()) {
				object = list.get(0);
			}

		}
		return object;
	}
}
