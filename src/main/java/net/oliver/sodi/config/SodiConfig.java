package net.oliver.sodi.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SodiConfig {

  private static Properties props = new Properties();

  static{

      File file = new File("./sodi-config.properties");
      System.out.println(file.getAbsolutePath());
      try {
          props.load(new FileReader(file));
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public static String getValue(String key)
  {
     return (String) props.get(key);
  }

}
