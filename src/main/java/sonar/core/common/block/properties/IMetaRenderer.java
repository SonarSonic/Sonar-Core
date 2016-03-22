package sonar.core.common.block.properties;

public interface IMetaRenderer<T extends IMetaVariant>  {

	public T getVariant(int metadata);	
	
	public T[] getVariants();	
}
