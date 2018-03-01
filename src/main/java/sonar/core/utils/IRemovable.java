package sonar.core.utils;

/**
 * used for multiparts when a multipart is removed yet still remains
 */
public interface IRemovable {

    boolean wasRemoved();
}
