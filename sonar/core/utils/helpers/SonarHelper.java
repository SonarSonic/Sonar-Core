package sonar.core.utils.helpers;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.SonarAPI;
import sonar.core.utils.BlockCoords;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.registry.GameRegistry;

/** helps with getting tiles, adding energy and checking stacks */
public class SonarHelper {

	/**
	 * @param tile Tile Entity you're checking from
	 * @param side side to find IEnergyHandler
	 * @return if there is an adjacent Energy Hander
	 */
	public static boolean isAdjacentEnergyHandlerFromSide(TileEntity tile, int side) {
		TileEntity handler = getAdjacentTileEntity(tile, ForgeDirection.getOrientation(side));
		return isEnergyHandlerFromSide(handler, ForgeDirection.VALID_DIRECTIONS[side ^ 1]);
	}

	public static boolean isAdjacentEnergyHandlerFromSide(TileEntity tile, ForgeDirection side) {
		TileEntity handler = getAdjacentTileEntity(tile, side);
		return isEnergyHandlerFromSide(handler, side.getOpposite());
	}

	/**
	 * @param tile Tile Entity you want to check
	 * @param from direction your adding from
	 * @return if Handler can connect
	 */
	public static boolean isEnergyHandlerFromSide(TileEntity tile, ForgeDirection from) {
		if (tile instanceof IEnergyProvider || tile instanceof IEnergyReceiver) {
			IEnergyConnection handler = (IEnergyConnection) tile;
			return handler.canConnectEnergy(from);
		}

		if (tile instanceof IEnergySink) {
			IEnergySink handler = (IEnergySink) tile;
			return handler.acceptsEnergyFrom(tile, from);
		}
		return false;
	}

	/**
	 * @param tile Tile Entity you're checking from
	 * @param side direction from Tile Entity your checking from
	 * @return adjacent Tile Entity
	 */
	public static TileEntity getAdjacentTileEntity(TileEntity tile, ForgeDirection side) {

		return tile.getWorldObj().getTileEntity(tile.xCoord + side.offsetX, tile.yCoord + side.offsetY, tile.zCoord + side.offsetZ);
	}

	public static Block getAdjacentBlock(World world, BlockCoords coords, ForgeDirection side) {
		return world.getBlock(coords.getX() + side.offsetX, coords.getY() + side.offsetY, coords.getZ() + side.offsetZ);
	}
	/**
	 * @param tile Tile Entity you are checking
	 * @return if the Tile is an Energy Handler
	 */
	public static boolean isEnergyHandler(TileEntity tile) {
		if (tile instanceof IEnergyHandler) {
			return true;
		} else if (SonarAPI.ic2Loaded() && tile instanceof IEnergyTile) {
			return true;
		}
		return false;
	}

	/**
	 * Add energy to an IEnergyReciever, internal distribution is left entirely to the IEnergyReciever.
	 * 
	 * @param from Orientation the energy is received from.
	 * @param maxReceive Maximum amount of energy to receive.
	 * @param simulate If TRUE, the charge will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated) received.
	 */
	public static int pushEnergy(TileEntity tile, ForgeDirection dir, int amount, boolean simulate) {
		if (tile instanceof IEnergyReceiver) {
			IEnergyReceiver handler = (IEnergyReceiver) tile;
			return handler.receiveEnergy(dir, amount, simulate);
		}
		return 0;

	}

	/**
	 * Remove energy from an IEnergyProvider, internal distribution is left entirely to the IEnergyProvider.
	 * 
	 * @param from Orientation the energy is extracted from.
	 * @param maxExtract Maximum amount of energy to extract.
	 * @param simulate If TRUE, the extraction will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated) extracted.
	 */
	public static int pullEnergy(TileEntity tile, ForgeDirection dir, int amount, boolean simulate) {
		if (tile instanceof IEnergyProvider) {
			IEnergyProvider handler = (IEnergyProvider) tile;
			return handler.extractEnergy(dir, amount, simulate);
		}
		return 0;

	}

	/**
	 * checks if a tile implements IWrench and IDropTile and drops it accordingly
	 */
	public static void dropTile(EntityPlayer player, Block block, World world, int x, int y, int z) {
		if (SonarAPI.calculatorLoaded() && block == GameRegistry.findBlock("Calculator", "ConductorMastBlock")) {
			if (world.getBlock(x, y - 1, z) == GameRegistry.findBlock("Calculator", "ConductorMast")) {
				block.harvestBlock(world, player, x, y - 1, z, world.getBlockMetadata(x, y - 1, z));
			} else if (world.getBlock(x, y - 2, z) == GameRegistry.findBlock("Calculator", "ConductorMast")) {

				block.harvestBlock(world, player, x, y - 2, z, world.getBlockMetadata(x, y - 2, z));
			} else if (world.getBlock(x, y - 3, z) == GameRegistry.findBlock("Calculator", "ConductorMast")) {

				block.harvestBlock(world, player, x, y - 3, z, world.getBlockMetadata(x, y - 3, z));
			}
		} else {
			block.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		}

	}

	public static Entity getNearestEntity(Class entityClass, TileEntity tile, int range) {

		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(tile.xCoord - range, tile.yCoord - range, tile.zCoord - range, tile.xCoord + range, tile.yCoord + range, tile.zCoord + range);

		List<Entity> entities = tile.getWorldObj().getEntitiesWithinAABB(entityClass, aabb);
		Entity entity = null;
		double closest = Double.MAX_VALUE;
		for (int i = 0; i < entities.size(); i++) {
			Entity target = (Entity) entities.get(i);
			double d0 = tile.xCoord - target.posX;
			double d1 = tile.yCoord - target.posY;
			double d2 = tile.zCoord - target.posZ;
			double distance = d0 * d0 + d1 * d1 + d2 * d2;

			if (distance < closest) {
				entity = target;
				closest = distance;
			}

		}
		return entity;

	}

	public static ForgeDirection getForward(int meta) {
		return ForgeDirection.getOrientation(meta).getOpposite();
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
}
