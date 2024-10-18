package xin.vanilla.mc.rewards.impl;

import com.alibaba.fastjson2.JSONObject;
import xin.vanilla.mc.rewards.RewardParser;

public class ExpLevelRewardParser implements RewardParser<Integer> {

    @Override
    public Integer deserialize(JSONObject json) {
        return json.getIntValue("expLevel", 0);
    }

    @Override
    public JSONObject serialize(Integer reward) {
        JSONObject json = new JSONObject();
        json.put("expLevel", reward);
        return json;
    }
}
