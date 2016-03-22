package sonar.core.common.block.properties;

import java.util.Collection;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import sonar.core.utils.MachineSide;

public class PropertyGenericEnum<T extends Enum<T> & IStringSerializable> extends PropertyEnum<T> {

	protected PropertyGenericEnum(String name, Class<T> valueClass, Collection<T> allowedValues) {
		super(name, valueClass, allowedValues);
	}

}
