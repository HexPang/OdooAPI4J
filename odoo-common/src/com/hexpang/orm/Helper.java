package com.hexpang.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by HexPang on 2017/3/30.
 */
public class Helper {
    /**
     * 通过反射方法获取内部变量信息
     * @return Field[]
     */
    public Field[] GetFields(){
        Class cls = this.getClass();
        Field[] fields = cls.getDeclaredFields();
        return fields;
    }

    /**
     * 获取列名
     * @param cls
     * @return
     */
    public String[] Columns(Class cls){
        Field[] fields = cls.getDeclaredFields();
        //String[] columns = new String[fields.length];
        ArrayList<String> columns = new ArrayList<String>();
        for(int i=0;i<fields.length;i++){
            Field field = fields[i];
            Annotation[] annotations = field.getAnnotations();
            boolean valid = true;
            if(annotations.length > 0){
                for(int j=0;j<annotations.length;j++){
                    if(annotations[j].annotationType().getSimpleName().equalsIgnoreCase("hidden")){
                        valid = false;
                    }
                }
            }
            if(valid){
                String colName = field.getName();
                com.hexpang.orm.Field f = (com.hexpang.orm.Field)field.getAnnotation(com.hexpang.orm.Field.class);
                if(f != null){
                    colName = f.value();
                }

                columns.add(colName);

            }
        }
        String[] cc = new String[columns.size()];
        return columns.toArray(cc);
    }

    /**
     * 查询表名
     * @return
     * @throws IllegalAccessException
     */
    public String Table() throws IllegalAccessException {
        Class cls = this.getClass();
        String table = null;
        Table t = (Table) cls.getAnnotation(Table.class);
        if(t != null){
            table =  t.value();
            if(table != null){
                return table;
            }
        }
        return cls.getSimpleName();
    }

    /**
     * 获取主键
     * @return
     */
    public String PrimaryKey(){
        Class cls = this.getClass();
        String primaryKey = null;
        Table t = (Table) cls.getAnnotation(Table.class);
        if(t != null){
            primaryKey =  t.primaryKey();
            if(primaryKey != null){
                return primaryKey;
            }
        }
        return null;
    }

    public String ModelId(){
        String pk = this.PrimaryKey();
        if(pk != null){
            try {
                Field f = this.getClass().getDeclaredField(pk);
                f.setAccessible(true);
                return f.get(this).toString();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String formatTime(String format, Object v){
        if (v == null)
            return null;
        if (v.equals(""))
            return "";
        SimpleDateFormat df = new SimpleDateFormat(format);

        return df.format(v);
    }
}
