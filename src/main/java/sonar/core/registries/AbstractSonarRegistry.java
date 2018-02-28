package sonar.core.registries;

public class AbstractSonarRegistry<T> implements IAbstractSonarRegistry<T> {

	public T value;
	public String name;
	public boolean ignoreNormalTab;
	public boolean shouldRegisterRenderer = true;

	public AbstractSonarRegistry(T value, String name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public IAbstractSonarRegistry<T> setValue(T value) {
		this.value = value;
		return this;
	}

	@Override
	public String getRegistryName() {
		return name;
	}

	@Override
	public IAbstractSonarRegistry<T> setRegistryName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public void setShouldRegisterRenderer(boolean bool) {
		shouldRegisterRenderer = bool;
	}

	@Override
	public boolean shouldRegisterRenderer() {
		return shouldRegisterRenderer;
	}

}
