package xin.vanilla.mc.enums;

public enum ERewardRule {
    BASE_REWARD(1),
    CONTINUOUS_REWARD(2),
    CYCLE_REWARD(3),
    YEAR_REWARD(4),
    MONTH_REWARD(5),
    WEEK_REWARD(6),
    DATE_TIME_REWARD(7);

    private final int code;

    ERewardRule(int code) {
        this.code = code;
    }

    public static ERewardRule valueOf(int code) {
        for (ERewardRule type : ERewardRule.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
