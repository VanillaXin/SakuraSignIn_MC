package xin.vanilla.mc.screen;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 背景图片坐标配置, 坐标宽高度单位都为像素
 */
@Getter
@Setter
@Accessors(chain = true)
@SuppressWarnings("ConstantConditions")
public class CalendarTextureCoordinate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 纹理图片总宽度
     */
    private int totalWidth = 500;
    /**
     * 纹理图片总高度
     */
    private int totalHeight = 1000;
    /**
     * 周起始日
     * 7: 周日
     * 1: 周一
     * 2: 周二
     * 3: 周三
     * 4: 周四
     * 5: 周五
     * 6: 周六
     */
    private int weekStart = 7;

    /**
     * 文本颜色: 默认
     */
    private int textColorDefault = 0xFFDBDBDB;
    /**
     * 文本颜色: 本月日期
     */
    private int textColorCurrent = 0xFFFFFFFF;
    /**
     * 文本颜色: 可补签
     */
    private int textColorCanRepair = 0xFFFFFFBB;
    /**
     * 文本颜色: 今天
     */
    private int textColorToday = 0xFF474747;
    /**
     * 文本颜色: 日期
     */
    private int textColorDate = 0xFF000000;

    /**
     * 年份区域坐标
     */
    @NonNull
    private TextureCoordinate yearCoordinate = new TextureCoordinate().setX(128).setY(60).setWidth(92).setHeight(16);

    /**
     * 月份区域坐标
     */
    @NonNull
    private TextureCoordinate monthCoordinate = new TextureCoordinate().setX(320).setY(60).setWidth(62).setHeight(16);

    /**
     * 日期区域坐标
     */
    @NonNull
    private TextureCoordinate cellCoordinate = new TextureCoordinate().setX(73).setY(134).setWidth(36).setHeight(36);
    /**
     * 日期单元格左右边距
     */
    private int cellHMargin = 18;
    /**
     * 日期单元格上下边距
     */
    private int cellVMargin = 26;

    /**
     * 左箭头区域坐标
     */
    @NonNull
    private TextureCoordinate leftArrowCoordinate = new TextureCoordinate().setX(320).setY(60).setWidth(62).setHeight(16);

    /**
     * 右箭头区域坐标
     */
    @NonNull
    private TextureCoordinate rightArrowCoordinate = new TextureCoordinate().setX(320).setY(60).setWidth(62).setHeight(16);

    /**
     * 上箭头区域坐标
     */
    @NonNull
    private TextureCoordinate upArrowCoordinate = new TextureCoordinate().setX(128).setY(60).setWidth(92).setHeight(16);

    /**
     * 下箭头区域坐标
     */
    @NonNull
    private TextureCoordinate downArrowCoordinate = new TextureCoordinate().setX(128).setY(60).setWidth(92).setHeight(16);

    /**
     * 主题区域坐标
     */
    @NonNull
    private TextureCoordinate themeCoordinate = new TextureCoordinate().setX(300).setY(552).setWidth(40).setHeight(48);
    /**
     * 主题按钮左右边距
     */
    private int themeHMargin = 0;

    /**
     * 背景纹理坐标
     */
    @NonNull
    private TextureCoordinate bgUV = new TextureCoordinate().setU0(0).setV0(0).setUWidth(500).setVHeight(600);

    /**
     * 奖励弹出层纹理坐标
     */
    @NonNull
    private TextureCoordinate tooltipUV = new TextureCoordinate().setU0(0).setV0(784).setUWidth(300).setVHeight(96);
    /**
     * 弹出层单元格左右边距
     */
    private int tooltipCellHMargin = 5;
    /**
     * 弹出层单元格坐标
     */
    @NonNull
    private TextureCoordinate tooltipCellCoordinate = new TextureCoordinate().setX(15).setY(10).setWidth(42).setHeight(42);
    /**
     * 弹出层日期坐标
     */
    @NonNull
    private TextureCoordinate tooltipDateCoordinate = new TextureCoordinate().setX(110).setY(64).setWidth(50).setHeight(19);
    /**
     * 弹出层滚动条坐标
     */
    @NonNull
    private TextureCoordinate tooltipScrollCoordinate = new TextureCoordinate().setX(10).setY(93).setWidth(280).setHeight(1);
    /**
     * 箭头纹理坐标
     */
    @NonNull
    private TextureCoordinate arrowUV = new TextureCoordinate().setU0(0).setV0(600).setUWidth(40).setVHeight(40);
    /**
     * 箭头焦点纹理坐标
     */
    @NonNull
    private TextureCoordinate arrowHoverUV = new TextureCoordinate().setU0(0).setV0(600).setUWidth(40).setVHeight(40);
    /**
     * 箭头按下纹理坐标
     */
    @NonNull
    private TextureCoordinate arrowTapUV = new TextureCoordinate().setU0(40).setV0(600).setUWidth(40).setVHeight(40);

    /**
     * 未签到纹理坐标
     */
    @NonNull
    private TextureCoordinate notSignedInUV = new TextureCoordinate().setU0(80).setV0(600).setUWidth(40).setVHeight(40);
    /**
     * 已签到纹理坐标
     */
    @NonNull
    private TextureCoordinate signedInUV = new TextureCoordinate().setU0(120).setV0(600).setUWidth(40).setVHeight(40);
    /**
     * 已领奖纹理坐标
     */
    @NonNull
    private TextureCoordinate rewardedUV = new TextureCoordinate().setU0(160).setV0(600).setUWidth(40).setVHeight(40);

    /**
     * BUFF纹理坐标
     */
    @NonNull
    private TextureCoordinate buffUV = new TextureCoordinate().setU0(200).setV0(600).setUWidth(40).setVHeight(40);
    /**
     * 经验纹理坐标
     */
    @NonNull
    private TextureCoordinate pointUV = new TextureCoordinate().setU0(240).setV0(600).setUWidth(40).setVHeight(40);
    /**
     * 等级纹理坐标
     */
    @NonNull
    private TextureCoordinate levelUV = new TextureCoordinate().setU0(280).setV0(600).setUWidth(40).setVHeight(40);
    /**
     * 签到卡纹理坐标
     */
    @NonNull
    private TextureCoordinate cardUV = new TextureCoordinate().setU0(320).setV0(600).setUWidth(40).setVHeight(40);
    /**
     * 消息纹理坐标
     */
    @NonNull
    private TextureCoordinate messageUV = new TextureCoordinate().setU0(360).setV0(600).setUWidth(40).setVHeight(40);

    /**
     * 主题纹理坐标
     */
    @NonNull
    private TextureCoordinate themeUV = new TextureCoordinate().setU0(0).setV0(640).setUWidth(40).setVHeight(48);
    /**
     * 主题焦点纹理坐标
     */
    @NonNull
    private TextureCoordinate themeHoverUV = new TextureCoordinate().setU0(0).setV0(688).setUWidth(40).setVHeight(48);
    /**
     * 主题按下纹理坐标
     */
    @NonNull
    private TextureCoordinate themeTapUV = new TextureCoordinate().setU0(0).setV0(736).setUWidth(40).setVHeight(48);

    /**
     * 奖励配置页面背景纹理坐标
     */
    @NonNull
    private TextureCoordinate optionBgUV = new TextureCoordinate().setU0(0).setV0(880).setUWidth(120).setVHeight(120);

    // region Always NON-NULL Getter

    public @NonNull TextureCoordinate getYearCoordinate() {
        return yearCoordinate = yearCoordinate == null ? new TextureCoordinate() : yearCoordinate;
    }

    public @NonNull TextureCoordinate getMonthCoordinate() {
        return monthCoordinate = monthCoordinate == null ? new TextureCoordinate() : monthCoordinate;
    }

    public @NonNull TextureCoordinate getCellCoordinate() {
        return cellCoordinate = cellCoordinate == null ? new TextureCoordinate() : cellCoordinate;
    }

    public @NonNull TextureCoordinate getLeftArrowCoordinate() {
        return leftArrowCoordinate = leftArrowCoordinate == null ? new TextureCoordinate() : leftArrowCoordinate;
    }

    public @NonNull TextureCoordinate getRightArrowCoordinate() {
        return rightArrowCoordinate = rightArrowCoordinate == null ? new TextureCoordinate() : rightArrowCoordinate;
    }

    public @NonNull TextureCoordinate getUpArrowCoordinate() {
        return upArrowCoordinate = upArrowCoordinate == null ? new TextureCoordinate() : upArrowCoordinate;
    }

    public @NonNull TextureCoordinate getDownArrowCoordinate() {
        return downArrowCoordinate = downArrowCoordinate == null ? new TextureCoordinate() : downArrowCoordinate;
    }

    public @NonNull TextureCoordinate getThemeCoordinate() {
        return themeCoordinate = themeCoordinate == null ? new TextureCoordinate() : themeCoordinate;
    }

    public @NonNull TextureCoordinate getBgUV() {
        return bgUV = bgUV == null ? new TextureCoordinate() : bgUV;
    }

    public @NonNull TextureCoordinate getTooltipUV() {
        return tooltipUV = tooltipUV == null ? new TextureCoordinate() : tooltipUV;
    }

    public @NonNull TextureCoordinate getTooltipCellCoordinate() {
        return tooltipCellCoordinate = tooltipCellCoordinate == null ? new TextureCoordinate() : tooltipCellCoordinate;
    }

    public @NonNull TextureCoordinate getTooltipDateCoordinate() {
        return tooltipDateCoordinate = tooltipDateCoordinate == null ? new TextureCoordinate() : tooltipDateCoordinate;
    }

    public @NonNull TextureCoordinate getTooltipScrollCoordinate() {
        return tooltipScrollCoordinate = tooltipScrollCoordinate == null ? new TextureCoordinate() : tooltipScrollCoordinate;
    }

    public @NonNull TextureCoordinate getArrowUV() {
        return arrowUV = arrowUV == null ? new TextureCoordinate() : arrowUV;
    }

    public @NonNull TextureCoordinate getArrowHoverUV() {
        return arrowHoverUV = arrowHoverUV == null ? new TextureCoordinate() : arrowHoverUV;
    }

    public @NonNull TextureCoordinate getArrowTapUV() {
        return arrowTapUV = arrowTapUV == null ? new TextureCoordinate() : arrowTapUV;
    }

    public @NonNull TextureCoordinate getNotSignedInUV() {
        return notSignedInUV = notSignedInUV == null ? new TextureCoordinate() : notSignedInUV;
    }

    public @NonNull TextureCoordinate getSignedInUV() {
        return signedInUV = signedInUV == null ? new TextureCoordinate() : signedInUV;
    }

    public @NonNull TextureCoordinate getRewardedUV() {
        return rewardedUV = rewardedUV == null ? new TextureCoordinate() : rewardedUV;
    }

    public @NonNull TextureCoordinate getBuffUV() {
        return buffUV = buffUV == null ? new TextureCoordinate() : buffUV;
    }

    public @NonNull TextureCoordinate getPointUV() {
        return pointUV = pointUV == null ? new TextureCoordinate() : pointUV;
    }

    public @NonNull TextureCoordinate getLevelUV() {
        return levelUV = levelUV == null ? new TextureCoordinate() : levelUV;
    }

    public @NonNull TextureCoordinate getCardUV() {
        return cardUV = cardUV == null ? new TextureCoordinate() : cardUV;
    }

    public @NonNull TextureCoordinate getMessageUV() {
        return messageUV = messageUV == null ? new TextureCoordinate() : messageUV;
    }

    public @NonNull TextureCoordinate getThemeUV() {
        return themeUV = themeUV == null ? new TextureCoordinate() : themeUV;
    }

    public @NonNull TextureCoordinate getThemeHoverUV() {
        return themeHoverUV = themeHoverUV == null ? new TextureCoordinate() : themeHoverUV;
    }

    public @NonNull TextureCoordinate getThemeTapUV() {
        return themeTapUV = themeTapUV == null ? new TextureCoordinate() : themeTapUV;
    }

    public @NonNull TextureCoordinate getOptionBgUV() {
        return optionBgUV = optionBgUV == null ? new TextureCoordinate() : optionBgUV;
    }

    // endregion Always NON-NULL Getter

    /**
     * 获取默认配置
     *
     * @return original主题配置
     */
    @NonNull
    public static CalendarTextureCoordinate getDefault() {
        return new CalendarTextureCoordinate();
    }
}
