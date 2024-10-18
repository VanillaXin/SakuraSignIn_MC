package xin.vanilla.mc.config;

import com.alibaba.fastjson2.*;
import lombok.Getter;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.rewards.RewardList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class SignInDataManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String FILE_NAME = "sign_in_data.json";
    @Getter
    private static SignInData signInData;

    /**
     * 获取配置文件路径
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve(SakuraSignIn.MODID);
    }

    /**
     * 加载 JSON 数据
     */
    public static void loadSignInData() {
        File file = new File(getConfigDirectory().toFile(), FILE_NAME);
        if (file.exists()) {
            try {
                String jsonString = new String(Files.readAllBytes(Paths.get(file.getPath())));
                signInData = new SignInData();
                JSONObject jsonObject = JSON.parseObject(jsonString);
                signInData.setBaseRewards(jsonObject.getObject("baseRewards", new TypeReference<RewardList>() {
                }));
                signInData.setContinuousRewards(jsonObject.getObject("continuousRewards", new TypeReference<Map<String, RewardList>>() {
                }));
                signInData.setCycleRewards(jsonObject.getObject("cycleRewards", new TypeReference<Map<String, RewardList>>() {
                }));
                signInData.setYearRewards(jsonObject.getObject("yearRewards", new TypeReference<Map<String, RewardList>>() {
                }));
                signInData.setMonthRewards(jsonObject.getObject("monthRewards", new TypeReference<Map<String, RewardList>>() {
                }));
                signInData.setWeekRewards(jsonObject.getObject("weekRewards", new TypeReference<Map<String, RewardList>>() {
                }));
                signInData.setDateTimeRewards(jsonObject.getObject("dateTimeRewards", new TypeReference<Map<String, RewardList>>() {
                }));
            } catch (IOException | JSONException e) {
                LOGGER.error("Error loading sign-in data: ", e);
            }
        } else {
            // 如果文件不存在，初始化默认值
            signInData = SignInData.getDefault();
            saveSignInData();
        }
    }

    /**
     * 保存 JSON 数据
     */
    public static void saveSignInData() {
        File dir = getConfigDirectory().toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, FILE_NAME);
        try (FileWriter writer = new FileWriter(file)) {
            // 格式化输出
            writer.write(JSON.toJSONString(signInData, JSONWriter.Feature.PrettyFormat));
        } catch (IOException e) {
            LOGGER.error("Error saving sign-in data: ", e);
        }
    }

}
