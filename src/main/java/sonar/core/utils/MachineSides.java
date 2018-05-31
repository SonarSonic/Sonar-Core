package sonar.core.utils;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.inventory.handling.filters.IExtractFilter;
import sonar.core.inventory.handling.filters.IInsertFilter;
import sonar.core.network.PacketSonarSides;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.ISyncPart;

import javax.annotation.Nonnull;

public class MachineSides extends DirtyPart implements ISyncPart, IInsertFilter, IExtractFilter {

	public MachineSideConfig[] configs;
	public ArrayList<EnumFacing> allowedDirs;
	public ArrayList<MachineSideConfig> allowedSides;
	public MachineSideConfig def;
	public TileEntity tile;
	public int[] input = new int[0], output = new int[0];

	public MachineSides(MachineSideConfig def, TileEntity tile, Object... blocked) {
		this.def = def;
		this.tile = tile;
		if (!(tile instanceof IMachineSides)) {
			SonarCore.logger.warn("Machine Sides: TileEntity doesn't implement IMachineSides!");
		}
		configs = new MachineSideConfig[] { def, def, def, def, def, def };
		ArrayList<EnumFacing> allowedDirs = Lists.newArrayList(EnumFacing.VALUES);
		ArrayList<MachineSideConfig> allowedSides = Lists.newArrayList(MachineSideConfig.ALLOWED_VALUES);
		for (Object object : blocked) {
			if (object != null) {
				if (object instanceof EnumFacing) {
                    allowedDirs.remove(object);
				}
				if (object instanceof MachineSideConfig) {
                    allowedSides.remove(object);
				}
			}
		}
		if (allowedDirs.isEmpty() || allowedSides.isEmpty()) {
			SonarCore.logger.warn("Machine Sides can't have no allowed directions/sides");
			this.allowedDirs = Lists.newArrayList(EnumFacing.VALUES);
			this.allowedSides = Lists.newArrayList(MachineSideConfig.ALLOWED_VALUES);
		} else {
			this.allowedDirs = allowedDirs;
			this.allowedSides = allowedSides;
		}
	}

	public void sendPacket(int dimension, EnumFacing side) {
        if (tile instanceof IMachineSides) {
			BlockPos pos = tile.getPos();
			SonarCore.network.sendToAllAround(new PacketSonarSides(pos, side, getSideConfig(side)), new TargetPoint(dimension, pos.getX(), pos.getY(), pos.getZ(), 64));
		}
	}

	public boolean decreaseSide(EnumFacing side) {
		if (!allowedDirs.contains(side)) {
			return false;
		}
		if (!tile.getWorld().isRemote) {
			String name = configs[side.getIndex()].getName();
			int pos = 0;
			for (MachineSideConfig configs : allowedSides) {
				if (configs.getName().equals(name)) {
					continue;
				}
				pos++;
			}
			configs[side.getIndex()] = allowedSides.get((pos == 0 ? allowedSides.size() : pos) - 1);
			sendPacket(tile.getWorld().provider.getDimension(), side);
		}
		markChanged();
		return true;
	}

	public boolean increaseSide(EnumFacing side) {
		if (!allowedDirs.contains(side)) {
			return false;
		}
		if (!tile.getWorld().isRemote) {
			String name = configs[side.getIndex()].getName();
			int pos = 0;
			for (MachineSideConfig configs : allowedSides) {
				if (configs.getName().equals(name)) {
					break;
				}
				pos++;
			}
			configs[side.getIndex()] = allowedSides.get(allowedSides.size() <= pos + 1 ? 0 : pos + 1);
			sendPacket(tile.getWorld().provider.getDimension(), side);
		}
		markChanged();
		return true;
	}

	public boolean setSide(EnumFacing side, MachineSideConfig config) {
		if (allowedDirs.contains(side) && allowedSides.contains(config)) {
			configs[side.getIndex()] = config;
			markChanged();
			return true;
		}
		return false;
	}

	public boolean resetSides() {
		MachineSideConfig[] sides = new MachineSideConfig[] { def, def, def, def, def, def };
		return true;
	}

	public MachineSideConfig getSideConfig(EnumFacing side) {
		if (!allowedDirs.contains(side)) {
			return MachineSideConfig.NONE;
		}
		return configs[side.getIndex()];
	}

	public ArrayList<EnumFacing> getSidesWithConfig(MachineSideConfig side) {
        ArrayList<EnumFacing> sides = new ArrayList<>();
		for (EnumFacing facing : allowedDirs) {
            if (configs[facing.getIndex()].name().equals(side.name())) {
				sides.add(facing);
			}
		}
		return sides;
	}

	public MachineSideConfig[] getConfigs() {
		return configs;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound sideNBT = new NBTTagCompound();
		for (int i = 0; i < 6; i++) {
            sideNBT.setInteger(String.valueOf(i), configs[i].ordinal());
		}
		nbt.setTag(getTagName(), sideNBT);
		return nbt;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound sideNBT = nbt.getCompoundTag(getTagName());
		for (int i = 0; i < 6; i++) {
            if (sideNBT.hasKey(String.valueOf(i))) {
                MachineSideConfig side = MachineSideConfig.values()[sideNBT.getInteger(String.valueOf(i))];
				if (side != null)
					configs[i] = side;
			}
		}
	}

	@Override
	public boolean canSync(SyncType sync) {
		return sync.isType(SyncType.SAVE, SyncType.DEFAULT_SYNC);
	}

	@Override
	public String getTagName() {
		return "sides";
	}

	@Override
	public Boolean canExtract(int slot, int amount, EnumFacing face) {
		if(getSideConfig(face).isOutput()){
			return SonarHelper.intContains(output, slot);
		}
		return false;
	}

	@Override
	public Boolean canInsert(int slot, @Nonnull ItemStack stack, EnumFacing face) {
		if(getSideConfig(face).isInput()){
			return SonarHelper.intContains(input, slot);
		}
		return false;
	}
}
