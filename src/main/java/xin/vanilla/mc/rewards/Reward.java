package xin.vanilla.mc.rewards;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
    private boolean rewarded;
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
            cloned.rewarded = this.rewarded;
            cloned.disabled = this.disabled;
            cloned.type = this.type;
            cloned.content = this.content.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static Reward getDefault() {
        return new Reward(RewardManager.serializeReward(new ItemStack(Items.AIR), ERewardType.ITEM), ERewardType.ITEM);
    }
}
