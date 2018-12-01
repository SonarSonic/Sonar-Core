package sonar.core.helpers;

import javax.annotation.Nullable;
import java.util.HashMap;

public class SimpleRegistry<K, V> {

    public HashMap<K, V> registry_ni = new HashMap<>();
    public HashMap<V, K> registry_in = new HashMap<>();

    public void register(K name, V item){
        registry_ni.put(name, item);
        registry_in.put(item, name);
        log(name, item);
    }

    @Nullable
    public V getValue(K name){
        return registry_ni.get(name);
    }

    @Nullable
    public K getKey(V item){
        return registry_in.get(item);
    }

    public void log(K name, V item){}

    public int getRegisterCount(){
        return registry_ni.size();
    }

}
