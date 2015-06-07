package sonar.core.utils.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.IPlantable;

public class InventoryHelper {

	public static void extractItems(TileEntity pull, TileEntity push, int pullSide, int pushSide, IInventoryFilter filter) {
		if (pull instanceof IInventory && push instanceof IInventory) {
			IInventory start = (IInventory) pull;
			IInventory stop = (IInventory) push;
			int[] pullAccess = null, pushAccess = null;

			if (pull instanceof TileEntityChest) {
				Block block = pull.blockType;

				if (block instanceof BlockChest) {
					start = ((BlockChest) block).func_149951_m(pull.getWorldObj(), pull.xCoord, pull.yCoord, pull.zCoord);
				}
			}
			if (push instanceof TileEntityChest) {
				Block block = push.blockType;

				if (block instanceof BlockChest) {
					stop = ((BlockChest) block).func_149951_m(push.getWorldObj(), push.xCoord, push.yCoord, push.zCoord);
				}
			}
			if (pull instanceof ISidedInventory) {
				pullAccess = ((ISidedInventory) pull).getAccessibleSlotsFromSide(pullSide);
			}
			if (push instanceof ISidedInventory) {
				pushAccess = ((ISidedInventory) push).getAccessibleSlotsFromSide(pushSide);
			}
			extractItems(new InventoryOperation(start, pullAccess, filter), new InventoryOperation(stop, pushAccess, filter));
		}
	}

	public static ItemStack addItems(TileEntity push, ItemStack stack, int pushSide, IInventoryFilter filter) {
		if (stack == null) {
			return null;
		}
		if (filter == null || filter.matches(stack)) {
			if (push instanceof IInventory) {
				IInventory start = (IInventory) push;
				int[] pushAccess = null;

				if (push instanceof TileEntityChest) {
					Block block = push.blockType;

					if (block instanceof BlockChest) {
						start = ((BlockChest) block).func_149951_m(push.getWorldObj(), push.xCoord, push.yCoord, push.zCoord);
					}
				}
				if (push instanceof ISidedInventory) {
					pushAccess = ((ISidedInventory) push).getAccessibleSlotsFromSide(pushSide);
				}
				return exportItems(new InventoryOperation(start, pushAccess, null), stack);
			}
		}
		return stack;
	}

	private static ItemStack exportItems(InventoryOperation push, ItemStack stack) {
		ItemStack exported = stack.copy();
		int insert = canInsert(push, exported);
		if (insert != -999) {
			ItemStack pushStack;
			if (!push.hasAccess()) {
				pushStack = push.inv.getStackInSlot(insert);
			} else {
				pushStack = push.inv.getStackInSlot(push.access[insert]);
			}

			if (exported != null) {
				if (pushStack == null) {
					int max = Math.min(exported.getMaxStackSize(), push.inv.getInventoryStackLimit());

					if (max >= exported.stackSize) {
						if (!push.hasAccess()) {
							push.inv.setInventorySlotContents(insert, exported);
						} else {
							push.inv.setInventorySlotContents(push.access[insert], exported);
						}
						return null;
					} else {
						if (!push.hasAccess()) {
							push.inv.setInventorySlotContents(insert, exported.splitStack(max));
						} else {
							push.inv.setInventorySlotContents(push.access[insert], exported.splitStack(max));
						}
					}
				} else {
					int max = Math.min(pushStack.getMaxStackSize(), push.inv.getInventoryStackLimit());
					if (max > pushStack.stackSize) {
						int l = Math.min(exported.stackSize, max - pushStack.stackSize);

						exported.stackSize -= l;
						if (exported.stackSize - l <= 0) {
							exported = null;
						}
						ItemStack moveStack = pushStack.copy();
						moveStack.stackSize += l;
						if (!push.hasAccess()) {
							push.inv.setInventorySlotContents(insert, moveStack);
						} else {
							push.inv.setInventorySlotContents(push.access[insert], moveStack);

						}
						return exported;
					}
				}
			}

		}
		return exported;
	}

	private static void extractItems(InventoryOperation pull, InventoryOperation push) {
		if (pull.access == null) {
			for (int j = 0; j < pull.inv.getSizeInventory(); j++) {
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
		ItemStack target = pull.inv.getStackInSlot(slot);
		if (target != null) {
			int insert = canInsert(push, target);
			if (insert != -999) {
				if (!pull.hasFilter() || pull.filter.matches(target)) {
					return insert;
				}
			}
		}
		return -999;
	}

	private static int canInsert(InventoryOperation push, ItemStack stack) {
		if (push.access == null) {
			for (int j = 0; j < push.inv.getSizeInventory(); j++) {
				ItemStack target = push.inv.getStackInSlot(j);
				if (target != null && target.stackSize != target.getMaxStackSize() && target.stackSize != push.inv.getInventoryStackLimit() && target.getItem() == stack.getItem() && target.getItemDamage() == stack.getItemDamage() && target.areItemStackTagsEqual(target, stack)) {
					return j;
				}
			}
			for (int j = 0; j < push.inv.getSizeInventory(); j++) {
				if (push.inv.getStackInSlot(j) == null) {
					return j;
				}
			}
		} else {
			for (int j = 0; j < push.access.length; j++) {
				ItemStack target = push.inv.getStackInSlot(push.access[j]);
				if (target != null && target.stackSize != target.getMaxStackSize() && target.stackSize != push.inv.getInventoryStackLimit() && target.getItem() == stack.getItem() && target.getItemDamage() == stack.getItemDamage() && target.areItemStackTagsEqual(target, stack)) {
					return j;
				}
			}
			for (int j = 0; j < push.access.length; j++) {
				if (push.inv.getStackInSlot(push.access[j]) == null) {
					return j;
				}
			}
		}
		return -999;

	}

	private static void performExtract(InventoryOperation pull, InventoryOperation push, int pullID, int pushID) {
		ItemStack pullStack = pull.inv.getStackInSlot(pullID);
		ItemStack pushStack;
		if (!push.hasAccess()) {
			pushStack = push.inv.getStackInSlot(pushID);
		} else {
			pushStack = push.inv.getStackInSlot(push.access[pushID]);
		}

		if (pullStack != null) {
			if (pushStack == null) {
				int max = Math.min(pullStack.getMaxStackSize(), push.inv.getInventoryStackLimit());

				if (max >= pullStack.stackSize) {
					if (!push.hasAccess()) {
						push.inv.setInventorySlotContents(pushID, pullStack);
					} else {
						push.inv.setInventorySlotContents(push.access[pushID], pullStack);
					}
					pull.inv.setInventorySlotContents(pullID, null);
				} else {
					if (!push.hasAccess()) {
						push.inv.setInventorySlotContents(pushID, pullStack.splitStack(max));
					} else {
						push.inv.setInventorySlotContents(push.access[pushID], pullStack.splitStack(max));
					}
				}
			} else {
				int max = Math.min(pushStack.getMaxStackSize(), push.inv.getInventoryStackLimit());
				if (max > pushStack.stackSize) {
					int l = Math.min(pullStack.stackSize, max - pushStack.stackSize);

					pull.inv.decrStackSize(pullID, l);

					if (pullStack.stackSize - l <= 0) {
						pull.inv.setInventorySlotContents(pushID, null);
					}
					ItemStack moveStack = pushStack.copy();
					moveStack.stackSize += l;
					if (!push.hasAccess()) {
						push.inv.setInventorySlotContents(pushID, moveStack);
					} else {
						push.inv.setInventorySlotContents(push.access[pushID], moveStack);

					}
				}
			}
		}
	}

	private static class InventoryOperation {
		public IInventory inv;
		public int[] access;
		public IInventoryFilter filter;

		public InventoryOperation(IInventory inv, int[] access, IInventoryFilter filter) {
			this.inv = inv;
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
	}

	public static interface IInventoryFilter {

		public boolean matches(ItemStack stack);
	}
}
