package xin.vanilla.mc.rewards.impl;

import com.alibaba.fastjson2.JSONObject;
import xin.vanilla.mc.rewards.RewardParser;

public class ExpPointRewardParser implements RewardParser<Integer> {

    @Override
    public Integer deserialize(JSONObject json) {
        return json.getIntValue("expPoint", 0);
    }

    @Override
    public JSONObject serialize(Integer reward) {
        JSONObject json = new JSONObject();
        json.put("expPoint", reward);
        return json;
    }
}
