package xin.vanilla.mc.rewards.impl;

import com.alibaba.fastjson2.JSONObject;
import xin.vanilla.mc.rewards.RewardParser;

public class SignInCardRewardParser implements RewardParser<Integer> {

    @Override
    public Integer deserialize(JSONObject json) {
        return json.getIntValue("signInCard", 0);
    }

    @Override
    public JSONObject serialize(Integer reward) {
        JSONObject json = new JSONObject();
        json.put("signInCard", reward);
        return json;
    }
}
