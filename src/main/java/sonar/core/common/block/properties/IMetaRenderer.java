package sonar.core.common.block.properties;

public interface IMetaRenderer<T extends IMetaVariant> extends IItemRenderer<T> {

	public T getVariant(int metadata);	
}
