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

	public static class CLASSLIST extends SonarValidation {

		public Class[] types;

		public CLASSLIST(Class... types) {
			this.types = types;
		}

		@Override
		public boolean isValid(Object obj) {
			if(obj==null){
				return false;
			}
			for(Class type :types){
				if(type.isInstance(obj)){
					return true;
				}
			}			
			return false;
		}
	}
}
