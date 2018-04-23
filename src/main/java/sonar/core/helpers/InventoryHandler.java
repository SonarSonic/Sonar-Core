package sonar.core.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.ActionType;

/** TODO - unfinished will replace InventoryHelper */
public class InventoryHandler {

	public static Map<ItemStack, Long> addItems() {
		return null;
	}

	public static class ItemListFilter implements Predicate<ItemStack> {

		public List<ItemStack> stacks;

		public ItemListFilter(List<ItemStack> stacks) {
			this.stacks = stacks;
		}

		@Override
		public boolean test(ItemStack t) {
			return containsMatchingStack(stacks, t);
		}
	}

	public static class InventoryTransferHandler implements BiFunction<ItemStack, ActionType, Integer> {

		public long maximum;
		public long simulated = 0;
		public long performed = 0;
		public Predicate<ItemStack> canTransfer;

		public InventoryTransferHandler(long maximum, Predicate<ItemStack> canTransfer) {
			this.maximum = maximum;
			this.simulated = maximum;
			this.canTransfer = canTransfer;
		}

		public boolean canPerform() {
			return maximum != simulated;
		}

		public void startPerform() {
			this.performed = maximum - simulated;
		}

		public boolean hasCompleted() {
			return maximum != performed;
		}

		public boolean canContinueAction(ActionType type) {
			return getRemainingTransfer(type) > 0;
		}

		public long getRemainingTransfer(ActionType type) {
			return type.shouldSimulate() ? simulated : performed;
		}

		public int getValidTransfer(ItemStack stack, ActionType type) {
			return (int) Math.min(stack.getCount(), getRemainingTransfer(type));
		}

		public void transfer(long transfer, ActionType type) {
			switch (type) {
			case PERFORM:
				performed -= transfer;
				break;
			case SIMULATE:
				simulated -= transfer;
				break;
			default:
				break;

			}
		}

		@Override
		public Integer apply(ItemStack stack, ActionType type) {
			if(canTransfer.test(stack)){
				int validTransfer = getValidTransfer(stack, type);
				transfer(validTransfer, type);
				return validTransfer;
			}
			return 0;
		}
	}

	public static boolean containsMatchingStack(List<ItemStack> list, ItemStack stack) {
		for (ItemStack s : list) {
			return ItemStack.areItemsEqual(s, stack) && ItemStack.areItemStackTagsEqual(s, stack);
		}
		return false;
	}
	/*
	public static List<ItemStack> transfer(TileEntity from, EnumFacing fromDir, TileEntity to, EnumFacing toDir, long maxTransfer, BiFunction<ItemStack, ActionType, Integer> filter){		
		InventoryTransferHandler fromHandler = new InventoryTransferHandler(maxTransfer);
		List<ItemStack> extracted_s = simulateExtract(from, fromDir, fromHandler, filter);
		
		if(fromHandler.canPerform()){
			InventoryTransferHandler toHandler = new InventoryTransferHandler(maxTransfer);
			List<ItemStack> inserted_s = simulateInsert(to, toDir, fromHandler, extracted_s);			
			if(toHandler.canPerform()){
				List<ItemStack> extracted = performExtract(from, fromDir, fromHandler, new ItemListFilter(inserted_s));
				List<ItemStack> inserted = performInsert(to, toDir, toHandler, extracted);
			}
		}		
		return null;		
	}
	*/

	public static List<ItemStack> simulateInsert(TileEntity tile, EnumFacing face, List<ItemStack> insert) {
		return new ArrayList<>();
	}

	public static List<ItemStack> simulateExtract(TileEntity tile, EnumFacing face, BiFunction<ItemStack, ActionType, Integer> filter) {	
		return new ArrayList<>();
	}

	public static List<ItemStack> performInsert(TileEntity tile, EnumFacing face, List<ItemStack> insert) {
		return new ArrayList<>();
	}

	public static List<ItemStack> performExtract(TileEntity tile, EnumFacing face, BiFunction<ItemStack, ActionType, Integer> filter) {	
		return new ArrayList<>();
	}
}
