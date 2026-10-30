package app.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class SettingsService {

    private static final String SETTINGS_PATH =
            "./Publisher/res/settings.properties";

    public static void save(Properties props) {

        try {

            File file = new File(SETTINGS_PATH);

            file.getParentFile().mkdirs();

            FileOutputStream fos = new FileOutputStream(file);

            props.store(fos, "Publisher UI Settings");

            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Properties load() {

        Properties props = new Properties();

        File file = new File(SETTINGS_PATH);

        if (!file.exists()) {
            return props;
        }

        try {

            FileInputStream fis = new FileInputStream(file);

            props.load(fis);

            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return props;
    }
}