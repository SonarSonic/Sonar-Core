package sonar.core.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.calculator.mod.api.IUpgradeCircuits;
import sonar.calculator.mod.api.IWrench;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.utils.IDropTile;
import sonar.core.utils.SonarAPI;
import sonar.core.utils.helpers.SonarHelper;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;

public abstract class SonarBlock extends Block implements IWrench {

	protected Random rand = new Random();
	public boolean orientation,wrenchable;	
	
	protected SonarBlock(Material material, boolean orientation) {
		super(material);
		this.orientation = orientation;
		this.wrenchable = true;
	}

	protected SonarBlock(Material material, boolean orientation, boolean wrenchable) {
		super(material);
		this.orientation = orientation;
		this.wrenchable = wrenchable;
	}

	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitx, float hity, float hitz) {
		if (player != null) {
			if (wrenchable && SonarAPI.calculatorLoaded()
					&& player.getHeldItem() != null
					&& player.getHeldItem().getItem() == GameRegistry.findItem("Calculator", "Wrench")) {
				return false;
			} else {
				return operateBlock(world, x, y, z, player, side, hitx, hity,hitz);
			}
		}
		return false;

	}

	@Override
	public final boolean removedByPlayer(World world, EntityPlayer player,
			int x, int y, int z, boolean willHarvest) {
		if (willHarvest) {

			return true;
		}
		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	/**@return does the block drop as normal*/
	public abstract boolean dropStandard(World world, int x, int y, int z);

	/**standard onBlockActivated for use in Calculators blocks */
	public abstract boolean operateBlock(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitx, float hity, float hitz);

	@Override
	public final void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
		super.harvestBlock(world, player, x, y, z, meta);
		world.setBlockToAir(x, y, z);
	}

	@Override
	public final ArrayList<ItemStack> getDrops(World world, int x, int y,
			int z, int metadata, int fortune) {

		if (dropStandard(world, x, y, z)) {
			return super.getDrops(world, x, y, z, metadata, fortune);
		}

		return Lists.newArrayList(getSpecialDrop(world, x, y, z));
	}

	public final ItemStack getSpecialDrop(World world, int x, int y, int z) {

		if (world.getTileEntity(x, y, z) != null
				&& world.getTileEntity(x, y, z) instanceof IDropTile) {
			ItemStack itemStack = new ItemStack(this, 1);
			processDrop(world, x, y, z,	(IDropTile) world.getTileEntity(x, y, z), itemStack);
			return itemStack;
		} else {
			ItemStack itemStack = new ItemStack(this, 1);
			processDrop(world, x, y, z, null, itemStack);
			return itemStack;
		}

	}

	public void processDrop(World world, int x, int y, int z, IDropTile te, ItemStack drop) {
		if (te != null) {
			IDropTile handler = (IDropTile) te;
			NBTTagCompound tag = new NBTTagCompound();
			handler.writeInfo(tag);
			drop.setTagCompound(tag);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		if (orientation) {
			setDefaultDirection(world, x, y, z);
		}
	}

	/**sets the direction the block is pointing*/
	public void setDefaultDirection(World world, int x, int y, int z) {
		if (!world.isRemote) {
			Block b1 = world.getBlock(x, y, z - 1);
			Block b2 = world.getBlock(x, y, z + 1);
			Block b3 = world.getBlock(x - 1, y, z);
			Block b4 = world.getBlock(x + 1, y, z);

			byte b0 = 3;

			if ((b1.func_149730_j()) && (!b2.func_149730_j())) {
				b0 = 3;
			}

			if ((b2.func_149730_j()) && (!b1.func_149730_j())) {
				b0 = 2;
			}

			if ((b3.func_149730_j()) && (!b4.func_149730_j())) {
				b0 = 5;
			}

			if ((b4.func_149730_j()) && (!b3.func_149730_j())) {
				b0 = 4;
			}

			world.setBlockMetadataWithNotify(x, y, x, b0, 2);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entityplayer, ItemStack itemstack) {
		if (orientation) {
			int l = net.minecraft.util.MathHelper
					.floor_double(entityplayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;

			if (l == 0) {
				world.setBlockMetadataWithNotify(x, y, z, 2, 2);
			}

			if (l == 1) {
				world.setBlockMetadataWithNotify(x, y, z, 5, 2);
			}

			if (l == 2) {
				world.setBlockMetadataWithNotify(x, y, z, 3, 2);
			}

			if (l == 3) {
				world.setBlockMetadataWithNotify(x, y, z, 4, 2);
			}
		}
		if (itemstack.hasTagCompound()) {
			TileEntity entity = world.getTileEntity(x, y, z);
			if (entity != null && entity instanceof IDropTile) {
				IDropTile handler = (IDropTile) entity;
				handler.readInfo(itemstack.getTagCompound());
			}
		}
	}

	@Override
	public final boolean canWrench() {
		return wrenchable;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z,
			Block oldblock, int oldMetadata) {

		TileEntity entity = world.getTileEntity(x, y, z);

		if (entity != null && entity instanceof IInventory) {
			IInventory tileentity = (IInventory) world.getTileEntity(x, y, z);
			for (int i = 0; i < tileentity.getSizeInventory(); i++) {
				ItemStack itemstack = tileentity.getStackInSlot(i);

				if (itemstack != null) {
					float f = this.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
					float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0) {
						int j = this.rand.nextInt(21) + 10;

						if (j > itemstack.stackSize) {
							j = itemstack.stackSize;
						}

						itemstack.stackSize -= j;

						EntityItem item = new EntityItem(world, x + f, y + f1,
								z + f2, new ItemStack(itemstack.getItem(), j,
										itemstack.getItemDamage()));

						if (itemstack.hasTagCompound()) {
							item.getEntityItem().setTagCompound(
									(NBTTagCompound) itemstack.getTagCompound()
											.copy());
						}

						world.spawnEntityInWorld(item);
					}
				}
			}

			world.func_147453_f(x, y, z, oldblock);
		}
		if (SonarAPI.calculatorLoaded() && entity != null
				&& entity instanceof IUpgradeCircuits) {
			IUpgradeCircuits tileentity = (IUpgradeCircuits) entity;
			float f = this.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = this.rand.nextFloat() * 0.8F + 0.1F;
			if (tileentity.getUpgrades(0) > 0) {
				EntityItem speed = new EntityItem(world, x + f, y + f1, z + f2,
						new ItemStack(GameRegistry.findItem("Calculator",
								"SpeedUpgrade"), tileentity.getUpgrades(0)));
				world.spawnEntityInWorld(speed);
			}
			if (tileentity.getUpgrades(1) > 0) {
				EntityItem energy = new EntityItem(world, x + f, y + f1,
						z + f2, new ItemStack(GameRegistry.findItem(
								"Calculator", "EnergyUpgrade"),
								tileentity.getUpgrades(1)));
				world.spawnEntityInWorld(energy);
			}
			if (tileentity.getUpgrades(2) > 0) {
				EntityItem energy = new EntityItem(world, x + f, y + f1,
						z + f2, new ItemStack(GameRegistry.findItem(
								"Calculator", "VoidUpgrade"),
								tileentity.getUpgrades(2)));
				world.spawnEntityInWorld(energy);
			}
		}

		super.breakBlock(world, x, y, z, oldblock, oldMetadata);
		world.removeTileEntity(x, y, z);

	}

}