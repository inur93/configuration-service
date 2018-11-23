package dk.agenia.configservice;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

/**
 * Created: 11-04-2018
 * Author: Runi
 */

@Slf4j
public class Configuration {


    private static final String CONFIG_LOCATION;
    private static final String CONFIG_PREFIX;
    private Properties configs;
    private static final boolean USE_ENVIRONMENT_VARIABLES;

    static {
        Properties props = new Properties();
        InputStream is = Configuration.class.getResourceAsStream("/configservice.properties");
        String location = null;
        String prefix = "";
        boolean useEnvVar = false;
        try {
            props.load(is);
            location = props.getProperty("LOCATION");
            useEnvVar = "true".equals(props.getProperty("USE_ENVIRONMENT_VARIABLES"));
            prefix = props.getProperty("PREFIX");
            if(prefix != null && prefix.length() > 0){
                if(!prefix.endsWith(".")){
                    prefix = prefix + ".";
                }
            }
            if(prefix == null) prefix = "";
        } catch (IOException e) {
           log.error("Configuration service is missing its config file", e);
           useEnvVar = true;
        }
        CONFIG_LOCATION = location;
        CONFIG_PREFIX = prefix;
        USE_ENVIRONMENT_VARIABLES = useEnvVar;
    }

    private static Configuration instance;
    private static Configuration getInstance(){
        if(instance == null) instance = new Configuration();
        return instance;
    }



    private Configuration(){
        this.configs = new Properties();

        InputStream is = null;
        try{
            if(CONFIG_LOCATION != null && !USE_ENVIRONMENT_VARIABLES) {
                is = new FileInputStream(new File(CONFIG_LOCATION));
                configs.load(is);
            }
        }catch (IOException e){
            log.error("configuration file not found: " + CONFIG_LOCATION, e);
        }
    }

    public static String get(String key){
        return get(key, null);
    }

    public static int getInt(String key){
        return getInt(key, 0);
    }

    public static double getDouble(String key){
        return getDouble(key, 0);
    }

    public static boolean getBool(String key){
        return getBool(key, false);
    }



    public static String get(String key, String defaultValue){
        String val = USE_ENVIRONMENT_VARIABLES ? System.getenv(CONFIG_PREFIX + key)
                : getInstance().configs.getProperty(CONFIG_PREFIX + key);
        return val == null || val.length() == 0 ? defaultValue : val;
    }

    public static int getInt(String key, int defaultValue){
        String val = get(key, String.valueOf(defaultValue));
        try {
            return Integer.valueOf(val);
        }catch (Exception e){
            return defaultValue;
        }
    }

    public static double getDouble(String key, double defaultValue){
        String val = get(key, String.valueOf(defaultValue));
        try {
            return Double.valueOf(val);
        }catch (Exception e){
            return defaultValue;
        }
     }

    /**
     *
     * @param key - configuration key to be read from properties file
     * @param defaultValue - if value for key is null or empty this will be returned.
     * @return true if value is 'true' or if value is null and defaultValue is true, otherwise false.
     */
    public static boolean getBool(String key, boolean defaultValue){
        String val = get(key, String.valueOf(defaultValue));
        try {
            return Boolean.valueOf(val);
        }catch (Exception e){
            return defaultValue;
        }
    }

}
