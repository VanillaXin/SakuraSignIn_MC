package xin.vanilla.mc.capability;

import lombok.NonNull;

import java.util.Date;
import java.util.List;

public interface IPlayerSignInData {
    /**
     * 获取连续签到天数
     */
    int getContinuousSignInDays();

    /**
     * 设置连续签到天数
     */
    void setContinuousSignInDays(int days);

    /**
     * 获取最后签到时间
     */
    Date getLastSignInTime();

    /**
     * 设置最后签到时间
     */
    void setLastSignInTime(Date time);

    /**
     * 获取签到记录
     */
    @NonNull
    List<SignInRecord> getSignInRecords();

    /**
     * 设置签到记录
     */
    void setSignInRecords(List<SignInRecord> records);

}
