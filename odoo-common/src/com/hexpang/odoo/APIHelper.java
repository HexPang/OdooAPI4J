package com.hexpang.odoo;


import com.hexpang.orm.Helper;
import com.hexpang.model.BaseModel;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

/**
 * Created by HexPang on 2017/3/30.
 */
public class APIHelper extends Helper {
    private final Logger logger = Logger.getLogger("LOG");
    private final String HTTP_PROTOCOL = "http://";
    private ResourceBundle bundler = ResourceBundle.getBundle("odooCfg");
    private final String HOST_KEY = bundler.getString("HOST_NAME");
    private final int PORT_KEY = Integer.valueOf(bundler.getString("PORT_NUM"));
    private final String DATABASENAME_KEY = bundler.getString("DB_NAME");
    final XmlRpcClient client = new XmlRpcClient();
    final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
    private String username;
    private String password;
    private int uid;
    private final XmlRpcClient models = new XmlRpcClient() {{
        setConfig(new XmlRpcClientConfigImpl() {{
            try {
                setServerURL(new URL(String.format("%s%s:%s/xmlrpc/2/object", HTTP_PROTOCOL, HOST_KEY, PORT_KEY)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }});
    }};

    /**
     * 用户认证
     * @param username
     * @param password
     * @return
     */
    public int Authenticate(String username,String password){
        try {
            common_config.setServerURL(new URL(String.format("%s%s:%s/xmlrpc/2/common", HTTP_PROTOCOL, HOST_KEY, PORT_KEY)));
            uid = (Integer) client.execute(common_config, "authenticate",
                    new Object[]{DATABASENAME_KEY, username,password, new Object[]{}});
            this.username = username;
            this.password = password;
            BaseModel.AuthToken = username + ":" + password;
        } catch (ClassCastException e2) {
            uid = -1;
            logger.info("用户名或密码不正确！");
        } catch (MalformedURLException e1) {
            uid = -2;
            logger.info("系统异常!");
        } catch (XmlRpcException e) {
            uid = -3;
            logger.info("系统异常");
        }
        return uid;
    }

    /**
     * 执行接口查询
     * @param model_id
     * @param method
     * @param object
     * @param map
     * @return
     * @throws MalformedURLException
     * @throws XmlRpcException
     */
    public Object execute_kw(String model_id,String method,Object[] object,HashMap map) throws MalformedURLException, XmlRpcException {
        final XmlRpcClient models = new XmlRpcClient() {{
            setConfig(new XmlRpcClientConfigImpl() {{
                setServerURL(new URL(String.format("%s%s:%s/xmlrpc/2/object", HTTP_PROTOCOL, HOST_KEY, PORT_KEY)));
            }});
        }};
        return models.execute("execute_kw",
                asList(
                        DATABASENAME_KEY, uid, password,
                        model_id, method,
                        object,map
                )
                );
    }

    /**
     * 执行接口查询
     * @param model_id
     * @param method
     * @param object
     * @return
     * @throws MalformedURLException
     * @throws XmlRpcException
     */
    public Object execute_kw(String model_id,String method,Object object) throws MalformedURLException, XmlRpcException {
        final XmlRpcClient models = new XmlRpcClient() {{
            setConfig(new XmlRpcClientConfigImpl() {{
                setServerURL(new URL(String.format("%s%s:%s/xmlrpc/2/object", HTTP_PROTOCOL, HOST_KEY, PORT_KEY)));
            }});
        }};
        return models.execute("execute_kw", asList(
                DATABASENAME_KEY, uid, password,
                model_id, method,
                object
        ));
    }

    /**
     * HashMap转换到实体类
     * @param map
     * @param cls
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> T mapToCls(HashMap map, Class cls) throws IllegalAccessException, InstantiationException {
        Object obj = cls.newInstance();
        String[] columns = Columns(cls);
        for(int i=0;i<columns.length;i++){
            String column = columns[i];
            Object value = map.get(column);
            if(false != value && null != value){
                try {
                    Field f = cls.getDeclaredField(column);
                    f.setAccessible(true);
                    Type t = f.getGenericType();
                    if(t.toString().equalsIgnoreCase("int")) {
                        try{
                            f.setInt(obj, Integer.valueOf(value.toString()));
                        }catch (Exception ex){
                            Object[] objs = (Object[]) value;
                            f.setInt(obj, Integer.valueOf(objs[0].toString()));
                        }

                    }else if(t.toString().equalsIgnoreCase("double")) {
                        f.setDouble(obj, Double.valueOf(value.toString()));
                    }else if(t.toString().equalsIgnoreCase("float")) {
                        f.setFloat(obj, Float.valueOf(value.toString()));
                    }else if(t.toString().equalsIgnoreCase("boolean")){
                        f.setBoolean(obj, Boolean.valueOf(value.toString()));
                    }else {
                        f.set(obj,value);
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }

        }

        return (T) obj;
    }

    /**
     * 写入数据
     * @param model
     * @param id
     * @param map
     * @return
     * @throws MalformedURLException
     * @throws XmlRpcException
     */
    public Boolean write(String model,int id,HashMap map) throws MalformedURLException, XmlRpcException {
        Object obj = execute_kw(
                model,
                "write",
                asList(
                        asList(
                                id
                        ),
                        map
                )
        );
        return Boolean.valueOf(obj.toString());
    }

    /**
     * 创建记录
     * @param model
     * @param map
     * @return
     * @throws MalformedURLException
     * @throws XmlRpcException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public int create(String model,HashMap map) throws MalformedURLException, XmlRpcException, InstantiationException, IllegalAccessException {
        Object obj = execute_kw(
                model,
                "create",
                asList( map ));

        return Integer.valueOf(obj.toString());
    }

    /**
     * 搜索记录
     * @param model
     * @param cls
     * @param list
     * @param <T>
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws MalformedURLException
     * @throws XmlRpcException
     */
    public <T> T searchRead(String model,Class cls,List list) throws InstantiationException, IllegalAccessException, MalformedURLException, XmlRpcException {
        Object[] obj = (Object[])execute_kw(model,"search_read",asList(asList(list)));
        if(obj.length > 0){
            if(obj.length > 1){
                List<Object> objs = new ArrayList<Object>();
                for(int i=0;i<obj.length;i++){
                    HashMap<String,String> m = (HashMap)obj[i];
                    objs.add(mapToCls(m,cls));
                }
                return (T) objs.toArray();
            }
            HashMap<String,String> map = (HashMap)obj[0];
            return mapToCls(map,cls);
        }
        return null;
    }

    public <T> T searchRead(String model,Class cls,String[] params) throws InstantiationException, IllegalAccessException, MalformedURLException, XmlRpcException {
        Object[] obj = (Object[])execute_kw(model,"search_read",asList(asList(params)));
        if(obj.length > 0){

            if(obj.length > 1){
                List<Object> objs = new ArrayList<Object>();
                for(int i=0;i<obj.length;i++){
                    HashMap<String,String> m = (HashMap)obj[i];
                    objs.add(mapToCls(m,cls));
                }
                return (T) objs.toArray();
            }
            HashMap<String,String> map = (HashMap)obj[0];
            return mapToCls(map,cls);
        }
        return null;
    }
}
