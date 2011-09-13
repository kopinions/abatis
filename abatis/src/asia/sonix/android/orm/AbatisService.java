/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package asia.sonix.android.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Android向けのO/RMを提供します。
 * 
 * @author sonix - http://www.sonix.asia
 * @since JDK1.6 Android Level 3
 *
 */
public class AbatisService extends SQLiteOpenHelper {
    /**
     * Debug TAG名
     */
    private static final String TAG = "aBatis";
    
    /**
     * DBを初期化するSQLID
     */
    private static final String INIT_CREATE_SQL = "initialize";	
    /**
     * Default DB file name
     */
    private static final String DB_FILE_NAME = "database.db";
    
    /**
     * 自分のinstance object
     */
    private static AbatisService instance = null;
    
    /**
     * SQLiteDatabase object
     */
    private SQLiteDatabase dbObj;
    
    /**
     * Context object
     */
    private Context context;
    
    /**
     * Default DB file nameを利用するConstructor
     *
     * @param context 呼び出し元Contextオブジェクト
     *
     */
    private AbatisService(Context context) {
        super(context, DB_FILE_NAME, null, 1);
        this.context = context;
    }
    
    /**
     * 指定DB file nameを利用するConstructor
     *
     * @param context 呼び出し元Contextオブジェクト
     * @param dbName 生成するDB file name
     *
     */
    private AbatisService(Context context, String dbName) {
        super(context, dbName.concat(".db") , null, 1);
        this.context = context;
    }
    
    /**
     * Default DB file nameを利用する外部Constructor 
     *
     * @param context 呼び出し元Contextオブジェクト
     * @param dbName 生成するDB file name
     *
     */
    public static AbatisService getInstance(Context context) {
	    if (instance == null) {
		    instance = new AbatisService(context);
		}
	    return instance;
	}
    
    /**
     * 指定DB file nameを利用する外部Constructor
     *
     * @param context 呼び出し元Contextオブジェクト
     * @param dbName 生成するDB file name
     *
     */
    public static AbatisService getInstance(Context context, String dbName) {
        if (instance == null) {
            instance = new AbatisService(context, dbName);
        }
        return instance;
    }
    
    /**
     * DB connector
     *
     * @param db SQLiteDatabase object
     *
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        int pointer = context.getResources().getIdentifier(INIT_CREATE_SQL, "string", context.getPackageName());
        if (pointer == 0) {
            Log.e(TAG, "undefined sql id - initialize");
        } else {
            String createTabelSql = context.getResources().getString(pointer);
            db.execSQL(createTabelSql);
        }
    }
    
    /**
     * for upgrade (バージョン0.1では実装されていない)
     *
     * @param db SQLiteDatabase object
     * @param oldVersion old version value
     * @param newVersion new version value
     * 
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no poc
    }
 
    /**
     * 指定したSQLIDにparameterをmappingして、クエリする。結果mapをリストで返却。
     * 
     * <p>
     * mappingの時、parameterが足りない場合はnullを返す。
     * </p>
     *
     * @param sqlId SQLID
     * @param bindParams sql parameter
     * 
     * @return List<Map<String, Object>> result
     */
    public List<Map<String, Object>> executeForMapList(String sqlId, Map<String, Object> bindParams) {
        getDbObject();
        int pointer = context.getResources().getIdentifier(sqlId, "string", context.getPackageName());
        if (pointer == 0) {
            Log.e(TAG, "undefined sql id");
            return null;
        }
        String sql = context.getResources().getString(pointer);
        if (bindParams != null) {
            Iterator<String> mapIterator = bindParams.keySet().iterator();
            while (mapIterator.hasNext()) {
                String key = mapIterator.next();
                Object value = bindParams.get(key);
                sql = sql.replaceAll("#" + key.toLowerCase() + "#", "'" + value.toString() + "'");
            }
        }
        if (sql.indexOf('#') != -1) {
            Log.e(TAG, "undefined parameter");
            return null;
        }
        Cursor cursor = dbObj.rawQuery(sql, null);
        List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
        if (cursor == null) {
            return null;
        }
        String[] columnNames = cursor.getColumnNames();
        while(cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            int i = 0;
            for (String columnName : columnNames) {
                map.put(columnName, cursor.getString(i));
                i++;
            }
            mapList.add(map);
        }
        cursor.close();
        dbObj.close();
        return mapList;
    }
	
    /**
     * 指定したSQLIDにparameterをmappingして、クエリする。結果beanをリストで返却。
     * 
     * <p>
     * mappingの時、parameterが足りない場合はnullを返す。
     * </p>
     *
     * @param sqlId SQLID
     * @param bindParams sql parameter
     * @param bean bean class of result 
     * 
     * @return List<Map<String, Object>> result
     */
    @SuppressWarnings("rawtypes")
    public List<Object> executeForBeanList(String sqlId, Map<String, Object> bindParams, Class bean) {
        getDbObject();
        int pointer = context.getResources().getIdentifier(sqlId, "string", context.getPackageName());
        if (pointer == 0) {
            Log.e(TAG, "undefined sql id");
            return null;
        }
        String sql = context.getResources().getString(pointer);
        if (bindParams != null) {
            Iterator<String> mapIterator = bindParams.keySet().iterator();
            while (mapIterator.hasNext()) {
                String key = mapIterator.next();
                Object value = bindParams.get(key);
                sql = sql.replaceAll("#" + key.toLowerCase() + "#", "'" + value.toString() + "'");
            }
        }
        if (sql.indexOf('#') != -1) {
            Log.e(TAG, "undefined parameter");
            return null;
        }
        Cursor cursor = dbObj.rawQuery(sql, null);
        List<Object> objectList = new ArrayList<Object>();
        if (cursor == null) {
            return null;
        }
        String[] columnNames = cursor.getColumnNames();
        Object beanObj = null;
        // get bean class package
        Package beanPackage = bean.getClass().getPackage();
        while(cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            int i = 0;
            for (String columnName : columnNames) {
                map.put(columnName, cursor.getString(i));
                i++;
            }
            JSONObject json = new JSONObject(map);
            try {
                beanObj = parse(json.toString(), bean, beanPackage.getName());
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                return null;
            } 
            objectList.add(beanObj);
        }
        cursor.close();
        dbObj.close();
        return objectList;
    }
    
    /**
     * 指定したSQLIDにparameterをmappingして、実行する。
     * 
     * <p>
     * mappingの時、parameterが足りない場合は0を返す。
     * </p>
     *
     * @param sqlId SQLiteDatabase object
     * @param bindParams old version value
     * 
     * @return int 実行によって影響をもらった行数
     */
    public int execute(String sqlId, Map<String, Object> bindParams) {
        getDbObject();
        int row = 0;
        int pointer = context.getResources().getIdentifier(sqlId, "string", context.getPackageName());
        if (pointer == 0) {
            Log.e(TAG, "undefined sql id");
            return row;
        }
        String sql = context.getResources().getString(pointer);
        if (bindParams != null) {
            Iterator<String> mapIterator = bindParams.keySet().iterator();
            while (mapIterator.hasNext()) {
                String key = mapIterator.next();
                Object value = bindParams.get(key);
                sql = sql.replaceAll("#" + key.toLowerCase() + "#", "'" + value.toString() + "'");
            }
        }
        if (sql.indexOf('#') != -1) {
            Log.e(TAG, "undefined parameter");
            return row;
        }
        try {
            dbObj.execSQL(sql);
            dbObj.close();
            row += 1;
        } catch (SQLException e) {
            return row;
        }
        return row;
    }
    
    /**
     * SQLiteDatabase Objectを取得する。
     * 
     * @return SQLiteDatabase SQLiteDatabase Object
     */
    private SQLiteDatabase getDbObject() {
        if (dbObj == null || !dbObj.isOpen()) {
            dbObj = getWritableDatabase();
        }
        return dbObj;
    }
    
    /**
     * JsonStringからBeanに変換する。
     *  
     * @param jsonStr JSON String
     * @param beanClass Bean class 
     * @param basePackage Base package name which includes all Bean classes 
     * @return Object Bean
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object parse(String jsonStr, Class beanClass, String basePackage) throws Exception {
        Object obj = null;
        JSONObject jsonObj = new JSONObject(jsonStr);
        // Check bean object
        if(beanClass == null){
            Log.d(TAG, "Bean class is null");
            return null;
        }
        // Read Class member fields
        Field[] props = beanClass.getDeclaredFields();
        if(props == null || props.length == 0) {
            Log.d(TAG, "Class"+ beanClass.getName() +" has no fields");
            return null;
        }
        // Create instance of this Bean class
        obj = beanClass.newInstance();
        // Set value of each member variable of this object
        for (int i=0; i<props.length; i++) {
            String fieldName = props[i].getName();
            // Skip public and static fields
            if (props[i].getModifiers() == (Modifier.PUBLIC | Modifier.STATIC)) {
                continue;
            }
            // Date Type of Field 
            Class type = props[i].getType();
            String typeName = type.getName();
            // Check for Custom type
            if (typeName.equals("int")) {
                Class[] parms = {type};
                Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
                m.setAccessible(true);
                // Set value
                try {
                    m.invoke(obj, jsonObj.getInt(fieldName));
                } catch (Exception ex) {
                    Log.d(TAG, ex.getMessage());
                }
            } else if (typeName.equals("long")) {
                Class[] parms = {type};
                Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
                m.setAccessible(true);
                // Set value
                try {
                    m.invoke(obj, jsonObj.getLong(fieldName));
                }catch(Exception ex){
                    Log.d(TAG, ex.getMessage());
                }
            } else if (typeName.equals("java.lang.String")) {
                Class[] parms = {type};
                Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
                m.setAccessible(true);
                // Set value
                try {
                    m.invoke(obj, jsonObj.getString(fieldName));
                } catch (Exception ex) {
                    Log.d(TAG, ex.getMessage());
                }
            } else if (typeName.equals("double")) {
                Class[] parms = {type};
                Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
                m.setAccessible(true);
                // Set value
                try {
                    m.invoke(obj,  jsonObj.getDouble(fieldName));
                } catch(Exception ex){
                    Log.d(TAG, ex.getMessage());
                }
            } else if (type.getName().equals(List.class.getName()) || 
                    type.getName().equals(ArrayList.class.getName())) {
                // Find out the Generic
                String generic = props[i].getGenericType().toString();
                if (generic.indexOf("<") != -1) {
                    String genericType = generic.substring(generic.lastIndexOf("<") + 1, generic.lastIndexOf(">"));
                    if (genericType != null) {
                        JSONArray array = null;
                        try {
                            array = jsonObj.getJSONArray(fieldName);
                        } catch(Exception ex) {
                            Log.d(TAG, ex.getMessage());
                            array = null;
                        }
                        if (array == null) {
                            continue;
                        }
                        ArrayList arrayList = new ArrayList();
                        for (int j=0; j<array.length(); j++) {
                            arrayList.add(parse(array.getJSONObject(j).toString(), Class.forName(genericType), basePackage));
                        }
                        // Set value
                        Class[] parms = {type};
                        Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
                        m.setAccessible(true);
                        m.invoke(obj, arrayList);
                    }
                } else {
                    // No generic defined
                    generic = null;
                }
            } else if (typeName.startsWith(basePackage)) {
                Class[] parms = {type};
                Method m = beanClass.getDeclaredMethod(getBeanMethodName(fieldName, 1), parms);
                m.setAccessible(true);
                // Set value
                try{
                    JSONObject customObj = jsonObj.getJSONObject(fieldName);
                    if (customObj != null) {
                        m.invoke(obj, parse(customObj.toString(), type, basePackage));
                    }
                } catch (JSONException ex) {
                    Log.d(TAG, ex.getMessage());
                }
            } else {
                // Skip
                Log.d(TAG, "Field " + fieldName + "#" + typeName + " is skip");
            }
        }
        return obj;
    }
    
    /**
     * BeanClass fields名からmethod名を取得する。
     * @param fieldName
     * @param type
     * @return String MethodName
     */
    private String getBeanMethodName(String fieldName, int type){
        if(fieldName == null || fieldName == "") {
            return "";
        }
        String method_name = "";
        if(type == 0) {
            method_name = "get";
        } else {
            method_name = "set";
        }
        method_name += fieldName.substring(0, 1).toUpperCase();
        if (fieldName.length() == 1) {
            return method_name;
        }
        method_name += fieldName.substring(1);
        return method_name;
    }
}
