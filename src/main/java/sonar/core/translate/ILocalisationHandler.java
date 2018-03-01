package sonar.core.translate;

import java.util.List;

public interface ILocalisationHandler {

    List<Localisation> getLocalisations(List<Localisation> current);

}