package sonar.core;

public class ItemRenderRegister {
	public static void register() {
		/*
		for (Item item : SonarItems.registeredItems) {
			if (item.getHasSubtypes()) {
				List<ItemStack> stacks = new ArrayList<>();
				item.getSubItems(item, SonarCore.tab, stacks);
				for (ItemStack stack : stacks) {
					String variant = "variant=meta" + stack.getItemDamage();
					if (item instanceof IMetaRenderer) {
						IMetaRenderer meta = (IMetaRenderer) item;
						variant = "variant=" + meta.getVariant(stack.getItemDamage()).getName();
					}
					ModelLoader.setCustomModelResourceLocation(item, stack.getItemDamage(), new ModelResourceLocation(SonarCore.modid + ":" + "items/" + item.getUnlocalizedName().substring(5), variant));
				}
			} else {
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(SonarCore.modid, item.getUnlocalizedName().substring(5)), "inventory"));
			}
		}
		*/
	}
}
