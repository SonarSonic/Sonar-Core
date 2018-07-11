package sonar.core.sync;

import javax.annotation.Nullable;

public interface ISyncHandlerFactory {

    @Nullable
    ISyncHandler createHandler(Class clazz);
}
