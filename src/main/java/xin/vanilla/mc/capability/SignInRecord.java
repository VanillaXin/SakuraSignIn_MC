package xin.vanilla.mc.capability;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.nbt.CompoundNBT;
import xin.vanilla.mc.rewards.RewardList;

import java.io.Serializable;
import java.util.Date;

/**
 * 签到记录
 */
@Data
public class SignInRecord implements Serializable, Cloneable {
    /**
     * 补偿后时间(签到时间+签到冷却刷新时间)
     */
    @NonNull
    private Date compensateTime;
    /**
     * 签到时间
     */
    @NonNull
    private Date signInTime;
    /**
     * 签到玩家
     */
    @NonNull
    private String signInUUID;
    /**
     * 奖励是否领取
     */
    private boolean claimed;
    /**
     * 签到物品奖励
     */
    @NonNull
    private RewardList rewardList;

    public SignInRecord() {
        this.compensateTime = new Date();
        this.signInTime = new Date();
        this.signInUUID = "";
        this.rewardList = new RewardList();
    }


    // 序列化到 NBT
    public CompoundNBT writeToNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putLong("compensateTime", compensateTime.getTime());
        tag.putLong("signInTime", signInTime.getTime());
        tag.putString("signInUUID", signInUUID);
        tag.putBoolean("claimed", claimed);
        tag.putString("reward", JSON.toJSONString(rewardList));
        return tag;
    }

    // 反序列化方法
    public static SignInRecord readFromNBT(CompoundNBT tag) {
        SignInRecord record = new SignInRecord();
        // 读取简单字段
        record.compensateTime = new Date(tag.getLong("compensateTime"));
        record.signInTime = new Date(tag.getLong("signInTime"));
        record.signInUUID = tag.getString("signInUUID");
        record.claimed = tag.getBoolean("claimed");

        // 反序列化奖励列表
        String rewardListString = tag.getString("rewardList");
        record.rewardList = JSON.parseObject(rewardListString, RewardList.class);
        return record;
    }

    @Override
    public SignInRecord clone() {
        try {
            SignInRecord cloned = (SignInRecord) super.clone();
            cloned.compensateTime = (Date) this.compensateTime.clone();
            cloned.signInTime = (Date) this.signInTime.clone();
            cloned.signInUUID = this.signInUUID;
            cloned.claimed = this.claimed;
            cloned.rewardList = (RewardList) this.rewardList.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
