package com.me.dm.i18n.translation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import com.me.dm.i18n.translation.model.PropertiesManager;

public class PreferencesManager {
    public final static String FILES_DIRECTORY = "files.directory";
    
    private final PropertiesManager propertiesManager;
    private Properties applicationProperties;
    private final String preferencesFileLocation = System.getProperty("user.home") + "/translations-preferences.conf";

    public PreferencesManager(PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }
    
    public void loadPreferences() throws PreferencesException {
        File f = new File(preferencesFileLocation);
        if (!f.exists()) {
            applicationProperties = new Properties();
        } else {
            try {
                applicationProperties = propertiesManager.readProperties(new File(preferencesFileLocation));
            } catch (TranslationException ex) {
                throw new PreferencesException("Unable to load preferences", ex);
            }
        }
    }
    
    public void savePreferences() throws PreferencesException {
        File f = new File(preferencesFileLocation);
        if (applicationProperties.isEmpty()) {
            return;
        }
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            applicationProperties.store(new FileOutputStream(f), "Translation Application Preferences");
        } catch (IOException ex) {
            throw new PreferencesException("Unable to save preferences", ex);
        }
    }

    public void setPreference(String key, String value) {
        applicationProperties.setProperty(key, value);
    }

    public String getPreference(String key) {
        return applicationProperties.getProperty(key);
    }
    
}
