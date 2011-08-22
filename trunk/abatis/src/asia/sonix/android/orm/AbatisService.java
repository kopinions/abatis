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
 * Android������O/RM��񋟂��܂��B
 * 
 * @author sonix - http://www.sonix.asia
 * @since JDK1.5 Android Level 4
 *
 */
public class AbatisService extends SQLiteOpenHelper {
	
    /**
     * DB������������SQLID
     */
	private static final String INIT_CREATE_SQL = "initialize";
	
	/**
     * Default DB file name
     */
	private static final String DB_FILE_NAME = "database.db";
	
    /**
     * ������instance object
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
     * �����N������v���O
     */
	private boolean isFirstStart = false;
	
    /**
     * Default DB file name�𗘗p����Constructor
     *
     * @param context �Ăяo����Context�I�u�W�F�N�g
     *
     */
	private AbatisService(Context context) {
		super(context, DB_FILE_NAME, null, 1);
		this.context = context;
		getDbObject();
	}
	
    /**
     * �w��DB file name�𗘗p����Constructor
     *
     * @param context �Ăяo����Context�I�u�W�F�N�g
     * @param dbName ��������DB file name
     *
     */
	private AbatisService(Context context, String dbName) {
		super(context, dbName.concat(".db") , null, 1);
		this.context = context;
		getDbObject();
	}

    /**
     * Default DB file name�𗘗p����O��Constructor 
     *
     * @param context �Ăяo����Context�I�u�W�F�N�g
     * @param dbName ��������DB file name
     *
     */
	public static synchronized AbatisService getInstance(Context context) {
		if (instance == null) {
			instance = new AbatisService(context);
		}
		return instance;
	}
	
    /**
     * �w��DB file name�𗘗p����O��Constructor
     *
     * @param context �Ăяo����Context�I�u�W�F�N�g
     * @param dbName ��������DB file name
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
     * for upgrade (�o�[�W����0.1�ł͎�������Ă��Ȃ�)
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
     * �w�肵��SQLID��parameter��mapping���āA�N�G������B����map�����X�g�ŕԋp�B
     * 
     * <p>
     * mapping�̎��Aparameter������Ȃ��ꍇ��null��Ԃ��B
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
     * �w�肵��SQLID��parameter��mapping���āA���s����B
     * 
     * <p>
     * mapping�̎��Aparameter������Ȃ��ꍇ��0��Ԃ��B
     * </p>
     *
     * @param sqlId SQLiteDatabase object
     * @param bindParams old version value
     * 
     * @return int ���s�ɂ���ĉe������������s��
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
     * DB����������v���O�̒l���擾����B
     * 
     * @return boolean DB����������v���O
     */
	public boolean isFirstStart() {
		return isFirstStart;
	}
	
    /**
     * SQLiteDatabase Object���擾����B
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
