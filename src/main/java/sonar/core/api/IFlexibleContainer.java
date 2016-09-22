package sonar.core.api;

import io.netty.buffer.ByteBuf;

public interface IFlexibleContainer<T> {

	public void refreshState();

	public T getCurrentState();

	public interface Syncable extends IFlexibleContainer {

		public void readState(ByteBuf buf);

		public void writeState(ByteBuf buf);
	}
}
