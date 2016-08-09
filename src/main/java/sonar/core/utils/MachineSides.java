package sonar.core.utils;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSaveable;
import sonar.core.network.PacketSonarSides;

public class MachineSides implements INBTSaveable {

	public MachineSideConfig[] configs;
	public ArrayList<EnumFacing> allowedDirs;
	public ArrayList<MachineSideConfig> allowedSides;
	public MachineSideConfig def;
	public TileEntity tile;

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
					allowedDirs.remove((EnumFacing) object);
				}
				if (object instanceof MachineSideConfig) {
					allowedSides.remove((MachineSideConfig) object);
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
		if ((tile instanceof IMachineSides)) {
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
			if (pos == 0) {
				configs[side.getIndex()] = allowedSides.get(allowedSides.size() - 1);
			} else {
				configs[side.getIndex()] = allowedSides.get(pos - 1);
			}
			sendPacket(tile.getWorld().provider.getDimension(), side);
		}
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
			if (allowedSides.size() <= pos + 1) {
				configs[side.getIndex()] = allowedSides.get(0);
			} else {
				configs[side.getIndex()] = allowedSides.get(pos + 1);
			}
			sendPacket(tile.getWorld().provider.getDimension(), side);
		}
		return true;
	}

	public boolean setSide(EnumFacing side, MachineSideConfig config) {
		if (allowedDirs.contains(side) && allowedSides.contains(config)) {
			configs[side.getIndex()] = config;
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
		ArrayList<EnumFacing> sides = new ArrayList();
		for (EnumFacing facing : allowedDirs) {
			if (configs[facing.getIndex()].name() == side.name()) {
				sides.add(facing);
			}
		}
		return sides;
	}

	public MachineSideConfig[] getConfigs() {
		return configs;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagCompound sideNBT = nbt.getCompoundTag("sides");
		for (int i = 0; i < 6; i++) {
			if (sideNBT.hasKey("" + i)) {
				MachineSideConfig side = MachineSideConfig.values()[sideNBT.getInteger("" + i)];
				if (side != null)
					configs[i] = side;
			}
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound sideNBT = new NBTTagCompound();
		for (int i = 0; i < 6; i++) {
			sideNBT.setInteger("" + i, configs[i].ordinal());
		}
		nbt.setTag("sides", sideNBT);
	}

}
