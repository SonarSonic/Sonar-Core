package sonar.core.utils;

import java.util.UUID;

public interface IUUIDIdentity {

	public static final UUID INVALID_UUID = new UUID(0, 0);

	public UUID getIdentity();

}
