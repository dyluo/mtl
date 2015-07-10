package cn.sh.smg.motianlun.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jl_luo on 2015/7/9.
 */
public class HuoDong implements Serializable{
    public String id;
    public String type;
    public String title;
    public String gallaryid;


    public HuoDong(JSONObject object){
        this.id = object.optString("id");
        this.type = object.optString("type");
        this.title = object.optString("title");
        this.gallaryid = object.optString("gallaryid");
    }

}
