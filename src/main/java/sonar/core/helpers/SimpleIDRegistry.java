package sonar.core.helpers;

import sonar.core.api.IRegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SimpleIDRegistry<T extends IRegistryObject> extends SimpleRegistry<Integer, String> {

    public List<T> objects = new ArrayList();

    public void register(T object){
        objects.add(object);
        register(objects.size(), object.getName());
    }

    /**don't modify the return*/
    public List<T> getObjects(){
        return objects;
    }

    @Nullable
    public T getObject(String name){
        Integer id = getKey(name);
        if(id != null){
            return objects.get(id - 1);
        }
        return null;
    }

    @Nullable
    public T getObject(int id){
        if(id <= objects.size()) {
            return objects.get(id - 1);
        }
        return null;
    }

}
