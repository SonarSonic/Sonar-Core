package sonar.core.network.sync;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraftforge.common.util.Constants;

public enum ObjectType {
	BOOLEAN(Constants.NBT.TAG_END, Boolean.class), BYTE(Constants.NBT.TAG_BYTE, Byte.class), SHORT(Constants.NBT.TAG_SHORT, Short.class), INTEGER(Constants.NBT.TAG_INT, Integer.class), LONG(Constants.NBT.TAG_LONG, Long.class), FLOAT(Constants.NBT.TAG_FLOAT, Float.class), DOUBLE(Constants.NBT.TAG_DOUBLE, Double.class), STRING(Constants.NBT.TAG_STRING, String.class), NONE(-1, null);

	public static final ArrayList<ObjectType> numbers = Lists.newArrayList(BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE);
	public int tagType;
	public Class<?> classType;

	ObjectType(int tagType, Class<?> classType) {
		this.tagType = tagType;
		this.classType = classType;
	}

	public boolean isNumber() {
		return numbers.contains(this);
	}

	public static ObjectType getInfoType(Object obj) {
		for (ObjectType type : values()) {
			if (type.classType != null && (type.classType.isInstance(obj) || type.classType.isAssignableFrom(obj.getClass()))) {
				return type;
			}
		}
		return NONE;
	}
}