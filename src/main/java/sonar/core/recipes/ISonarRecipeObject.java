package sonar.core.recipes;
public interface ISonarRecipeObject {

		public Object getValue();

		public boolean matches(Object object);
	}