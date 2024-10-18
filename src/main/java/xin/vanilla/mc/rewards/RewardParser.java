package xin.vanilla.mc.rewards;

import com.alibaba.fastjson2.JSONObject;

public interface RewardParser<T> {
    /**
     * 反序列化奖励对象
     */
    T deserialize(JSONObject json);

    /**
     * 序列化奖励对象
     */
    JSONObject serialize(T reward);
}
