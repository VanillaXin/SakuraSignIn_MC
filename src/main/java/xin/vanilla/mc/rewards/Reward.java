package xin.vanilla.mc.rewards;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import xin.vanilla.mc.enums.ERewardType;

import java.io.Serializable;

import static net.minecraft.util.datafix.fixes.SignStrictJSON.GSON;

/**
 * 奖励实体
 */
@Data
@Accessors(chain = true)
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
    private JsonObject content;

    public Reward() {
    }

    public Reward(JsonObject content, ERewardType type) {
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
            cloned.content = GSON.fromJson(GSON.toJson(this.content), JsonObject.class);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static Reward getDefault() {
        return new Reward(RewardManager.serializeReward(new ItemStack(Items.AIR), ERewardType.ITEM), ERewardType.ITEM);
    }

    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("rewarded", this.rewarded);
        json.addProperty("disabled", this.disabled);
        json.addProperty("type", this.type.name());
        json.add("content", this.content);
        return json;
    }
}
