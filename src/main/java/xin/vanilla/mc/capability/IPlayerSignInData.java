package xin.vanilla.mc.capability;

import lombok.NonNull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Date;
import java.util.List;

public interface IPlayerSignInData extends INBTSerializable<CompoundNBT> {
    // TIPS 加完属性记得去 PlayerSignInDataStorage 里注册

    /**
     * 获取连续签到天数
     */
    int getContinuousSignInDays();

    /**
     * 设置连续签到天数
     */
    void setContinuousSignInDays(int days);

    /**
     * 增加连续签到天数
     */
    int plusContinuousSignInDays();

    /**
     * 重置连续签到天数
     */
    void resetContinuousSignInDays();

    /**
     * 获取最后签到时间
     */
    Date getLastSignInTime();

    /**
     * 设置最后签到时间
     */
    void setLastSignInTime(Date time);

    /**
     * 获取补签卡数量
     */
    int getSignInCard();

    /**
     * 增加补签卡数量
     */
    int plusSignInCard();

    /**
     * 减少补签卡数量
     */
    int subSignInCard();

    /**
     * 设置补签卡数量
     */
    void setSignInCard(int num);

    /**
     * 是否自动领取奖励
     */
    boolean isAutoRewarded();

    /**
     * 设置是否自动领取奖励
     */
    void setAutoRewarded(boolean autoRewarded);

    /**
     * 获取签到记录
     */
    @NonNull
    List<SignInRecord> getSignInRecords();

    /**
     * 设置签到记录
     */
    void setSignInRecords(List<SignInRecord> records);

    void writeToBuffer(PacketBuffer buffer);

    void readFromBuffer(PacketBuffer buffer);

    void copyFrom(IPlayerSignInData capability);

    void save(ServerPlayerEntity player);
}
