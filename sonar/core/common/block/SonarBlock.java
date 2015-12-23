package sonar.core.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import sonar.core.utils.ISyncTile;
import sonar.core.utils.IUpgradeCircuits;
import sonar.core.utils.SonarAPI;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.core.utils.helpers.SonarHelper;
import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.api.tileentity.IReconfigurableSides;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class SonarBlock extends Block implements IDismantleable {

	protected Random rand = new Random();
	public boolean orientation, wrenchable;

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
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz) {
		super.onBlockActivated(world, x, y, z, player, side, hitx, hity, hitz);
		if (player != null) {
			ItemStack heldItem = player.getHeldItem();
			if (wrenchable && heldItem != null && heldItem.getItem() instanceof IToolHammer) {
				if (!player.isSneaking()) {
					TileEntity target = world.getTileEntity(x, y, z);
					if (target instanceof IReconfigurableSides) {
						((IReconfigurableSides) target).incrSide(side);
					}
				}
				return false;
			}
			if (wrenchable && SonarAPI.calculatorLoaded() && heldItem != null && (heldItem.getItem() instanceof IToolHammer || heldItem.getItem() == GameRegistry.findItem("Calculator", "Wrench"))) {
				return false;
			} else {
				return operateBlock(world, x, y, z, player, side, hitx, hity, hitz);
			}
		}
		return false;

	}

	@Override
	public final boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		if (willHarvest) {

			return true;
		}
		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	/** @return does the block drop as normal */
	public abstract boolean dropStandard(World world, int x, int y, int z);

	/** standard onBlockActivated for use in Calculators blocks */
	public abstract boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz);

	@Override
	public final void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
		super.harvestBlock(world, player, x, y, z, meta);
		world.setBlockToAir(x, y, z);
	}

	@Override
	public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		if (dropStandard(world, x, y, z)) {
			return super.getDrops(world, x, y, z, metadata, fortune);
		}

		return Lists.newArrayList(getSpecialDrop(world, x, y, z));
	}

	public final ItemStack getSpecialDrop(World world, int x, int y, int z) {
		if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof ISyncTile) {
			ItemStack itemStack = new ItemStack(this, 1);
			processDrop(world, x, y, z, (ISyncTile) world.getTileEntity(x, y, z), itemStack);
			return itemStack;
		} else {
			ItemStack itemStack = new ItemStack(this, 1);
			processDrop(world, x, y, z, null, itemStack);
			return itemStack;
		}

	}

	public void processDrop(World world, int x, int y, int z, ISyncTile te, ItemStack drop) {
		if (te != null) {
			ISyncTile handler = (ISyncTile) te;
			NBTTagCompound tag = new NBTTagCompound();
			handler.writeData(tag, SyncType.DROP);
			drop.setTagCompound(tag);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		if (orientation)
			setDefaultDirection(world, x, y, z);
	}

	/** sets the direction the block is pointing */
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemstack) {
		if (orientation) {
			int l = net.minecraft.util.MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;

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
			if (entity != null && entity instanceof ISyncTile) {
				ISyncTile handler = (ISyncTile) entity;
				handler.readData(itemstack.getTagCompound(), SyncType.DROP);
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldblock, int oldMetadata) {

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

						EntityItem item = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

						if (itemstack.hasTagCompound()) {
							item.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
						}

						world.spawnEntityInWorld(item);
					}
				}
			}

			world.func_147453_f(x, y, z, oldblock);
		}
		if (entity != null && entity instanceof IUpgradeCircuits) {
			IUpgradeCircuits tileentity = (IUpgradeCircuits) entity;
			float f = this.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = this.rand.nextFloat() * 0.8F + 0.1F;
			if (tileentity.getUpgrades(0) > 0) {
				EntityItem speed = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(GameRegistry.findItem("Calculator", "SpeedUpgrade"), tileentity.getUpgrades(0)));
				world.spawnEntityInWorld(speed);
			}
			if (tileentity.getUpgrades(1) > 0) {
				EntityItem energy = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(GameRegistry.findItem("Calculator", "EnergyUpgrade"), tileentity.getUpgrades(1)));
				world.spawnEntityInWorld(energy);
			}
			if (tileentity.getUpgrades(2) > 0) {
				EntityItem energy = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(GameRegistry.findItem("Calculator", "VoidUpgrade"), tileentity.getUpgrades(2)));
				world.spawnEntityInWorld(energy);
			}
		}

		super.breakBlock(world, x, y, z, oldblock, oldMetadata);
		world.removeTileEntity(x, y, z);

	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {

		SonarHelper.dropTile(player, world.getBlock(x, y, z), world, x, y, z);
		return null;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {
		return true;
	}

	public boolean hasSpecialRenderer() {
		return false;
	}

	@Override
	public int getRenderType() {
		return hasSpecialRenderer() ? -1 : 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return hasSpecialRenderer() ? false : true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return hasSpecialRenderer() ? false : true;
	}

	public List<AxisAlignedBB> getCollisionBoxes(World world, int x, int y, int z, List<AxisAlignedBB> list) {
		list.add(AxisAlignedBB.getBoundingBox((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ));
		return list;
	}

	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axis, List list, Entity entity) {
		if (hasSpecialCollisionBox()) {
			List<AxisAlignedBB> collisionList = this.getCollisionBoxes(world, x, y, z, new ArrayList());
			for (AxisAlignedBB collision : collisionList) {
				collision.offset(x, y, z);
				if (collision != null && collision.intersectsWith(axis)) {
					list.add(collision);
				}
			}
		}else{
			super.addCollisionBoxesToList(world, x, y, z, axis, list, entity);
		}
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
	}

	public boolean hasSpecialCollisionBox() {
		return false;
	}

}