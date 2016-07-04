package sonar.core.integration.fmp;
/*
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.ISyncPart;


public abstract class SonarTilePart extends McMetaPart implements INBTSyncable {

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
		List<ISyncPart> parts = new ArrayList();
		this.addSyncParts(parts);
		for (ISyncPart part : parts) {
			if (part.canSync(type))
				part.readFromNBT(nbt, type);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		List<ISyncPart> parts = new ArrayList();
		this.addSyncParts(parts);
		for (ISyncPart part : parts) {
			if (part.canSync(type))
				part.writeToNBT(nbt, type);
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
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

	public void sendSyncPacket(EntityPlayer player) {
		if (world().isRemote) {
			return;
		}
		if (player != null && player instanceof EntityPlayerMP) {
			NBTTagCompound tag = new NBTTagCompound();
			writeData(tag, SyncType.DEFAULT_SYNC);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendTo(new PacketTileSync(x(), y(), z(), tag), (EntityPlayerMP) player);
			}
		}
	}

	@Override
	public void invalidateConvertedTile() {
		TileEntity tile = ((TileEntity) this.world().getTileEntity(x(), y(), z()));
		if (tile instanceof INBTSyncable) {
			INBTSyncable sync = (INBTSyncable) tile;
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
*/