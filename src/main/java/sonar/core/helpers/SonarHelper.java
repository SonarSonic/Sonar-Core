package sonar.core.helpers;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.IWorldPosition;
import sonar.core.utils.SortingDirection;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * helps with getting tiles, adding energy and checking stacks
 */
public class SonarHelper {

	private static List<EnumFacing> face_values;

	public static List<EnumFacing> getEnumFacingValues(){
		return face_values == null ? face_values = convertArray(EnumFacing.VALUES) : face_values;
	}

    public static ChunkPos getChunkFromPos(int xPos, int zPos) {
        return new ChunkPos(xPos >> 4, zPos >> 4);
    }

    public static ChunkPos getChunkPos(int xChunk, int zChunk) {
        return new ChunkPos(xChunk, zChunk);
    }

	public static TileEntity getAdjacentTileEntity(TileEntity tile, EnumFacing side) {
		return tile.getWorld().getTileEntity(tile.getPos().offset(side));
	}

	public static Block getAdjacentBlock(World world, BlockPos pos, EnumFacing side) {
		return world.getBlockState(pos.offset(side)).getBlock();
	}

	public static <TILE> TILE getTile(World world, BlockPos pos, Class<TILE> type){
		TileEntity tile = world.getTileEntity(pos);
		if(tile != null && type.isAssignableFrom(tile.getClass())) {
			return (TILE) tile;
		}
		return null;
	}

	public static <BLOCK> BLOCK getBlock(World world, BlockPos pos, Class<BLOCK> type){
		Block block = world.getBlockState(pos).getBlock();
		if(type.isAssignableFrom(block.getClass())) {
			return (BLOCK) block;
		}
		return null;
	}

	public static Entity getEntity(Class entityClass, IWorldPosition tile, int range, boolean nearest) {
		BlockCoords coords = tile.getCoords();

		AxisAlignedBB aabb = new AxisAlignedBB(coords.getX() - range, coords.getY() - range, coords.getZ() - range, coords.getX() + range, coords.getY() + range, coords.getZ() + range);

		List<Entity> entities = coords.getWorld().getEntitiesWithinAABB(entityClass, aabb);
		Entity entity = null;
		double entityDis = nearest ? Double.MAX_VALUE : 0;
        for (Entity target : entities) {
			double d0 = coords.getX() - target.posX;
			double d1 = coords.getY() - target.posY;
			double d2 = coords.getZ() - target.posZ;
			double distance = d0 * d0 + d1 * d1 + d2 * d2;
			if (nearest ? distance < entityDis : distance > entityDis) {
				entity = target;
				entityDis = distance;
			}
		}
		return entity;
	}

	public static EntityPlayerMP getPlayerFromName(String player) {
		List<EntityPlayerMP> server = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayers();
		for (EntityPlayerMP entityPlayer : server) {
			if (entityPlayer.getName().equals(player)) {
				return entityPlayer;
			}
		}
		return null;
	}

	public static EntityPlayer getPlayerFromUUID(UUID player) {
		EntityPlayer fromUUID = null;
		Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(player);
		if (entity instanceof EntityPlayer) {
			fromUUID = (EntityPlayer) entity;
		}
		return fromUUID;
	}

	public static GameProfile getProfileByUUID(UUID playerUUID) {
		if (playerUUID == null) {
			return new GameProfile(UUID.randomUUID(), "ERROR: UUID IS INCORRECT");
		}
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getProfileByUUID(playerUUID);
		} else {
			return new GameProfile(playerUUID, UsernameCache.containsUUID(playerUUID) ? UsernameCache.getLastKnownUsername(playerUUID) : "PLAYER ERROR!");
		}
	}

	public static GameProfile getGameProfileForUsername(String playerName) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(playerName);
	}

	public static EnumFacing getForward(int meta) {
		return EnumFacing.getFront(meta).rotateYCCW();
	}

	public static int getAngleFromMeta(int meta) {
		switch (meta) {
		case 2:
			return 180;
		case 3:
			return 0;
		case 4:
			return 90;
		case 5:
			return 270;
		}
		return 0;
	}

	public static int invertMetadata(int meta) {
		switch (meta) {
		case 0:
			return 0;
		case 1:
			return 5;
		case 2:
			return 4;
		case 3:
			return 3;
		case 4:
			return 2;
		case 5:
			return 1;
		default:
			return -1;
		}
	}

	public static EnumFacing offsetFacing(EnumFacing facing, EnumFacing front) {
		if (facing.getAxis().getPlane() == EnumFacing.Plane.VERTICAL || front.getAxis().getPlane() == EnumFacing.Plane.VERTICAL) {
			return facing;
		} else {
			return EnumFacing.fromAngle(getRenderRotation(front) + getRenderRotation(facing)).getOpposite();
		}
	}

	public static int getRenderRotation(EnumFacing face) {
		switch (face) {
		case SOUTH:
			return 180;
		case WEST:
			return 270;
		case EAST:
			return 90;
		default:
			return 0;
		}
	}

	public static EnumFacing getHorizontal(EnumFacing dir) {
		if (dir == EnumFacing.NORTH) {
			return EnumFacing.EAST;
		}
		if (dir == EnumFacing.EAST) {
			return EnumFacing.SOUTH;
		}
		if (dir == EnumFacing.SOUTH) {
			return EnumFacing.WEST;
		}
		if (dir == EnumFacing.WEST) {
			return EnumFacing.NORTH;
		}
		return dir; //DEFAULT
	}

	public static ArrayList<BlockCoords> getConnectedBlocks(Block block, List<EnumFacing> dirs, World w, BlockPos pos, int max) {
        ArrayList<BlockCoords> handlers = new ArrayList<>();
		addCoords(block, w, pos, max, handlers, dirs);
		return handlers;
	}

	public static void addCoords(Block block, World w, BlockPos pos, int max, ArrayList<BlockCoords> handlers, List<EnumFacing> dirs) {
		for (EnumFacing side : dirs) {
			if (handlers.size() > max) {
				return;
			}
			BlockPos current = pos.offset(side);
			IBlockState state = w.getBlockState(current);
            Block tile = w.getBlockState(current).getBlock();
			if (tile == block) {
				BlockCoords coords = new BlockCoords(current);
				if (!handlers.contains(coords)) {
					handlers.add(coords);
                    addCoords(block, w, current, max, handlers, SonarHelper.convertArray(EnumFacing.values()));
				}
			}
		}
	}

	public static <E extends Enum> E incrementEnum(E enumObj, E[] values) {
		int ordinal = enumObj.ordinal() + 1;
		if (ordinal < values.length) {
			return values[ordinal];
		} else {
			return values[0];
		}
	}

	public static <T> List<T> convertArray(T[] objs) {
        List<T> inputs = new ArrayList<>();
        Collections.addAll(inputs, objs);
		return inputs;
	}

	public static <T> T[] convertArray(List<T> objs) {
		return (T[]) objs.toArray();
	}


	public static boolean intContains(int[] ints, int num) {
		for (int i : ints) {
			if (i == num) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean arrayContains(T[] ints, T num) {
		for (T i : ints) {
			if (i == num) {
				return true;
			}
		}
		return false;
	}

	public static int compareWithDirection(long stored1, long stored2, SortingDirection dir) {
		if (stored1 < stored2)
			return dir == SortingDirection.DOWN ? 1 : -1;
		if (stored1 == stored2)
			return 0;
		return dir == SortingDirection.DOWN ? -1 : 1;
	}

	public static int compareStringsWithDirection(String string1, String string2, SortingDirection dir) {
		int res = String.CASE_INSENSITIVE_ORDER.compare(string1, string2);
		if (res == 0) {
			res = string1.compareTo(string2);
		}
		return dir == SortingDirection.DOWN ? res : -res;
	}

    public static List<EntityPlayerMP> getPlayersWatchingChunk(PlayerChunkMapEntry entry) {
        if (entry != null && entry.isSentToPlayers()) {
            try {
                Field field = PlayerChunkMapEntry.class.getDeclaredField("players");
                field.setAccessible(true);
                List<EntityPlayerMP> obj = (List<EntityPlayerMP>) field.get(entry);
                return Lists.newArrayList(obj);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return new ArrayList<>();

    }
    
    @Nullable
    public static EnumFacing getBlockDirection(BlockPos main, BlockPos dirPos){
    	for(EnumFacing face : EnumFacing.VALUES){
    		if(main.offset(face).equals(dirPos)){
    			return face;
    		}
    	}
    	return null;
    }
}
