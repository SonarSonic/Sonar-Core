package sonar.core.helpers;

import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FunctionHelper {

	public static final Function ARRAY = MAP -> Lists.newArrayList();
	
	public static final Function HASH_MAP = MAP -> Maps.newHashMap();

}
