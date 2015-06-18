package sonar.core.utils.helpers;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.calculator.mod.api.IWrench;
import sonar.core.utils.SonarAPI;
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
		if (tile instanceof IEnergyHandler) {
			IEnergyHandler handler = (IEnergyHandler) tile;
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
	 * checks if the two itemstacks are equial
	 * 
	 * @param stack1 first stack your checking
	 * @param stack2 second stack your checking
	 * @return if they are equal
	 */
	public static boolean equalStacks(ItemStack stack1, ItemStack stack2) {
		if (stack1 != null && stack2 != null) {
			if (isCircuit(stack1.getItem())) {
				return false;
			}
			return stack1.getItem() != stack2.getItem() ? false : (stack1.getItemDamage() != stack2.getItemDamage() ? false : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack
					.areItemStackTagsEqual(stack1, stack2)));
		}
		return false;
	}

	/**
	 * @param item Item you are checking
	 * @return if the stack is an circuit
	 */
	public static boolean isCircuit(Item item) {

		if (SonarAPI.calculatorLoaded()) {
			if (item == GameRegistry.findItem("Calculator", "CircuitBoard")) {
				return true;
			} else if (item == GameRegistry.findItem("Calculator", "CircuitDamaged")) {
				return true;
			} else if (item == GameRegistry.findItem("Calculator", "CircuitDirty")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checks if a tile implements IWrench and IDropTile and drops it accordingly
	 */
	public static void dropTile(TileEntity te, EntityPlayer player, Block block, World world, int x, int y, int z) {
		IWrench wrench = (IWrench) block;
		if (wrench.canWrench()) {
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
	}
}
