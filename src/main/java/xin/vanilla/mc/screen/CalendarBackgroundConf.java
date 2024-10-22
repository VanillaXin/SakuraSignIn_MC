package xin.vanilla.mc.screen;

import lombok.Data;

import java.io.Serializable;

/**
 * 背景图片坐标配置, 坐标宽高度单位都为像素
 */
@Data
public class CalendarBackgroundConf implements Serializable {
    /**
     * 标题区域X轴起始坐标
     */
    private int titleStartX;
    /**
     * 标题区域Y轴起始坐标
     */
    private int titleStartY;
    /**
     * 标题区域宽度
     */
    private int titleWidth;
    /**
     * 标题区域高度
     */
    private int titleHeight;
    /**
     * 副标题区域X轴起始坐标
     */
    private int subTitleStartX;
    /**
     * 副标题区域Y轴起始坐标
     */
    private int subTitleStartY;
    /**
     * 副标题区域宽度
     */
    private int subTitleWidth;
    /**
     * 副标题区域高度
     */
    private int subTitleHeight;
    /**
     * 日期区域X轴起始坐标
     */
    private int cellStartX;
    /**
     * 日期区域Y轴起始坐标
     */
    private int cellStartY;
    /**
     * 日期单元格宽度
     */
    private int cellWidth;
    /**
     * 日期单元格高度
     */
    private int cellHeight;
    /**
     * 日期单元格左右边距
     */
    private int cellHMargin;
    /**
     * 日期单元格上下边距
     */
    private int cellVMargin;
    /**
     * 左侧翻页按钮X轴起始坐标
     */
    private int leftButtonStartX;
    /**
     * 左侧翻页按钮Y轴起始坐标
     */
    private int leftButtonStartY;
    /**
     * 左侧翻页按钮宽度
     */
    private int leftButtonWidth;
    /**
     * 左侧翻页按钮高度
     */
    private int leftButtonHeight;
    /**
     * 右侧翻页按钮X轴起始坐标
     */
    private int rightButtonStartX;
    /**
     * 右侧翻页按钮Y轴起始坐标
     */
    private int rightButtonStartY;
    /**
     * 右侧翻页按钮宽度
     */
    private int rightButtonWidth;
    /**
     * 右侧翻页按钮高度
     */
    private int rightButtonHeight;
    /**
     * 背景图片宽度
     */
    private int totalWidth;
    /**
     * 背景图片高度
     */
    private int totalHeight;

    public static CalendarBackgroundConf getDefault() {
        return new CalendarBackgroundConf() {{
            setTitleStartX(84);
            setTitleStartY(70);
            setTitleWidth(100);
            setTitleHeight(32);
            setSubTitleStartX(346);
            setSubTitleStartY(76);
            setSubTitleWidth(100);
            setSubTitleHeight(28);
            setCellStartX(61);
            setCellStartY(148);
            setCellWidth(36);
            setCellHeight(36);
            setCellHMargin(22);
            setCellVMargin(24);
            setLeftButtonStartX(409);
            setLeftButtonStartY(466);
            setLeftButtonWidth(11);
            setLeftButtonHeight(11);
            setRightButtonStartX(431);
            setRightButtonStartY(466);
            setRightButtonWidth(11);
            setRightButtonHeight(11);
            setTotalWidth(500);
            setTotalHeight(600);
        }};
    }
}
