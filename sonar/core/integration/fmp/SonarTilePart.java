package sonar.core.integration.fmp;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.minecraft.McMetaPart;

public abstract class SonarTilePart extends McMetaPart implements ISyncTile {

	public Object rend;

	public SonarTilePart() {
	}

	public SonarTilePart(int meta) {
		this.meta = (byte) meta;
	}

	@Override
	public Iterable<Cuboid6> getCollisionBoxes() {
		return Arrays.asList(getBounds());
	}

	@Override
	public void load(NBTTagCompound tag) {
		super.load(tag);
		this.readData(tag, SyncType.SAVE);
	}

	@Override
	public void save(NBTTagCompound tag) {
		super.save(tag);
		this.writeData(tag, SyncType.SAVE);
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
	}

	@Override
	public final void writeDesc(MCDataOutput packet) {
		super.writeDesc(packet);
		NBTTagCompound write = new NBTTagCompound();
		writeData(write, SyncType.SAVE);
		packet.writeNBTTagCompound(write);
	}

	@Override
	public final void readDesc(MCDataInput packet) {
		super.readDesc(packet);
		readData(packet.readNBTTagCompound(), SyncType.SAVE);
	}

	@Override
	public void invalidateConvertedTile() {
		TileEntity tile = ((TileEntity) this.world().getTileEntity(x(), y(), z()));
		if (tile instanceof ISyncTile) {
			ISyncTile sync = (ISyncTile) tile;
			NBTTagCompound tag = new NBTTagCompound();
			sync.writeData(tag, SyncType.SAVE);
			this.readData(tag, SyncType.SAVE);
		}
	}

	public abstract Object getSpecialRenderer();

	@Override
	public void onWorldJoin() {
		if (world().isRemote)
			rend = getSpecialRenderer();
	}

	@Override
	public void renderDynamic(Vector3 pos, float frame, int pass) {
		if (world().isRemote && rend != null) {
			if (rend instanceof TileEntitySpecialRenderer) {
				((TileEntitySpecialRenderer) rend).renderTileEntityAt(tile(), pos.x, pos.y, pos.z, 0);
			}
		}
	}

	@Override
	public boolean doesTick() {
		return true;
	}

	public void sendAdditionalPackets(EntityPlayer player) {

	}
}
