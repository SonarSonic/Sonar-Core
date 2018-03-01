package sonar.core.common.block.properties;

public interface IMetaRenderer extends IItemRenderer {

    IMetaVariant getVariant(int metadata);
}
