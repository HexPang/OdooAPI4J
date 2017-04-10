package com.hexpang.model;

import com.hexpang.odoo.APIHelper;
import com.hexpang.orm.Entity;
import org.apache.xmlrpc.XmlRpcException;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HexPang on 2017/3/30.
 */
public class BaseModel extends Entity {
    private static APIHelper apiHelper = null;
    public static String AuthToken = null;
    public static APIHelper getApiHelper(){
        if(apiHelper == null){
            apiHelper = new APIHelper();
            String[] accs = AuthToken.split(":");
            int uid = apiHelper.Authenticate(accs[0], accs[1]);
        }
        return apiHelper;
    }
    
    public <T> T[] All(){
        try {
            return getApiHelper().searchRead(Table(),this.getClass(),Arrays.asList("id",">",0));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int Create(){
        try {
            return getApiHelper().create(Table(),this.toMap());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean Update(){
        String pk = PrimaryKey();
        int id = Integer.valueOf(this.ModelId());
        if(id > 0){
            try {
                return getApiHelper().write(Table(),id,this.toMap());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlRpcException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public <T> T Find(int id){
        String pk = this.PrimaryKey();
        return this.Find(Arrays.asList(pk,"=",id));
    }

    public <T> T Find(String id){
        String pk = this.PrimaryKey();
        return this.Find(Arrays.asList(pk,"=",id));
    }

    @Override
    public String toString() {
        try {
            return this.toMap().toString();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return super.toString();
    }

    public <T> T Find(List list){
        try {
            return (T) getApiHelper().searchRead(Table(),this.getClass(),list);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }
}
