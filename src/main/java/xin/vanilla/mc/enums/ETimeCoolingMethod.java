package xin.vanilla.mc.enums;

import lombok.Getter;

/**
 * 签到时间冷却方式
 */
@Getter
public enum ETimeCoolingMethod {
    FIXED_TIME("固定时间"),
    FIXED_INTERVAL("固定间隔"),
    MIXED("混合模式");

    private final String name;

    ETimeCoolingMethod(String name) {
        this.name = name;
    }
}
