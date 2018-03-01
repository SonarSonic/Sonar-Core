package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.inventory.slots.SlotLarge;
import sonar.core.utils.SonarCompat;

public abstract class ContainerLargeInventory extends ContainerSync {

	ILargeInventory entity;

	public ContainerLargeInventory(TileEntitySonar tile) {
		super(tile);
		entity = (ILargeInventory) tile;
	}

    /**
     * a rewrite of the mergeItemStack which accommodates for a
     * {@link ILargeInventory}
     */
	protected boolean mergeSpecial(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		boolean flag = false;
		int i = startIndex;

		if (reverseDirection) {
			i = endIndex - 1;
		}

		if (stack.isStackable()) {
			while (SonarCompat.getCount(stack) > 0 && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)) {
                Slot slot = this.inventorySlots.get(i);
				ItemStack itemstack = slot.getStack();
				StoredItemStack stored = entity.getTileInv().getLargeStack(i);

				if (!SonarCompat.isEmpty(itemstack) && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
					int j = SonarCompat.getCount(itemstack) + SonarCompat.getCount(stack);
					int maxSize = /* slot instanceof SlotLarge ?
									 * entity.getTileInv().max : */stack.getMaxStackSize();
					if (j <= maxSize) {
						stack = SonarCompat.setCount(stack, 0);
						if (slot instanceof SlotLarge) {
							entity.getTileInv().setLargeStack(i, stored.setStackSize(j));
						} else {
							itemstack = SonarCompat.setCount(itemstack, j);
						}
						slot.onSlotChanged();
						flag = true;
					} else if (SonarCompat.getCount(itemstack) < maxSize) {
						stack = SonarCompat.shrink(stack, - maxSize - SonarCompat.getCount(itemstack));
						if (slot instanceof SlotLarge) {
							stored.add(new StoredItemStack(itemstack.copy()).setStackSize(maxSize));
							entity.getTileInv().setLargeStack(i, stored);
						} else {
							itemstack = SonarCompat.setCount(itemstack, maxSize);
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

		if (SonarCompat.getCount(stack) > 0) {
			if (reverseDirection) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex) {
                Slot slot1 = this.inventorySlots.get(i);
				ItemStack itemstack1 = slot1.getStack();
				if (slot1.isItemValid(stack)) {
					if (slot1 instanceof SlotLarge) {
						StoredItemStack target = entity.getTileInv().getLargeStack(i);
						if (target != null) {
							target = target.copy();
							itemstack1 = target.getFullStack();
						} else {
							target = new StoredItemStack(stack).setStackSize(0);
							itemstack1 = SonarCompat.getEmpty();
						}
						int max = target.getItemStack().getMaxStackSize() * entity.getTileInv().numStacks;
						if (target.stored < max) {
							int toAdd = (int) Math.min(max - target.stored, SonarCompat.getCount(stack));
							target.add(new StoredItemStack(stack.copy()).setStackSize(toAdd));
							entity.getTileInv().setLargeStack(i, target);
							stack = SonarCompat.shrink(stack, toAdd);
							if (SonarCompat.getCount(stack) == 0) {
								flag = true;
								break;
							}
						}
					} else if (SonarCompat.isEmpty(itemstack1)) {
						slot1.putStack(stack.copy());
						slot1.onSlotChanged();
						stack = SonarCompat.setCount(stack, 0);
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

    /**
     * special implementation which accommodates for a
     * {@link ILargeInventory}
     */
    @Override
	public ItemStack slotClick(int slotID, int dragType, ClickType button, EntityPlayer player) {
		if (!(slotID < entity.getTileInv().size) || button == ClickType.QUICK_MOVE) {
			return super.slotClick(slotID, dragType, button, player);
		}
		if (slotID >= 0) {
			StoredItemStack clicked = entity.getTileInv().getLargeStack(slotID);
			if ((dragType == 0 || dragType == 1) && button == ClickType.PICKUP) {
				ItemStack held = player.inventory.getItemStack();
				if (SonarCompat.isEmpty(held) && clicked != null && clicked.getItemStack() != null) {
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
						return SonarCompat.getEmpty();
					}
				} else if (!SonarCompat.isEmpty(held)) {
					if (clicked == null || SonarCompat.isEmpty(clicked.getItemStack()) || clicked.getStackSize() == 0) {
                        if (entity.getTileInv().isItemValidForSlot(slotID * entity.getTileInv().numStacks, held)) {
							entity.getTileInv().setLargeStack(slotID, new StoredItemStack(held));
							player.inventory.setItemStack(SonarCompat.getEmpty());
							return SonarCompat.getEmpty();
						}
					} else if (clicked != null && !SonarCompat.isEmpty(clicked.getItemStack())) {
						if (clicked.equalStack(held)) {
                            int maxAdd = (int) Math.min(held.getMaxStackSize() * entity.getTileInv().numStacks - clicked.getStackSize(), SonarCompat.getCount(held));
							if (maxAdd > 0) {
								StoredItemStack newStack = clicked.copy();
								newStack.add(new StoredItemStack(held).setStackSize(maxAdd));
								held = SonarCompat.shrink(held, maxAdd);
								entity.getTileInv().setLargeStack(slotID, newStack);
								return SonarCompat.getEmpty();
							}
						}
					}
				}
			}
		}
		return SonarCompat.getEmpty();
	}
}
