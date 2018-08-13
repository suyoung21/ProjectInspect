package com.glink.inspect.data;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallBackParamData {
    private String orderId = "";
    private String tunnelDevId = "";

    public JSONObject getJSOnObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId",orderId);
            jsonObject.put( "tunnelDevId",tunnelDevId);
        } catch (JSONException e) {

        }
        return jsonObject;
    }
}
