package sonar.core.registries;

public interface IAbstractSonarRegistry<T> {

    /**
     * gets the item to register
     */
    T getValue();

    /**
     * @param item the item you wish to register
     * @return this;
     */
    IAbstractSonarRegistry<T> setValue(T item);

    /**
     * @return gets the name to register the item under
     */
    String getRegistryName();

    /**
     * sets the name to register this item under
     */
    IAbstractSonarRegistry<T> setRegistryName(String name);

    void setShouldRegisterRenderer(boolean bool);
    
    boolean shouldRegisterRenderer();
}
