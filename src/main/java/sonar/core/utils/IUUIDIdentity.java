package sonar.core.utils;

import java.util.UUID;

public interface IUUIDIdentity {

    UUID INVALID_UUID = new UUID(0, 0);

    UUID getIdentity();
}
