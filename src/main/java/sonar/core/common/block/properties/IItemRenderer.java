package sonar.core.common.block.properties;

public interface IItemRenderer<T extends IItemVariant>  {
	
	public T[] getVariants();	
}
