package xin.vanilla.mc.rewards;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import xin.vanilla.mc.enums.ERewardType;

import java.io.Serializable;

/**
 * 奖励实体
 */
@Data
public class Reward implements Cloneable, Serializable {
    /**
     * 奖励是否领取
     */
    private boolean claimed;
    /**
     * 奖励是否禁用
     */
    private boolean disabled;
    /**
     * 奖励类型
     */
    private ERewardType type;
    /**
     * 奖励内容
     */
    private JSONObject content;

    public Reward() {
    }

    public Reward(JSONObject content, ERewardType type) {
        this.content = content;
        this.type = type;
    }

    @Override
    public Reward clone() {
        try {
            Reward cloned = (Reward) super.clone();
            cloned.claimed = this.claimed;
            cloned.disabled = this.disabled;
            cloned.type = this.type;
            cloned.content = this.content.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
