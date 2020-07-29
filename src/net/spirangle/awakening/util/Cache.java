package net.spirangle.awakening.util;

import com.wurmonline.mesh.MeshIO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Cache {

    private class CachedObject {
        private Object object;
        private long time;
    }


    private static Cache instance = null;

    public static Cache getInstance() {
        if(instance==null) instance = new Cache();
        return instance;
    }

    private HashMap<String,CachedObject> objects;

    private Cache() {
        objects = new HashMap<>();
    }

    public void put(String key,Object object,long seconds) {
        CachedObject co = objects.get(key);
        if(co==null) {
            co = new CachedObject();
            objects.put(key,co);
        }
        co.object = object;
        co.time = System.currentTimeMillis()+(seconds*1000L);
    }

    public Object get(String key) {
        CachedObject co = objects.get(key);
        if(co==null || co.time<System.currentTimeMillis()) return null;
        return co.object;
    }

    public void gc() {
        long tm = System.currentTimeMillis();
        Iterator<Map.Entry<String,CachedObject>> it = objects.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String,CachedObject> entry = it.next();
            CachedObject co = entry.getValue();
            if(co.time<tm) it.remove();
            if(co.object instanceof MeshIO) {
                try {
                    ((MeshIO)co.object).close();
                } catch(IOException e) {}
            }
        }
    }
}
