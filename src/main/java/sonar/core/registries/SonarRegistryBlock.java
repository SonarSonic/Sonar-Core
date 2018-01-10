package sonar.core.registries;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import sonar.core.common.block.SonarBlockTip;

public class SonarRegistryBlock<T extends Block> extends AbstractSonarRegistry<T> implements ISonarRegistryBlock<T> {

	public Class<? extends TileEntity> tile;
	public boolean ignoreNormalTab;

	public SonarRegistryBlock(T block, String name, Class<? extends TileEntity> tile) {
		super(block, name);
		this.tile = tile;
	}

	public SonarRegistryBlock(T block, String name) {
		super(block, name);
	}

	@Override
	public Item getItemBlock() {
		return new SonarBlockTip(value);
	}

	@Override
	public SonarRegistryBlock setCreativeTab(CreativeTabs tab) {
		value.setCreativeTab(tab);
		ignoreNormalTab = true;
		return this;
	}

	public SonarRegistryBlock removeCreativeTab() {
		value.setCreativeTab(null);
		ignoreNormalTab = true;
		return this;
	}

	@Override
	public boolean hasTileEntity() {
		return tile != null;
	}

	@Override
	public Class<? extends TileEntity> getTileEntity() {
		return tile;
	}

	@Override
	public SonarRegistryBlock addTileEntity(Class<? extends TileEntity> tile) {
		this.tile = tile;
		return this;
	}

	public SonarRegistryBlock setProperties() {
		value.setHardness(1.0F).setResistance(20.0F);
		return this;
	}

	public SonarRegistryBlock setProperties(float hardness, float resistance) {
		value.setHardness(hardness).setResistance(resistance);
		return this;
	}

	@Override
	public boolean shouldRegisterRenderer() {
		return shouldRegisterRenderer;
	}
}
