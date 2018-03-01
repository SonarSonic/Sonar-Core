package sonar.core.api;

import io.netty.buffer.ByteBuf;

public interface IFlexibleContainer<T> {

    void refreshState();

    T getCurrentState();

    interface Syncable extends IFlexibleContainer {

        void readState(ByteBuf buf);

        void writeState(ByteBuf buf);
	}
}
