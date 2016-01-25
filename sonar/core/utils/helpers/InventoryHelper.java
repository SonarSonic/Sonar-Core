package sonar.core.utils.helpers;

import sonar.core.inventory.IFilteredInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.common.util.ForgeDirection;

public class InventoryHelper {

	public static boolean isPlayerInventoryFull(EntityPlayer player) {
		return player.inventory.getFirstEmptyStack() == -1;
	}

	public static void extractItems(TileEntity pull, TileEntity push, int pullSide, int pushSide, IInventoryFilter filter) {
		if (pull == null || push == null) {
			return;
		}
		if (pull instanceof IInventory && push instanceof IInventory) {
			// IInventory start = (IInventory) pull;
			// IInventory stop = (IInventory) push;
			int[] pullAccess = null, pushAccess = null;

			if (pull instanceof ISidedInventory) {
				pullAccess = ((ISidedInventory) pull).getAccessibleSlotsFromSide(pullSide);
			}
			if (push instanceof ISidedInventory) {
				pushAccess = ((ISidedInventory) push).getAccessibleSlotsFromSide(pushSide);
			}

			extractItems(new InventoryOperation(pull, pullSide, pullAccess, filter), new InventoryOperation(push, pushSide, pushAccess, filter));
		}
	}

	public static ItemStack addItems(TileEntity push, ItemStack stack, int pushSide, IInventoryFilter filter) {
		if (stack == null || push == null) {
			return stack;
		}
		if (filter == null || filter.matches(stack)) {
			if (push instanceof IInventory) {
				int[] pushAccess = null;
				if (push instanceof ISidedInventory) {
					pushAccess = ((ISidedInventory) push).getAccessibleSlotsFromSide(pushSide);
				}
				return exportItems(new InventoryOperation(push, pushSide, pushAccess, null), stack);

			}
		}
		return stack;
	}

	private static ItemStack exportItems(InventoryOperation push, ItemStack stack) {
		ItemStack exported = stack.copy();
		while (exported != null && canInsert(push, exported) != -999) {
			int insert = canInsert(push, exported);
			ItemStack pushStack = push.getInv().getStackInSlot(insert);

			if (exported != null) {
				if (pushStack == null) {
					int max = Math.min(exported.getMaxStackSize(), push.getInv().getInventoryStackLimit());

					if (max >= exported.stackSize) {
						push.getInv().setInventorySlotContents(insert, exported);
						exported = null;
					} else {
						// push.getInv().setInventorySlotContents(insert,
						// exported.splitStack(max));
						// pull action????
					}
				} else if (stack.getItem() == pushStack.getItem()) {
					int max = Math.min(pushStack.getMaxStackSize(), push.getInv().getInventoryStackLimit());
					if (max > pushStack.stackSize) {
						int l = Math.min(exported.stackSize, max - pushStack.stackSize);

						exported.stackSize -= l;
						if (exported.stackSize <= 0) {
							exported = null;
						}
						ItemStack moveStack = pushStack.copy();
						moveStack.stackSize += l;
						push.getInv().setInventorySlotContents(insert, moveStack);

					}
				}
			}

		}
		return exported;
	}

	private static void extractItems(InventoryOperation pull, InventoryOperation push) {
		if (pull.access == null) {
			for (int j = 0; j < pull.getInv().getSizeInventory(); j++) {
				int extract = canExtract(pull, push, j);
				if (extract != -999) {
					performExtract(pull, push, j, extract);
				}
			}
		} else {
			for (int j = 0; j < pull.access.length; j++) {
				int extract = canExtract(pull, push, pull.access[j]);
				if (extract != -999) {
					performExtract(pull, push, pull.access[j], extract);
				}
			}
		}
	}

	private static int canExtract(InventoryOperation pull, InventoryOperation push, int slot) {
		ItemStack target = pull.getInv().getStackInSlot(slot);
		boolean check = false;
		if (pull.inv instanceof TileEntityHopper) {
			if (BlockHopper.func_149917_c(pull.inv.getBlockMetadata())) {
				return -999;
			}
		}

		if (pull.getInv() instanceof IFilteredInventory) {
			IFilteredInventory inv = (IFilteredInventory) pull.getInv();
			if (!inv.canPullItem(target, pull.side)) {
				return -999;
			} else {
				check = true;
			}
		}
		if (!pull.hasAccess() || pull.hasAccess() && (check || target!=null && pull.getSidedInv().canExtractItem(slot, target, pull.side))) {

			if (target != null) {
				int insert = canInsert(push, target);

				if (insert != -999) {
					if (!pull.hasFilter() || pull.filter.matches(target)) {
						return insert;
					}
				}

			}

		}

		return -999;
	}

	private static int canInsert(InventoryOperation push, ItemStack stack) {
		boolean check = false;
		if (push.getInv() instanceof IFilteredInventory) {
			IFilteredInventory inv = (IFilteredInventory) push.getInv();
			if (!inv.canPushItem(stack, push.side)) {
				return -999;
			} else {
				check = true;
			}
		}
		int emptySlot = -999;
		if (push.access == null) {
			for (int j = 0; j < push.getInv().getSizeInventory(); j++) {
				ItemStack target = push.getInv().getStackInSlot(j);
				if (target != null && target.stackSize < target.getMaxStackSize() && target.stackSize < push.getInv().getInventoryStackLimit() && target.getItem() == stack.getItem() && target.getItemDamage() == stack.getItemDamage() && target.areItemStackTagsEqual(target, stack)) {
					return j;
				} else if (emptySlot == -999 && target == null) {
					emptySlot = j;
				}
			}
			return emptySlot;
		} else {
			ISidedInventory inv = push.getSidedInv();
			for (int j = 0; j < push.access.length; j++) {
				System.out.print(push.side);
				
				if (check || inv.canInsertItem(push.access[j], stack, push.side)) {
					ItemStack target = push.getInv().getStackInSlot(push.access[j]);
					if (target != null && target.stackSize != target.getMaxStackSize() && target.stackSize != push.getInv().getInventoryStackLimit() && target.getItem() == stack.getItem() && target.getItemDamage() == stack.getItemDamage() && target.areItemStackTagsEqual(target, stack)) {
						return push.access[j];
					} else if (emptySlot == -999 && target == null) {
						emptySlot = push.access[j];
					}
				}

			}
		}

		return emptySlot;

	}

	private static void performExtract(InventoryOperation pull, InventoryOperation push, int pullID, int pushID) {
		ItemStack pullStack = pull.getInv().getStackInSlot(pullID);
		ItemStack pushStack = push.getInv().getStackInSlot(pushID);

		if (pullStack != null && (!push.hasFilter() || push.filter.matches(pullStack))) {
			if (pushStack == null) {
				int max = Math.min(pullStack.getMaxStackSize(), push.getInv().getInventoryStackLimit());

				if (max >= pullStack.stackSize) {
					push.getInv().setInventorySlotContents(pushID, pullStack);
					pull.getInv().setInventorySlotContents(pullID, null);
				} else {
					// push.getInv().setInventorySlotContents(pushID,
					// pullStack.splitStack(max));
					// pull change?
				}
			} else if (pullStack.getItem() == pushStack.getItem()) {
				int max = Math.min(pushStack.getMaxStackSize(), push.getInv().getInventoryStackLimit());
				if (max > push.getInv().getStackInSlot(pushID).stackSize) {
					int l = Math.min(pull.getInv().getStackInSlot(pullID).stackSize, max - push.getInv().getStackInSlot(pushID).stackSize);

					pull.getInv().decrStackSize(pullID, l);
					ItemStack moveStack = push.getInv().getStackInSlot(pushID).copy();
					if (push.getInv().getStackInSlot(pushID).stackSize - l <= 0) {
						pull.getInv().setInventorySlotContents(pullID, null);
					}
					moveStack.stackSize += l;
					push.getInv().setInventorySlotContents(pushID, moveStack);
				}
			}
		}
	}

	private static class InventoryOperation {
		public TileEntity inv;
		public int side;
		private int[] access;
		public IInventoryFilter filter;

		public InventoryOperation(TileEntity inv, int side, int[] access, IInventoryFilter filter) {
			this.inv = inv;
			this.side = side;
			this.access = access;
			this.filter = filter;
		}

		public boolean hasFilter() {
			if (filter == null) {
				return false;
			}
			return true;
		}

		public boolean hasAccess() {
			if (access == null) {
				return false;
			}
			return true;
		}

		public IInventory getInv() {
			if (inv instanceof TileEntityChest) {
				Block block = inv.blockType;

				if (block instanceof BlockChest) {
					return ((BlockChest) block).func_149951_m(inv.getWorldObj(), inv.xCoord, inv.yCoord, inv.zCoord);
				}
			}
			return (IInventory) inv;
		}

		public ISidedInventory getSidedInv() {
			return (ISidedInventory) inv;
		}
	}

	public static interface IInventoryFilter {

		public boolean matches(ItemStack stack);
	}
}
