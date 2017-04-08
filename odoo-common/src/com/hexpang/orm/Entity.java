package com.hexpang.orm;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by HexPang on 2017/3/30.
 */
public class Entity extends Helper{
    public HashMap toMap() throws NoSuchFieldException, IllegalAccessException {
        HashMap map = new HashMap();
        String[] columns = this.Columns(this.getClass());
        for(int i=0;i<columns.length;i++){
            Field field = this.getClass().getDeclaredField(columns[i]);
            field.setAccessible(true);
            Object obj = field.get(this);
            if(obj != null){
                map.put(columns[i],obj);
            }
        }

        return map;
    }
}
