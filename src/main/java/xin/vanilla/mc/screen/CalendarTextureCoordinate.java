package xin.vanilla.mc.screen;

import lombok.Data;

import java.io.Serializable;

/**
 * 背景图片坐标配置, 坐标宽高度单位都为像素
 */
@Data
public class CalendarTextureCoordinate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 纹理图片总宽度
     */
    private int totalWidth;
    /**
     * 纹理图片总高度
     */
    private int totalHeight;
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
    private int weekStart;

    /**
     * 文本颜色: 无法操作
     */
    private int textColorNoAction;
    /**
     * 文本颜色: 无法操作(本月日期)
     */
    private int textColorNoActionCur;
    /**
     * 文本颜色: 可补签
     */
    private int textColorCanRepair;
    /**
     * 文本颜色: 未签到
     */
    private int textColorNotSignedIn;
    /**
     * 文本颜色: 已签到
     */
    private int textColorSignedIn;
    /**
     * 文本颜色: 已领取
     */
    private int textColorRewarded;
    /**
     * 文本颜色: 默认
     */
    private int textColorDefault;

    /**
     * 年份区域坐标
     */
    private TextureCoordinate yearCoordinate;

    /**
     * 月份区域坐标
     */
    private TextureCoordinate monthCoordinate;

    /**
     * 日期区域坐标
     */
    private TextureCoordinate cellCoordinate;
    /**
     * 日期单元格左右边距
     */
    private int cellHMargin;
    /**
     * 日期单元格上下边距
     */
    private int cellVMargin;

    /**
     * 左箭头区域坐标
     */
    private TextureCoordinate leftArrowCoordinate;

    /**
     * 右箭头区域坐标
     */
    private TextureCoordinate rightArrowCoordinate;

    /**
     * 上箭头区域坐标
     */
    private TextureCoordinate upArrowCoordinate;

    /**
     * 下箭头区域坐标
     */
    private TextureCoordinate downArrowCoordinate;

    /**
     * 主题区域坐标
     */
    private TextureCoordinate themeCoordinate;
    /**
     * 主题按钮左右边距
     */
    private int themeHMargin;

    /**
     * 背景纹理坐标
     */
    private TextureCoordinate bgUV;
    /**
     * 奖励纹理坐标
     */
    private TextureCoordinate rewardUV;
    /**
     * 已签到纹理坐标
     */
    private TextureCoordinate signedInUV;
    /**
     * 箭头纹理坐标
     */
    private TextureCoordinate arrowUV;
    /**
     * 箭头焦点纹理坐标
     */
    private TextureCoordinate arrowHoverUV;
    /**
     * 箭头按下纹理坐标
     */
    private TextureCoordinate arrowTapUV;
    /**
     * 主题纹理坐标
     */
    private TextureCoordinate themeUV;
    /**
     * 主题焦点纹理坐标
     */
    private TextureCoordinate themeHoverUV;
    /**
     * 主题按下纹理坐标
     */
    private TextureCoordinate themeTapUV;

    public static CalendarTextureCoordinate getDefault() {
        return new CalendarTextureCoordinate() {{
            setTotalWidth(500);
            setTotalHeight(784);
            setWeekStart(7);

            setTextColorNoAction(0xFFAAAAAA);
            setTextColorNoActionCur(0xFFFFFFFF);
            setTextColorCanRepair(0xFF0000FF);
            setTextColorNotSignedIn(0xFFF27690);
            setTextColorSignedIn(0xFF00FF00);
            setTextColorRewarded(0xFF00AAAA);
            setTextColorDefault(0xFF000000);

            setYearCoordinate(new TextureCoordinate().setX(128).setY(60).setWidth(92).setHeight(16));
            setMonthCoordinate(new TextureCoordinate().setX(320).setY(60).setWidth(62).setHeight(16));

            setCellCoordinate(new TextureCoordinate().setX(70).setY(120).setWidth(34).setHeight(34));
            setCellHMargin(20);
            setCellVMargin(24);

            setLeftArrowCoordinate(new TextureCoordinate().setX(320).setY(60).setWidth(62).setHeight(16));
            setRightArrowCoordinate(new TextureCoordinate().setX(320).setY(60).setWidth(62).setHeight(16));
            setUpArrowCoordinate(new TextureCoordinate().setX(128).setY(60).setWidth(92).setHeight(16));
            setDownArrowCoordinate(new TextureCoordinate().setX(128).setY(60).setWidth(92).setHeight(16));

            setThemeCoordinate(new TextureCoordinate().setX(300).setY(552).setWidth(40).setHeight(48));
            setThemeHMargin(0);

            setBgUV(new TextureCoordinate().setU0(0).setV0(0).setUWidth(500).setVHeight(600));

            setRewardUV(new TextureCoordinate().setU0(120).setV0(600).setUWidth(40).setVHeight(40));

            setSignedInUV(new TextureCoordinate().setU0(80).setV0(600).setUWidth(40).setVHeight(40));

            setArrowUV(new TextureCoordinate().setU0(0).setV0(600).setUWidth(40).setVHeight(40));
            setArrowHoverUV(new TextureCoordinate().setU0(0).setV0(600).setUWidth(40).setVHeight(40));
            setArrowTapUV(new TextureCoordinate().setU0(40).setV0(600).setUWidth(40).setVHeight(40));

            setThemeUV(new TextureCoordinate().setU0(0).setV0(640).setUWidth(40).setVHeight(48));
            setThemeHoverUV(new TextureCoordinate().setU0(0).setV0(688).setUWidth(40).setVHeight(48));
            setThemeTapUV(new TextureCoordinate().setU0(0).setV0(736).setUWidth(40).setVHeight(48));
        }};
    }
}
