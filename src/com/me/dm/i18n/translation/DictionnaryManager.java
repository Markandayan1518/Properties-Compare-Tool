package com.me.dm.i18n.translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DictionnaryManager {

  private final String preferencesFileLocation = System.getProperty("user.home") + "/translations-dictionnary.conf";
  private Set<String> dictionnary;

  public void loadDictionnary() {
    File f = new File(preferencesFileLocation);
    if (f.exists()) {
      try {
        dictionnary = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String entry = br.readLine();
        do {
          dictionnary.add(entry);
          entry = br.readLine();
        }
        while (entry != null);
      } catch (IOException ex) {
      }
    } else {
      dictionnary = Collections.EMPTY_SET;
    }
  }

  public boolean isInDictionnary(String entry) {
    return dictionnary.contains(entry);
  }

}
