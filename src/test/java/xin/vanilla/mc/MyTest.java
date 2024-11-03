package xin.vanilla.mc;

import org.junit.Test;
import xin.vanilla.mc.screen.CalendarTextureCoordinate;
import xin.vanilla.mc.util.DateUtils;
import xin.vanilla.mc.util.PNGUtils;
import xin.vanilla.mc.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class MyTest {

    @Test
    public void dateTest() {
        System.out.println(DateUtils.calculateContinuousDays(new ArrayList<Date>() {{
            add(DateUtils.getDate(2024, 10, 31));
            add(DateUtils.getDate(2024, 10, 30));
            add(DateUtils.getDate(2024, 10, 29));
            add(DateUtils.getDate(2024, 10, 28));
            add(DateUtils.getDate(2024, 10, 20));
            add(DateUtils.getDate(2024, 10, 19));
            add(DateUtils.getDate(2024, 10, 18));
            add(DateUtils.getDate(2024, 10, 17));
        }}, new Date()));
    }

    private static final String themeName = "clover";

    private static void testWriteZTxt(String fileName) {
        try {
            File sourceFile;
            File targetFile;
            if (StringUtils.isNullOrEmpty(fileName)) {
                fileName = themeName;
            }
            sourceFile = new File("src/main/resources/assets/sakura_sign_in/textures/gui/sign_in_calendar_" + fileName + "_source.png");
            targetFile = new File("src/main/resources/assets/sakura_sign_in/textures/gui/sign_in_calendar_" + fileName + ".png");
            PNGUtils.writeZTxt(sourceFile, targetFile, new LinkedHashMap<String, String>() {{
                put("titleStartX", "20");
                put("titleStartY", "20");
                put("titleWidth", "50");
                put("titleHeight", "20");
                put("subTitleStartX", "20");
                put("subTitleStartY", "20");
                put("subTitleWidth", "50");
                put("subTitleHeight", "20");
                put("cellStartX", "20");
                put("cellStartY", "40");
                put("cellWidth", "14");
                put("cellHeight", "14");
                put("cellHMargin", "12");
                put("cellVMargin", "8");
                put("leftButtonStartX", "20");
                put("leftButtonStartY", "20");
                put("leftButtonWidth", "20");
                put("leftButtonHeight", "20");
                put("rightButtonStartX", "20");
                put("rightButtonStartY", "20");
                put("rightButtonWidth", "20");
                put("rightButtonHeight", "20");
                put("totalWidth", "20");
                put("totalHeight", "20");
                put("Software", "Minecraft SakuraSignIn");
            }});
            System.out.println(PNGUtils.readAllZTxt(targetFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void testWriteChunk(String fileName) throws IOException, ClassNotFoundException {
        File sourceFile;
        File targetFile;
        if (StringUtils.isNullOrEmpty(fileName)) {
            fileName = themeName;
        }
        sourceFile = new File("src/main/resources/assets/sakura_sign_in/textures/gui/sign_in_calendar_" + fileName + "_source.png");
        targetFile = new File("src/main/resources/assets/sakura_sign_in/textures/gui/sign_in_calendar_" + fileName + ".png");
        File tempFile = new File("src/main/resources/assets/sakura_sign_in/textures/gui/checkin_background_temp.png");
        CalendarTextureCoordinate aDefault = CalendarTextureCoordinate.getDefault();
        switch (fileName) {
            case "sakura":
                aDefault.getCellCoordinate().setX(70).setY(128).setWidth(34).setHeight(34);
                aDefault.setCellHMargin(20);
                aDefault.setCellVMargin(24);
                aDefault.setTextColorDefault(0xFFFFD9E2);
                aDefault.setTextColorCurrent(0xFFF27690);
                aDefault.setTextColorCanRepair(0xFFFFFFBB);
                aDefault.setTextColorToday(0xFFE03D63);
                aDefault.setTextColorDate(0xFF000000);
                break;
            case "clover":
                aDefault.getCellCoordinate().setX(80);
                aDefault.setTextColorDefault(0xFFEFCE8B);
                aDefault.setTextColorCurrent(0xFF426812);
                aDefault.setTextColorCanRepair(0xFFEFCE8B);
                aDefault.setTextColorToday(0xFFB2D87C);
                aDefault.setTextColorDate(0xFF000000);
                break;
            case "maple":
                aDefault.setTextColorDefault(0xFFFFAA85);
                aDefault.setTextColorCurrent(0xFFCE4906);
                aDefault.setTextColorCanRepair(0xFFFFFCD2);
                aDefault.setTextColorToday(0xFFFF4E00);
                aDefault.setTextColorDate(0xFF000000);
                break;
            case "chaos":
                aDefault.setWeekStart(1);
                break;
        }
        PNGUtils.writePrivateChunk(sourceFile, tempFile, "vacb", aDefault, true);
        PNGUtils.writeZTxtByKey(tempFile, targetFile, "Software", "Minecraft SakuraSignIn");
        tempFile.deleteOnExit();
        CalendarTextureCoordinate backgroundConf = PNGUtils.readLastPrivateChunk(targetFile, "vacb");
        System.out.println(backgroundConf);
    }

    @Test
    public void pngTest() throws IOException, ClassNotFoundException {
        // testWriteZTxt("original");
        testWriteChunk("original");
        testWriteChunk("sakura");
        testWriteChunk("clover");
        testWriteChunk("maple");
        testWriteChunk("chaos");
    }
}
