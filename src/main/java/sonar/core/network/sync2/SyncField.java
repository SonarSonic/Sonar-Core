package sonar.core.network.sync2;

import sonar.core.helpers.NBTHelper.SyncType;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target(FIELD)
public @interface SyncField {

    int saveID();

    SyncType[] value() default {SyncType.SAVE, SyncType.DEFAULT_SYNC};

    Class<? extends ISyncHandler> handler() default NullHandler.class;
}