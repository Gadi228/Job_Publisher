package app.service;

import app.model.ProgressStep;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ProgressConfigService {

    private static final String CONFIG_PATH =
            "./Publisher/res/progressbar.properties";

    /**
     * Loads progress steps from the config file.
     * Steps are returned in order (step.1, step.2, ...).
     * Falls back to built-in defaults if the file is missing or unreadable.
     */
    public static List<ProgressStep> loadSteps() {

        Properties props = new Properties();

        File file = new File(CONFIG_PATH);

        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                props.load(new java.io.InputStreamReader(fis, "UTF-8"));
                fis.close();
            } catch (Exception e) {
                // fall through to defaults
            }
        }

        // Count how many steps are defined
        List<ProgressStep> steps = new ArrayList<ProgressStep>();

        if (props.isEmpty()) {
            return buildDefaults();
        }

        int i = 1;
        while (true) {
            String trigger = props.getProperty("step." + i + ".trigger");
            String label   = props.getProperty("step." + i + ".label");

            if (trigger == null || label == null) {
                break;
            }

            steps.add(new ProgressStep(trigger.trim(), label.trim()));
            i++;
        }

        if (steps.isEmpty()) {
            return buildDefaults();
        }

        return steps;
    }

    private static List<ProgressStep> buildDefaults() {

        List<ProgressStep> steps = new ArrayList<ProgressStep>();

        steps.add(new ProgressStep("Clear folders",                  "Очистка временных файлов"));
        steps.add(new ProgressStep("Load pm files",                  "Загрузка PM"));
        steps.add(new ProgressStep("Add to PM applic and issueInfo", "Настройка PM"));
        steps.add(new ProgressStep("Copy logo images",               "Копирование изображений"));
        steps.add(new ProgressStep("Transform UPI to EXCEL",         "Преобразование UPI в EXCEL"));
        steps.add(new ProgressStep("Create DM List",                 "Получение списка DM"));
        steps.add(new ProgressStep("Preprocess DM",                  "Обработка DM"));
        steps.add(new ProgressStep("Create IF file",                 "Создание IF file"));
        steps.add(new ProgressStep("Preparing PDF for image mapping","Обработка pdf для хотспотов"));
        steps.add(new ProgressStep("Print pdf",                      "Публикация конечного pdf"));
        steps.add(new ProgressStep("Get checkSum",                   "Получение чексуммы"));

        return steps;
    }
}