package sonar.core.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.integration.SonarAPI;
import sonar.core.inventory.IAdditionalInventory;
import sonar.core.inventory.IDropInventory;
import sonar.core.network.PacketBlockInteraction;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.core.utils.IInteractBlock;
import sonar.core.utils.IUpgradeCircuits;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.core.utils.helpers.SonarHelper;
import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.api.tileentity.IReconfigurableSides;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class SonarBlock extends Block implements IDismantleable, IInteractBlock {

	protected Random rand = new Random();
	public boolean orientation = true, wrenchable = true;

	protected SonarBlock(Material material) {
		super(material);
	}

	public void disableOrientation() {
		this.orientation = false;
	}

	public void disableWrenchable() {
		this.wrenchable = false;
	}

	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz) {
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
				return operateBlock(world, x, y, z, player, new BlockInteraction(side, hitx, hity, hitz, player.isSneaking() ? BlockInteractionType.SHIFT_RIGHT : BlockInteractionType.RIGHT));
			}
		}
		return super.onBlockActivated(world, x, y, z, player, side, hitx, hity, hitz);

	}

	public boolean isClickableSide(World world, int x, int y, int z, int side) {
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (world.isRemote && allowLeftClick()) {
			MovingObjectPosition posn = Minecraft.getMinecraft().objectMouseOver;
			if (isClickableSide(world, x, y, z, posn.sideHit)) {
				onBlockClicked(world, x, y, z, player);
				return false;
			}
		}
		return super.removedByPlayer(world, player, x, y, z);
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
	public abstract boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, BlockInteraction interact);

	@Override
	public boolean allowLeftClick() {
		return false;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if (world.isRemote && allowLeftClick()) {
			MovingObjectPosition pos = Minecraft.getMinecraft().objectMouseOver;
			float hitX = (float) (pos.hitVec.xCoord - pos.blockX);
			float hitY = (float) (pos.hitVec.yCoord - pos.blockY);
			float hitZ = (float) (pos.hitVec.zCoord - pos.blockZ);
			SonarCore.network.sendToServer(new PacketBlockInteraction(x, y, z, new BlockInteraction(pos.sideHit, hitX, hitY, hitZ, player.isSneaking() ? BlockInteractionType.SHIFT_LEFT : BlockInteractionType.LEFT)));
		}
	}

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
			tag.setBoolean("dropped", true);
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
			int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;

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
		List<ItemStack> drops = new ArrayList();
		TileEntity entity = world.getTileEntity(x, y, z);
		if (entity != null) {
			if (entity instanceof IDropInventory) {
				IDropInventory inv = (IDropInventory) entity;
				if (inv.canDrop() && inv.dropSlots() != null) {
					int[] slots = inv.dropSlots();
					for (int i = 0; i < slots.length; i++) {
						ItemStack itemstack = inv.getStackInSlot(slots[i]);
						if (itemstack != null) {
							drops.add(itemstack);
						}
					}
				}
			} else if (entity instanceof IInventory) {
				IInventory inv = (IInventory) entity;
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack itemstack = inv.getStackInSlot(i);
					if (itemstack != null) {
						drops.add(itemstack);
					}
				}
			}
			if (entity instanceof IAdditionalInventory) {
				IAdditionalInventory additionalInv = (IAdditionalInventory) entity;
				ItemStack[] additionalStacks = additionalInv.getAdditionalStacks();
				if (additionalStacks != null) {
					for (ItemStack stack : additionalStacks) {
						if (stack != null) {
							drops.add(stack);
						}
					}
				}
			}
		}

		for (ItemStack stack : drops) {
			if (stack != null) {
				float f = this.rand.nextFloat() * 0.8F + 0.1F;
				float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
				float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

				EntityItem dropStack = new EntityItem(world, x + f, y + f1, z + f2, stack);
				world.spawnEntityInWorld(dropStack);
			}
		}
		super.breakBlock(world, x, y, z, oldblock, oldMetadata);

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
		} else {
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