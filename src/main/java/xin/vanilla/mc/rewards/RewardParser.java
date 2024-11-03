package xin.vanilla.mc.rewards;


import com.google.gson.JsonObject;

public interface RewardParser<T> {
    /**
     * 反序列化奖励对象
     */
    T deserialize(JsonObject json);

    /**
     * 序列化奖励对象
     */
    JsonObject serialize(T reward);
}
