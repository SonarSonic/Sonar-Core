package sonar.core.common.block.properties;

import java.util.Collection;

import net.minecraft.block.properties.PropertyEnum;
import sonar.core.utils.MachineSide;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class PropertyMachineSide extends PropertyEnum<MachineSide> {
	protected PropertyMachineSide(String name, Collection<MachineSide> values) {
		super(name, MachineSide.class, values);
	}

	public static PropertyMachineSide create(String name) {
		return create(name, Predicates.<MachineSide> alwaysTrue());
	}

	public static PropertyMachineSide create(String name, Predicate<MachineSide> filter) {
		return create(name, Collections2.<MachineSide> filter(Lists.newArrayList(MachineSide.values()), filter));
	}

	public static PropertyMachineSide create(String name, Collection<MachineSide> values) {
		return new PropertyMachineSide(name, values);
	}
}