package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.inventory.slots.SlotLarge;

public abstract class ContainerLargeInventory extends ContainerSync {

	ILargeInventory entity;

	public ContainerLargeInventory(TileEntitySonar tile) {
		super(tile);
		entity = (ILargeInventory) tile;
	}

	/** a rewrite of the mergeItemStack which accommodates for a
	 * {@link ILargeInventory} */
	protected boolean mergeSpecial(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		boolean flag = false;
		int i = startIndex;

		if (reverseDirection) {
			i = endIndex - 1;
		}

		if (stack.isStackable()) {
			while (stack.getCount() > 0 && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)) {
				Slot slot = (Slot) this.inventorySlots.get(i);
				ItemStack itemstack = slot.getStack();
				StoredItemStack stored = entity.getTileInv().getLargeStack(i);

				if (itemstack != null && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
					int j = itemstack.getCount() + stack.getCount();
					int maxSize = /* slot instanceof SlotLarge ?
									 * entity.getTileInv().max : */stack.getMaxStackSize();
					if (j <= maxSize) {
						stack.setCount(0);
						if (slot instanceof SlotLarge) {
							entity.getTileInv().setLargeStack(i, stored.setStackSize(j));
						} else {
							itemstack.setCount(j);
						}
						slot.onSlotChanged();
						flag = true;
					} else if (itemstack.getCount() < maxSize) {
						stack.shrink(- maxSize - itemstack.getCount());
						if (slot instanceof SlotLarge) {
							stored.add(new StoredItemStack(itemstack.copy()).setStackSize(maxSize));
							entity.getTileInv().setLargeStack(i, stored);
						} else {
							itemstack.setCount(maxSize);
						}
						slot.onSlotChanged();
						flag = true;
					}
				}

				if (reverseDirection) {
					--i;
				} else {
					++i;
				}
			}
		}

		if (stack.getCount() > 0) {
			if (reverseDirection) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex) {
				Slot slot1 = (Slot) this.inventorySlots.get(i);
				ItemStack itemstack1 = slot1.getStack();
				if (slot1.isItemValid(stack)) {
					if (slot1 instanceof SlotLarge) {
						StoredItemStack target = entity.getTileInv().getLargeStack(i);
						if (target != null) {
							target = target.copy();
							itemstack1 = target.getFullStack();
						} else {
							target = new StoredItemStack(stack).setStackSize(0);
							itemstack1 = null;
						}
						int max = target.getItemStack().getMaxStackSize() * entity.getTileInv().numStacks;
						if (target.stored < max) {
							int toAdd = (int) Math.min(max - target.stored, stack.getCount());
							target.add(new StoredItemStack(stack.copy()).setStackSize(toAdd));
							entity.getTileInv().setLargeStack(i, target);
							stack.shrink(toAdd);
							if (stack.getCount() == 0) {
								flag = true;
								break;
							}
						}

					} else if (itemstack1 == null) {
						slot1.putStack(stack.copy());
						slot1.onSlotChanged();
						stack.setCount(0);
						flag = true;
						break;
					}
				}
				if (reverseDirection) {
					--i;
				} else {
					++i;
				}
			}
		}

		return flag;
	}

	/** special implementation which accommodates for a
	 * {@link ILargeInventory} */
	public ItemStack slotClick(int slotID, int dragType, ClickType button, EntityPlayer player) {
		// public ItemStack slotClick(int slotID, int dragType, int button,
		// EntityPlayer player) {
		if (!(slotID < entity.getTileInv().size) || button == ClickType.QUICK_MOVE) {
			return super.slotClick(slotID, dragType, button, player);
		}
		if (slotID >= 0) {
			StoredItemStack clicked = entity.getTileInv().getLargeStack(slotID);
			if ((dragType == 0 || dragType == 1) && button == ClickType.PICKUP) {
				ItemStack held = player.inventory.getItemStack();
				if (held == null && clicked != null && clicked.getItemStack() != null) {
					int toRemove = (int) Math.min(clicked.getItemStack().getMaxStackSize(), clicked.stored);
					if (dragType == 1 && toRemove != 1) {
						toRemove = (int) Math.ceil(toRemove / 2);
					}
					if (toRemove != 0) {
						StoredItemStack newStack = clicked.copy();
						ItemStack stack = newStack.copy().setStackSize(toRemove).getFullStack();
						newStack.remove(stack);
						if (newStack.stored == 0) {
							entity.getTileInv().setLargeStack(slotID, null);
						}
						player.inventory.setItemStack(stack);
						entity.getTileInv().setLargeStack(slotID, newStack);
						return null;
					}
				} else if (held != null) {
					if (clicked == null || clicked.getItemStack() == null || clicked.getStackSize() == 0) {
						if (entity.getTileInv().isItemValidForSlot(slotID * entity.getTileInv().numStacks, held)) {
							entity.getTileInv().setLargeStack(slotID, new StoredItemStack(held));
							player.inventory.setItemStack(null);
							return null;
						}
					} else if (clicked != null && clicked.getItemStack() != null) {
						if (clicked.equalStack(held)) {
							int maxAdd = (int) Math.min((held.getMaxStackSize() * entity.getTileInv().numStacks) - clicked.getStackSize(), held.getCount());
							if (maxAdd > 0) {
								StoredItemStack newStack = clicked.copy();
								newStack.add(new StoredItemStack(held).setStackSize(maxAdd));
								held.shrink(maxAdd);
								if (held.getCount() == 0) {
									player.inventory.setItemStack(null);
								}
								entity.getTileInv().setLargeStack(slotID, newStack);
								return null;
							}
						}
					}
				}
			}
		}
		return null;
	}

}
