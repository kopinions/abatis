package asia.sonix.android.orm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class OrmTestActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // アクティビティの基本処理
        super.onCreate(savedInstanceState);
        
        AbatisService abatisService = AbatisService.getInstance(this);
        for (int i = 0; i < 150; i++) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("orm_id", i);
            param.put("orm_name", "won" + i);
            param.put("orm_age", i);
            param.put("orm_memo", "memo" + i);
            int result = abatisService.execute("sqlId002", param);
            Log.d("test", "result : " + result);
        } 
        
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orm_age", 28);
        List<Map<String, Object>> result = abatisService.executeForMapList("sqlId004", param);
        Log.d("test", "get result : " + result.size());
    }
}
