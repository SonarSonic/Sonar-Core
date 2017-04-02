package sonar.core.network.sync2;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Target;

import sonar.core.helpers.NBTHelper.SyncType;

@Target(FIELD)
public @interface SyncField {

	public int saveID();
	
	public SyncType[] value() default { SyncType.SAVE, SyncType.DEFAULT_SYNC };

	public Class<? extends ISyncHandler> handler() default NullHandler.class;
}
