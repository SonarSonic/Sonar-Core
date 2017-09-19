package sonar.core.registries;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;

public class SonarRegistryBlock implements ISonarRegistryBlock {

	public Block block;
	public String name;
	public Class<? extends TileEntity> tile;
    public boolean ignoreNormalTab;
	
	public SonarRegistryBlock(Block block, String name, Class<? extends TileEntity> tile) {
		this.block = block;
		this.name = name;
		this.tile = tile;
	}
	
	public SonarRegistryBlock(Block block, String name) {
		this.block = block;
		this.name = name;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public SonarRegistryBlock setBlock(Block block) {
		this.block = block;
		return this;
	}

	@Override
	public String getRegistryName() {
		return name;
	}

	@Override
	public SonarRegistryBlock setRegistryName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public SonarRegistryBlock setCreativeTab(CreativeTabs tab) {
		block.setCreativeTab(tab);
		ignoreNormalTab=true;
		return this;
	}
	
	public SonarRegistryBlock removeCreativeTab(){
		block.setCreativeTab(null);
		ignoreNormalTab=true;
		return this;
	}

	@Override
	public boolean hasTileEntity() {
		return tile!=null;
	}

	@Override
	public Class<? extends TileEntity> getTileEntity() {
		return tile;
	}

	@Override
	public SonarRegistryBlock addTileEntity(Class<? extends TileEntity> tile) {
		this.tile=tile;
		return this;
	}
	
	public SonarRegistryBlock setProperties(){
		block.setHardness(1.0F).setResistance(20.0F);
		return this;		
	}
	
	public SonarRegistryBlock setProperties(float hardness, float resistance){
		block.setHardness(hardness).setResistance(resistance);
		return this;		
	}
}
