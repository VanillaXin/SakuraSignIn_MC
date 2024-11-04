package xin.vanilla.mc.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.NonNull;
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
import java.util.LinkedHashMap;

import static net.minecraft.util.datafix.fixes.SignStrictJSON.GSON;

public class SignInDataManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String FILE_NAME = "sign_in_data.json";
    @Getter
    @NonNull
    private static SignInData signInData = new SignInData();

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
                JsonObject jsonObject = GSON.fromJson(jsonString, JsonObject.class);
                signInData.setBaseRewards(GSON.fromJson(jsonObject.get("baseRewards"), new TypeToken<RewardList>() {
                }.getType()));
                signInData.setContinuousRewards(GSON.fromJson(jsonObject.get("continuousRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                signInData.setCycleRewards(GSON.fromJson(jsonObject.get("cycleRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                signInData.setYearRewards(GSON.fromJson(jsonObject.get("yearRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                signInData.setMonthRewards(GSON.fromJson(jsonObject.get("monthRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                signInData.setWeekRewards(GSON.fromJson(jsonObject.get("weekRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                signInData.setDateTimeRewards(GSON.fromJson(jsonObject.get("dateTimeRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
            } catch (IOException | JsonSyntaxException | JsonIOException e) {
                LOGGER.error("Error loading sign-in data: ", e);
            }
        } else {
            // 如果文件不存在，初始化默认值
            signInData = SignInData.getDefault();
            SignInDataManager.saveSignInData();
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
            Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
            writer.write(gson.toJson(signInData.toJsonObject()));
        } catch (IOException e) {
            LOGGER.error("Error saving sign-in data: ", e);
        }
    }

}
