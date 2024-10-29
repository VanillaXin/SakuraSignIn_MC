package xin.vanilla.mc.enums;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public enum ESignInType {
    REPAIR(0, "补签"),
    SIGN_IN(1, "签到");

    private final int code;
    private final String name;

    ESignInType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ESignInType valueOf(int code) {
        for (ESignInType status : ESignInType.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
