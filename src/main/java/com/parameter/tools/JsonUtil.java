package com.parameter.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName JsonUtil.java
 * @Description TODO
 * @createTime 2022年03月18日 14:02:00
 */
public class JsonUtil {
    /**
     * 把ResultSet集合转换成JsonArray数组
     *
     * @param rs
     * @return
     * @throws Exception
     */
    public static String formatRsToJsonArray(ResultSet rs){
        JSONArray array = null;// json数组，根据下标找值；[{name1:wp},{name2:{name3:'ww'}}]name为key值，wp为value值
        try {
            ResultSetMetaData md = rs.getMetaData();// 获取表结构
            int num = md.getColumnCount();// 得到行的总数
            array = new JSONArray();
            while (rs.next()) {// 如果结果集中有值
                JSONObject mapOfColValues = new JSONObject();// 创建json对象就是一个{name:wp}
                for (int i = 1; i <= num; i++) {
                    if(rs.getObject(i).getClass().getName().contains("Timestamp")){
                        String time = JSON.toJSONStringWithDateFormat(rs.getObject(i), "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);
                        mapOfColValues.put(md.getColumnName(i), time.replace("\"", ""));
                    }else {
                        mapOfColValues.put(md.getColumnName(i), rs.getObject(i));// 添加键值对，比如说{name:Wp}通过name找到wp
                    }
                }
                array.add(mapOfColValues);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("ResultSet转JSONArray异常","error");
        }
        return array.toString();
    }


    /**
     * 把ResultSet集合转换成JsonArray
     *
     * @param rs
     * @return
     * @throws Exception
     */
    public static JSONArray formatRsToArray(ResultSet rs){
        JSONArray array = null;// json数组，根据下标找值；[{name1:wp},{name2:{name3:'ww'}}]name为key值，wp为value值
        try {
            ResultSetMetaData md = rs.getMetaData();// 获取表结构
            int num = md.getColumnCount();// 得到行的总数
            array = new JSONArray();
            while (rs.next()) {// 如果结果集中有值
                JSONObject mapOfColValues = new JSONObject();// 创建json对象就是一个{name:wp}
                for (int i = 1; i <= num; i++) {
                    if(rs.getObject(i).getClass().getName().contains("Timestamp")){
                        String time = JSON.toJSONStringWithDateFormat(rs.getObject(i), "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);
                        mapOfColValues.put(md.getColumnName(i), time.replace("\"", ""));
                    }else {
                        mapOfColValues.put(md.getColumnName(i), rs.getObject(i));// 添加键值对，比如说{name:Wp}通过name找到wp
                    }
                }
                array.add(mapOfColValues);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogCommon.WriteLogNormal("ResultSet转JSONArray异常","error");
        }
        return array;
    }

    /**
     * json大写转小写
     *
     * @return JSONObject
     */
    public static JSONObject transToLowerObject(String json) {
        JSONObject JSONObject2 = new JSONObject();
        JSONObject JSONObject1 = JSON.parseObject(json, Feature.OrderedField);
        for (String key : JSONObject1.keySet()){
            Object object = JSONObject1.get(key);
            if (object.getClass().toString().endsWith("JSONObject")) {
                JSONObject2.put(key.toLowerCase(), transToLowerObject(object.toString()));
            } else if (object.getClass().toString().endsWith("JSONArray")) {
                JSONObject2.put(key.toLowerCase(), transToArrayLower(JSONObject1.getJSONArray(key).toString()));
            }else{
                JSONObject2.put(key.toLowerCase(), object);
            }
        }
        return JSONObject2;
    }

    /**
     * jsonArray转jsonArray
     *
     * @return JSONArray
     */
    public static JSONArray transToArrayLower(String jsonArray) {
        JSONArray jSONArray2 = new JSONArray();
        JSONArray jSONArray1 = JSON.parseArray(jsonArray);
        for (int i = 0; i < jSONArray1.size(); i++) {
            Object jArray = jSONArray1.getJSONObject(i);
            if (jArray.getClass().toString().endsWith("JSONObject")) {
                jSONArray2.add(transToLowerObject( jArray.toString()));
            } else if (jArray.getClass().toString().endsWith("JSONArray")) {
                jSONArray2.add(transToArrayLower(jArray.toString()));
            }
        }
        return jSONArray2;
    }


    /**
     * json大写转小写
     *
     * @return JSONObject
     */
    public static JSONObject transToUpperObject(String json) {
        JSONObject JSONObject2 = new JSONObject();
        JSONObject JSONObject1 = JSON.parseObject(json, Feature.OrderedField);
        for (String key : JSONObject1.keySet()){
            Object object = JSONObject1.get(key);
            if (object.getClass().toString().endsWith("JSONObject")) {
                JSONObject2.put(key.toUpperCase(), transToLowerObject(object.toString()));
            } else if (object.getClass().toString().endsWith("JSONArray")) {
                JSONObject2.put(key.toUpperCase(), transToArrayUpper(JSONObject1.getJSONArray(key).toString()));
            }else{
                JSONObject2.put(key.toUpperCase(), object);
            }
        }
        return JSONObject2;
    }

    /**
     * jsonArray转jsonArray
     *
     * @return JSONArray
     */
    public static JSONArray transToArrayUpper(String jsonArray) {
        JSONArray jSONArray2 = new JSONArray();
        JSONArray jSONArray1 = JSON.parseArray(jsonArray);
        for (int i = 0; i < jSONArray1.size(); i++) {
            Object jArray = jSONArray1.getJSONObject(i);
            if (jArray.getClass().toString().endsWith("JSONObject")) {
                jSONArray2.add(transToLowerObject( jArray.toString()));
            } else if (jArray.getClass().toString().endsWith("JSONArray")) {
                jSONArray2.add(transToArrayUpper(jArray.toString()));
            }
        }
        return jSONArray2;
    }
}
