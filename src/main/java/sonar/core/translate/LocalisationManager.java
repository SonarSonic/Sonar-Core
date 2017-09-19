package sonar.core.translate;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import sonar.core.SonarCore;
import sonar.core.helpers.FontHelper;

import java.util.ArrayList;
import java.util.List;

public class LocalisationManager implements IResourceManagerReloadListener {

    public List<ILocalisationHandler> handlers = new ArrayList<>();

    public void clear() {
        handlers.clear();
    }

    public void add(ILocalisationHandler handler) {
        handlers.add(handler);
        loadHandler(handler);
    }

    public void remove(ILocalisationHandler handler) {
        handlers.remove(handler);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        handlers.forEach(this::loadHandler);
    }

    public void loadHandler(ILocalisationHandler handler) {
        handler.getLocalisations(new ArrayList<>()).forEach(LocalisationManager::translate);
    }

    public static Localisation translate(Localisation l) {
        l.toDisplay = FontHelper.translate(l.original);
        l.wasFound = !l.toDisplay.equals(l.original);
        if (!l.wasFound) {
            SonarCore.logger.info("NO TRANSLATION FOUND FOR: " + l.o());
        }
        return l;
    }
}