package sonar.core.api;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** an object with a blocks x, y and z coordinates */
public class BlockCoords {
	private int xCoord;
	private int yCoord;
	private int zCoord;
	private int dimension;
	private boolean hasDimension;

	/** @param x block x coordinate
	 * @param y block y coordinate
	 * @param z block z coordinate */
	public BlockCoords(int x, int y, int z) {
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
		this.hasDimension = false;
	}

	public BlockCoords(int x, int y, int z, int dimension) {
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
		this.hasDimension = true;
		this.dimension = dimension;
	}

	public BlockCoords(TileEntity tile) {
		this.xCoord = tile.xCoord;
		this.yCoord = tile.yCoord;
		this.zCoord = tile.zCoord;
		if (tile.getWorldObj() == null) {
			this.hasDimension = false;
		} else {
			this.hasDimension = true;
			this.dimension = tile.getWorldObj().provider.dimensionId;
		}
	}

	public BlockCoords(TileEntity tile, int dimension) {
		this.xCoord = tile.xCoord;
		this.yCoord = tile.yCoord;
		this.zCoord = tile.zCoord;
		this.hasDimension = true;
		this.dimension = dimension;
	}

	/** @return blocks X coordinates */
	public int getX() {
		return this.xCoord;
	}

	/** @return blocks Y coordinates */
	public int getY() {
		return this.yCoord;
	}

	/** @return blocks Z coordinates */
	public int getZ() {
		return this.zCoord;
	}

	/** @return dimension */
	public int getDimension() {
		return this.dimension;
	}

	public boolean hasDimension() {
		return this.hasDimension;
	}

	public Block getBlock(World world) {
		return world.getBlock(xCoord, yCoord, zCoord);
	}

	public TileEntity getTileEntity(World world) {

		return world.getTileEntity(xCoord, yCoord, zCoord);
	}

	public Block getBlock() {
		if (this.hasDimension()) {
			return getWorld().getBlock(xCoord, yCoord, zCoord);
		} else {
			return null;
		}
	}

	public TileEntity getTileEntity() {
		if (this.hasDimension()) {
			return getWorld().getTileEntity(xCoord, yCoord, zCoord);
		} else {
			return null;
		}
	}

	public World getWorld() {
		MinecraftServer server = MinecraftServer.getServer();
		World world = server.worldServerForDimension(getDimension());
		return world;
	}

	public static void writeToBuf(ByteBuf tag, BlockCoords coords) {
		tag.writeInt(coords.xCoord);
		tag.writeInt(coords.yCoord);
		tag.writeInt(coords.zCoord);
		tag.writeInt(coords.dimension);
	}

	public static BlockCoords readFromBuf(ByteBuf tag) {
		return new BlockCoords(tag.readInt(), tag.readInt(), tag.readInt(), tag.readInt());
	}

	public static void writeToNBT(NBTTagCompound tag, BlockCoords coords) {
		tag.setInteger("x", coords.xCoord);
		tag.setInteger("y", coords.yCoord);
		tag.setInteger("z", coords.zCoord);
		tag.setBoolean("hasDimension", coords.hasDimension);
		tag.setInteger("dimension", coords.dimension);
	}

	public static BlockCoords readFromNBT(NBTTagCompound tag) {
		if (tag.getBoolean("hasDimension")) {
			return new BlockCoords(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"), tag.getInteger("dimension"));
		}
		return new BlockCoords(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
	}

	public static void writeBlockCoords(NBTTagCompound tag, List<BlockCoords> coords, String tagName) {
		NBTTagList list = new NBTTagList();
		if (coords != null) {
			for (int i = 0; i < coords.size(); i++) {
				if (coords.get(i) != null) {
					NBTTagCompound compound = new NBTTagCompound();
					writeToNBT(compound, coords.get(i));
					list.appendTag(compound);
				}
			}
		}
		tag.setTag(tagName, list);
	}

	public static void writeBlockCoords(NBTTagCompound tag, BlockCoords[] coords) {
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

	}

	public static List<BlockCoords> readBlockCoords(NBTTagCompound tag, String tagName) {
		List<BlockCoords> coords = new ArrayList();
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
		if (obj == null || !(obj instanceof BlockCoords)) {
			return false;
		}
		BlockCoords coords = (BlockCoords) obj;
		return this.xCoord == coords.xCoord && this.yCoord == coords.yCoord && this.zCoord == coords.zCoord && this.dimension == coords.dimension;
	}

	public int hashCode() {
		int result = 1;
		result = 37 * result + (hasDimension ? 0 : 1);
		result = 37 * result + xCoord;
		result = 37 * result + yCoord;
		result = 37 * result + zCoord;
		result = 37 * result + dimension;
		return result;
	}

	public static boolean equalCoords(BlockCoords coords1, BlockCoords coords2) {
		if (coords1 == null && coords2 == null) {
			return true;
		}
		if (coords1 != null && coords2 == null) {
			return false;
		}
		if (coords2 != null && coords1 == null) {
			return false;
		}
		return coords1.xCoord == coords2.xCoord && coords1.yCoord == coords2.yCoord && coords1.zCoord == coords2.zCoord && coords1.dimension == coords2.dimension;
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

	public static BlockCoords translateCoords(BlockCoords coords, ForgeDirection dir) {
		return new BlockCoords(coords.getX() + dir.offsetX, coords.getY() + dir.offsetY, coords.getZ() + dir.offsetZ, coords.dimension);
	}

	public String toString() {
		return "X: " + this.xCoord + " Y: " + this.yCoord + " Z: " + this.zCoord + " D: " + this.dimension;
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
