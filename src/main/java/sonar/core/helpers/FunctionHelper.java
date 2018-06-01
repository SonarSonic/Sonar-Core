package sonar.core.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class FunctionHelper {

    public static final Function ARRAY = MAP -> new ArrayList<>();

    public static final Function HASH_MAP = MAP -> new HashMap<>();

}