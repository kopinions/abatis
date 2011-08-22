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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Android向けのO/RMを提供します。
 * 
 * @author sonix - http://www.sonix.asia
 * @since JDK1.5 Android Level 4
 *
 */
public class AbatisService extends SQLiteOpenHelper {
	
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
     * 初期起動判定プラグ
     */
	private boolean isFirstStart = false;
	
    /**
     * Default DB file nameを利用するConstructor
     *
     * @param context 呼び出し元Contextオブジェクト
     *
     */
	private AbatisService(Context context) {
		super(context, DB_FILE_NAME, null, 1);
		this.context = context;
		getDbObject();
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
		getDbObject();
	}

    /**
     * Default DB file nameを利用する外部Constructor 
     *
     * @param context 呼び出し元Contextオブジェクト
     * @param dbName 生成するDB file name
     *
     */
	public static synchronized AbatisService getInstance(Context context) {
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
	public static synchronized AbatisService getInstance(Context context, String dbName) {
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
    		Log.e(this.getClass().getName(), "undefined sql id - initialize");
    	} else {
    		String createTabelSql = context.getResources().getString(pointer);
    		db.execSQL(createTabelSql);
    	}
    	isFirstStart = true;
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
     * @param bindParams old version value
     * 
     * @return List<Map<String, Object>> result
     */
	public List<Map<String, Object>> executeForMapList(String sqlId, Map<String, Object> bindParams) {
        int pointer = context.getResources().getIdentifier(sqlId, "string", context.getPackageName());
    	if (pointer == 0) {
    		Log.e(this.getClass().getName(), "undefined sql id");
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
    		Log.e(this.getClass().getName(), "undefined parameter");
    		return null;
    	}
        Cursor cursor = getWritableDatabase().rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
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
        return mapList;
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
    	int row = 0;
    	int pointer = context.getResources().getIdentifier(sqlId, "string", context.getPackageName());
    	if (pointer == 0) {
    		Log.e(this.getClass().getName(), "undefined sql id");
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
    		Log.e(this.getClass().getName(), "undefined parameter");
    		return row;
    	}
    	getWritableDatabase().execSQL(sql);
    	row += 1;
        return row;
    }

    /**
     * DB初期化判定プラグの値を取得する。
     * 
     * @return boolean DB初期化判定プラグ
     */
	public boolean isFirstStart() {
		return isFirstStart;
	}
	
    /**
     * SQLiteDatabase Objectを取得する。
     * 
     * @return SQLiteDatabase SQLiteDatabase Object
     */
	private synchronized SQLiteDatabase getDbObject() {
		if (dbObj == null) {
			dbObj = getWritableDatabase();
		}
		return dbObj;
	}
}
