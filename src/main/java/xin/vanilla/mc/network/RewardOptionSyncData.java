package xin.vanilla.mc.network;

import lombok.Data;
import xin.vanilla.mc.enums.ERewardRule;
import xin.vanilla.mc.rewards.Reward;

@Data
public class RewardOptionSyncData {
    /**
     * 签到奖励规则
     */
    private final ERewardRule rule;
    /**
     * 签到奖励规则参数
     */
    private final String key;
    /**
     * 签到奖励数据
     */
    private final Reward reward;
}
