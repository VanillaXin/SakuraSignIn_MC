package xin.vanilla.mc.capability;

import lombok.NonNull;
import xin.vanilla.mc.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerSignInData implements IPlayerSignInData {
    private int continuousSignInDays;
    private Date lastSignInTime;
    private List<SignInRecord> signInRecords;

    @Override
    public int getContinuousSignInDays() {
        return continuousSignInDays;
    }

    @Override
    public void setContinuousSignInDays(int days) {
        this.continuousSignInDays = days;
    }

    @Override
    public Date getLastSignInTime() {
        return lastSignInTime;
    }

    @Override
    public void setLastSignInTime(Date time) {
        this.lastSignInTime = time;
    }

    @Override
    public @NonNull List<SignInRecord> getSignInRecords() {
        return CollectionUtils.isNullOrEmpty(signInRecords) ? new ArrayList<>() : signInRecords;
    }

    @Override
    public void setSignInRecords(List<SignInRecord> records) {
        this.signInRecords = records;
    }
}
