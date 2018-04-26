package sonar.core.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import sonar.core.SonarCore;

/**
 * an object with a blocks x, y and z coordinates
 */
public class BlockCoords {

	public static final String X = "x", Y = "y", Z = "z", HAS_DIMENSION = "hasDimension", DIMENSION = "dimension";
	public static final BlockCoords EMPTY = new BlockCoords(0, 0, 0);
	private BlockPos pos;
	private int dimension;
	private boolean hasDimension;

    /**
     * @param x block x coordinate
	 * @param y block y coordinate
     * @param z block z coordinate
     */
	public BlockCoords(int x, int y, int z) {
		this.pos = new BlockPos(x, y, z);
		this.hasDimension = false;
	}

    @Deprecated
	public BlockCoords(int x, int y, int z, World world) {
		this.pos = new BlockPos(x, y, z);
		this.hasDimension = true;
		this.dimension = world.provider.getDimension();
	}

	public BlockCoords(int x, int y, int z, int dimension) {
		this.pos = new BlockPos(x, y, z);
		this.hasDimension = true;
		this.dimension = dimension;
	}

	public BlockCoords(BlockPos pos) {
		this.pos = pos;
		this.hasDimension = false;
	}

	public BlockCoords(BlockPos pos, World world) {
		this.pos = pos;
		this.hasDimension = true;
		this.dimension = world.provider.getDimension();
	}

	public BlockCoords(BlockPos pos, int dimension) {
		this.pos = pos;
		this.hasDimension = true;
		this.dimension = dimension;
	}

	public BlockCoords(TileEntity tile) {
		this.pos = tile.getPos();
		if (tile.getWorld() == null) {
			this.hasDimension = false;
		} else {
			this.hasDimension = true;
			this.dimension = tile.getWorld().provider.getDimension();
		}
    }

	public BlockCoords(TileEntity tile, int dimension) {
		this.pos = tile.getPos();
		this.hasDimension = true;
		this.dimension = dimension;
	}

    /**
     * @return blocks position
     */
	public BlockPos getBlockPos() {
		return pos;
	}

    /**
     * @return blocks X coordinates
     */
	public int getX() {
		return pos.getX();
	}

    /**
     * @return blocks Y coordinates
     */
	public int getY() {
		return pos.getY();
	}

    /**
     * @return blocks Z coordinates
     */
	public int getZ() {
		return pos.getZ();
	}

	public void setX(int x) {
		this.pos = new BlockPos(x, pos.getY(), pos.getZ());
	}

	public void setY(int y) {
		this.pos = new BlockPos(pos.getX(), y, pos.getZ());
	}

	public void setZ(int z) {
		this.pos = new BlockPos(pos.getX(), pos.getY(), z);
	}

    /**
     * @return dimension
     */
	public int getDimension() {
		return this.dimension;
	}

	public boolean hasDimension() {
		return this.hasDimension;
	}

	public Block getBlock(World world) {
		return world.getBlockState(pos).getBlock();
	}

	public TileEntity getTileEntity(World world) {
		return world.getTileEntity(pos);
	}

	
	public Block getBlock() {
		if (this.hasDimension()) {
			return getWorld().getBlockState(pos).getBlock();
		} else {
			return null;
		}
	}
	
	public IBlockState getBlockState(World world) {
		return world.getBlockState(pos);
	}

	
	public IBlockState getBlockState() {
		if (this.hasDimension()) {
			return getWorld().getBlockState(pos);
		} else {
			return null;
		}
	}

	public TileEntity getTileEntity() {
		if (this.hasDimension()) {
			return getWorld().getTileEntity(pos);
		} else {
			return null;
		}
	}
	
	/**do not use on client side!*/	
	
	public World getWorld() {
        return SonarCore.proxy.getDimension(getDimension());
	}
	
    public boolean insideChunk(ChunkPos pos) {
        return pos.x >> 4 == getX() >> 4 && pos.z >> 4 == getZ() >> 4;
    }

    public boolean insideChunk(int chunkX, int chunkZ) {
        return chunkX == getX() >> 4 && chunkZ == getZ() >> 4;
	}

	public boolean isChunkLoaded(World world){
		return world.isBlockLoaded(pos, false);
	}
	
	public boolean isChunkLoaded(){
		return getWorld().isBlockLoaded(pos, false);
	}
	
	public static void writeToBuf(ByteBuf tag, BlockCoords coords) {
		tag.writeInt(coords.pos.getX());
		tag.writeInt(coords.pos.getY());
		tag.writeInt(coords.pos.getZ());
		tag.writeInt(coords.dimension);
	}

	public static BlockCoords readFromBuf(ByteBuf tag) {
		return new BlockCoords(tag.readInt(), tag.readInt(), tag.readInt(), tag.readInt());
	}

	public static NBTTagCompound writeToNBT(NBTTagCompound tag, BlockCoords coords) {
		tag.setInteger(X, coords.getX());
		tag.setInteger(Y, coords.getY());
		tag.setInteger(Z, coords.getZ());
		tag.setBoolean(HAS_DIMENSION, coords.hasDimension);
		tag.setInteger(DIMENSION, coords.dimension);
		return tag;
	}

	public static boolean hasCoords(NBTTagCompound tag) {
		return tag.hasKey(X) && tag.hasKey(Y) && tag.hasKey("z");
	}

	public static BlockCoords readFromNBT(NBTTagCompound tag) {
		if (tag.getBoolean(HAS_DIMENSION)) {
			return new BlockCoords(tag.getInteger(X), tag.getInteger(Y), tag.getInteger(Z), tag.getInteger(DIMENSION));
		}
		return new BlockCoords(tag.getInteger(X), tag.getInteger(Y), tag.getInteger(Z));
	}

	public static NBTTagCompound writeBlockCoords(NBTTagCompound tag, List<BlockCoords> coords, String tagName) {
		NBTTagList list = new NBTTagList();
		if (coords != null) {
            for (BlockCoords coord : coords) {
                if (coord != null) {
					NBTTagCompound compound = new NBTTagCompound();
                    writeToNBT(compound, coord);
					list.appendTag(compound);
				}
			}
		}
		tag.setTag(tagName, list);
		return tag;
	}

	public static NBTTagCompound writeBlockCoords(NBTTagCompound tag, BlockCoords[] coords) {
		NBTTagList list = new NBTTagList();
		if (coords != null) {
			for (int i = 0; i < coords.length; i++) {
				if (coords[i] != null) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setByte("Slot", (byte) i);
					writeToNBT(compound, coords[i]);
					list.appendTag(compound);
				}
			}
		}
		tag.setTag("BlockCoords", list);
		return tag;
	}

	public static ArrayList<BlockCoords> readBlockCoords(NBTTagCompound tag, String tagName) {
        ArrayList<BlockCoords> coords = new ArrayList<>();
		if (tag.hasKey(tagName)) {
			NBTTagList list = tag.getTagList(tagName, 10);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				coords.add(readFromNBT(compound));
			}
		}
		return coords;
	}

	public static BlockCoords[] readBlockCoords(NBTTagCompound tag, int listSize) {
		NBTTagList list = tag.getTagList("BlockCoords", 10);
		BlockCoords[] coords = new BlockCoords[listSize];
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			byte b = compound.getByte("Slot");
			if (b >= 0 && b < listSize) {
				coords[b] = readFromNBT(compound);
			}
		}
		return coords;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof BlockCoords)) {
			return false;
		}
		BlockCoords coords = (BlockCoords) obj;
		return pos.getX() == coords.pos.getX() && pos.getY() == coords.pos.getY() && pos.getZ() == coords.pos.getZ() && this.dimension == coords.dimension;
	}

	public int hashCode() {
		int result = 1;
		result = 37 * result + (hasDimension ? 0 : 1);
		result = 37 * result + pos.getX();
		result = 37 * result + pos.getY();
		result = 37 * result + pos.getZ();
		result = 37 * result + dimension;
		return result;
	}

	public static boolean equalCoords(BlockCoords coords1, BlockCoords coords2) {
        return coords1 == null && coords2 == null || coords1 == null || coords2 != null && (coords2 == null || coords1.pos.getX() == coords2.pos.getX() && coords1.pos.getY() == coords2.pos.getY() && coords1.pos.getZ() == coords2.pos.getZ() && coords1.dimension == coords2.dimension);
	}

	public static boolean equalCoordArrays(BlockCoords[] coords1, BlockCoords[] coords2) {
		if (coords1.length != coords2.length) {
			return false;
		}
		for (int i = 0; i < coords1.length; i++) {

			if (!equalCoords(coords1[i], coords2[i])) {
				return false;
			}
		}
		return true;
	}

	public static BlockCoords translateCoords(BlockCoords coords, EnumFacing dir) {
		return new BlockCoords(coords.getX() + dir.getFrontOffsetX(), coords.getY() + dir.getFrontOffsetY(), coords.getZ() + dir.getFrontOffsetZ(), coords.dimension);
	}

	public String toString() {
		return "X: " + getX() + " Y: " + getY() + " Z: " + getZ() + " D: " + dimension;
	}

	public BlockCoords fromString(String string) {
		String[] split = string.split(": ");
		int x = Integer.parseInt(split[1]);
		int y = Integer.parseInt(split[3]);
		int z = Integer.parseInt(split[5]);
		int d = Integer.parseInt(split[7]);

		return new BlockCoords(x, y, z, d);
	}

	public boolean contains(Map<BlockCoords, ?> map) {
		for (Entry<BlockCoords, ?> set : map.entrySet()) {
			if (set.getKey().equals(this)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(List<BlockCoords> list) {
		for (BlockCoords coords : list) {
			if (coords.equals(this)) {
				return true;
			}
		}
		return false;
	}
}
