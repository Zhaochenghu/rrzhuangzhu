package com.renren0351.model.typeadapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.renren0351.model.bean.FeeBean;
import com.renren0351.model.response.FeeResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class FeeBeanTypeAdapter implements JsonDeserializer<FeeResponse>, JsonSerializer<FeeResponse> {
    private static final String TAG = FeeBeanTypeAdapter.class.getSimpleName();
    @Override
    public FeeResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        FeeResponse response = new FeeResponse();
        JsonObject object = json.getAsJsonObject();
        response.code = object.get("code").getAsInt();
        response.msg = object.get("msg").getAsString();
        JsonArray array = object.getAsJsonArray("content");
        List<FeeBean> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            FeeBean bean = new FeeBean();
            float current = -200;
            int index = -1;
            JsonObject feeObject = array.get(i).getAsJsonObject();
            bean.companyCode = feeObject.get("companyCode").getAsString();
            bean.templateType = feeObject.get("templateType").getAsString();
            bean.templateName = feeObject.get("templateName").getAsString();
            LinkedHashMap<Integer,Float> map = new LinkedHashMap<>();
            for (int j = 1; j < 97; j++) {
//                String name = "period" + i;
//                Log.i(TAG, "deserialize: ------------------------------ name:" + name);
                float value = Float.parseFloat(feeObject.get("period" + j).getAsString());
                if (j == 1){
                    current = value;
                    index = 1;
                }else {
                    if (current != value){
                        //保存上次的
                        map.put(index,current);
                        //新的赋值
                        current = value;
                    }
                    index = j;
                }
            }
            //保存最后一次
            map.put(index,current);
            bean.feeMap = map;
            list.add(bean);
        }
        response.list = list;
        return response;
    }

    @Override
    public JsonElement serialize(FeeResponse src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
