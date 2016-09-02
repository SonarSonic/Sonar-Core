package sonar.core.utils;

public abstract class SonarValidation {

	public abstract boolean isValid(Object obj);

	public static class CLASS<T> extends SonarValidation {

		public Class<T> type;

		public CLASS(Class<T> type) {
			this.type = type;
		}

		@Override
		public boolean isValid(Object obj) {
			return obj != null && type.isInstance(obj);
		}

	}
}
