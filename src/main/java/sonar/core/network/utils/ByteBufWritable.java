package sonar.core.network.utils;

import io.netty.buffer.ByteBuf;

/**
 * used for adding/changing packet data to be sent
 */
public abstract class ByteBufWritable {

	public boolean replacesDefaults;

	public ByteBufWritable(boolean replacesDefaults) {
		this.replacesDefaults = replacesDefaults;
	}

	public abstract void writeToBuf(ByteBuf buf);
}
