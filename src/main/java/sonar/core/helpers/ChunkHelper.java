package sonar.core.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ChunkHelper {

	public static List<EntityPlayerMP> getChunkPlayers(World world, BlockPos pos) {
		return getChunkPlayers(world, new ChunkPos(pos));
	}

	public static List<EntityPlayerMP> getChunkPlayers(World world, ChunkPos pos) {
		if (world instanceof WorldServer) {
			WorldServer server = (WorldServer) world;
			PlayerChunkMap map = server.getPlayerChunkMap();
			PlayerChunkMapEntry entry = map.getEntry(pos.chunkXPos, pos.chunkZPos);
			if (entry != null) {
				return getChunkPlayers(entry);
			}
		}
		return Lists.newArrayList();
	}

	public static List<EntityPlayerMP> getChunkPlayers(World world, List<ChunkPos> chunks) {
		List<EntityPlayerMP> allPlayers = Lists.newArrayList();
		if (world instanceof WorldServer) {
			WorldServer server = (WorldServer) world;
			PlayerChunkMap map = server.getPlayerChunkMap();
			for (ChunkPos pos : chunks) {
				if (server.getChunkFromChunkCoords(pos.chunkXPos, pos.chunkZPos).isLoaded()) {
					PlayerChunkMapEntry entry = map.getEntry(pos.chunkXPos, pos.chunkZPos);
					if (entry != null) {
						List<EntityPlayerMP> players = getChunkPlayers(entry);
						for (EntityPlayerMP player : players) {
							if (!allPlayers.contains(player)) {
								allPlayers.add(player);
							}
						}
					}
				}
			}
		}
		return allPlayers;
	}

	public static List<ChunkPos> getChunksInRadius(BlockPos pos, double radius) {
		List<ChunkPos> entities = new ArrayList<ChunkPos>();

		int smallX = MathHelper.floor_double((pos.getX() - radius) / 16.0D);
		int bigX = MathHelper.floor_double((pos.getX() + radius) / 16.0D);
		int smallZ = MathHelper.floor_double((pos.getZ() - radius) / 16.0D);
		int bigZ = MathHelper.floor_double((pos.getZ() + radius) / 16.0D);

		for (int x = smallX; x <= bigX; x++) {
			for (int z = smallZ; z <= bigZ; z++) {
				entities.add(new ChunkPos(x, z));
			}
		}
		return entities;
	}

	public static List<EntityPlayerMP> getChunkPlayers(PlayerChunkMapEntry entry) {
		try {
			Field f = entry.getClass().getDeclaredField("players");
			f.setAccessible(true);
			return (List<EntityPlayerMP>) f.get(entry);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// e.printStackTrace();
		}
		return Lists.newArrayList();
	}
}
