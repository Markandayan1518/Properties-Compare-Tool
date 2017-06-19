package com.me.dm.i18n.translation.model;

import com.me.dm.i18n.translation.DictionnaryManager;
import com.me.dm.i18n.translation.PreferencesException;
import com.me.dm.i18n.translation.TranslationException;
import com.me.dm.i18n.translation.gui.Application;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertiesManager {

    private int added;
    private int delete;
    private int nonTranslated;

    private final DictionnaryManager dictionnaryManager;
    
    public PropertiesManager(DictionnaryManager dictionnaryManager) {
        this.dictionnaryManager = dictionnaryManager;
        this.added = 0;
        this.delete = 0;
        this.nonTranslated = 0;
    }
    
    public Properties readProperties(File f) throws TranslationException {
        Properties p = new Properties();
        try(FileReader reader = new FileReader(f)) {
            p.load(reader);
        } catch (IOException ex) {
            throw new TranslationException("Unable to parse file", ex);
        }
        return p;
    }
    
    public List<PropertyTableModel.Property> loadProperties(Properties props) {
        return props.entrySet()
            .stream()
            .filter(p -> !dictionnaryManager.isInDictionnary((String) p.getValue()))
            .sorted((p1,p2) -> {return p1.getKey().toString().compareToIgnoreCase(p2.getKey().toString());})
            .map(p -> new PropertyTableModel.Property((String) p.getKey(), (String) p.getValue(), null, com.me.dm.i18n.translation.gui.Action.NONE))
            .collect(Collectors.toList());
    }

    public List<PropertyTableModel.Property> diff(Properties fromProps, Properties toProps) {
        List<PropertyTableModel.Property> result = new LinkedList<>();
        Set<Object> insertedkeys = new HashSet<>(fromProps.size());
        fromProps.entrySet().stream()
            .filter(p -> !dictionnaryManager.isInDictionnary((String) p.getValue()))
            .forEach(p -> {
                String toValueAsString = (String)toProps.get(p.getKey());
                final String fromValueAsString = (String)p.getValue();
                if (toValueAsString == null) {
                    this.delete++;
                    result.add(new PropertyTableModel.Property((String)p.getKey(), fromValueAsString, null, com.me.dm.i18n.translation.gui.Action.DELETED));
                } else if (fromValueAsString.trim().equalsIgnoreCase(toValueAsString.trim())) {
                    this.nonTranslated++;
                    result.add(new PropertyTableModel.Property((String)p.getKey(), (String)p.getValue(), toValueAsString, com.me.dm.i18n.translation.gui.Action.UNTRANSLATED));
                } else if (!insertedkeys.contains(p.getKey())) {
                    insertedkeys.add(p.getKey());
                    result.add(new PropertyTableModel.Property((String)p.getKey(), fromValueAsString, toValueAsString, com.me.dm.i18n.translation.gui.Action.NONE));
                }
            });
        toProps.entrySet().stream()
            .filter(p -> !dictionnaryManager.isInDictionnary((String) p.getValue()))
            .forEach(p -> {
                String fromValueAsString = (String)fromProps.get(p.getKey());
                if (fromValueAsString == null) {
                    this.added++;
                    result.add(new PropertyTableModel.Property((String)p.getKey(), null, (String)p.getValue(), com.me.dm.i18n.translation.gui.Action.ADDED));
                }
            });
        Collections.sort(result);
        return result;
    }
    
    public void export(Application application, PropertyTableModel tableModel) throws PreferencesException {
        String newLine = System.getProperty("line.separator");
        File f = new File(System.getProperty("java.io.tmpdir") + "/translations-export.csv"); 
        try (FileWriter fw = new FileWriter(f)) {
            for (int i=0; i<tableModel.getRowCount(); i++) {
                final PropertyTableModel.Property prop = tableModel.getModel(i);
                fw.append(prop.getKey()).append(';');
                if (prop.getValueFrom() != null) {
                    fw.append(prop.getValueFrom().replace("\n", "<LINEBREAK>")).append(';');
                }
                if (prop.getValueTo() != null) {
                    fw.append(prop.getValueTo().replace("\n", "<LINEBREAK>")).append(';');
                }
                fw.append(newLine);
            }
            JOptionPane.showMessageDialog(application, "Result has been successfully exported : "+f.getAbsolutePath());
        } catch(Exception ex) {
            throw new PreferencesException("Impossible to export data", ex);
        }
    }

    public void showDiffMsg(Application application){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n Deleted keys        :  \t"+ delete);
        stringBuilder.append("\n Added keys          :  \t"+ added);
        stringBuilder.append("\n Not Translated keys :  \t"+ nonTranslated);
        JOptionPane.showMessageDialog(application, stringBuilder.toString());
    }

    public int getAdded() {
        return added;
    }

    public int getDelete() {
        return delete;
    }

    public int getNonTranslated() {
        return nonTranslated;
    }
}
