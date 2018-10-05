package dk.agenia.configservice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    private static final String PREFIX = "PROJECT";
    private static final String  TEST_CONFIG_PATH = "/testconfig.properties";
    private static final String  CONFIGSERVICE_CONFIG_PATH = "/configservice.properties";

    private static final String[][] testProperties = new String[][]{
            new String[]{"STRING", "HELLO_MR_CONFIG"},
            new String[]{"INT", "123"},
            new String[]{"DOUBLE", "123.123"},
            new String[]{"BOOLEAN1", "TRUE"},
            new String[]{"BOOLEAN2", "true"},
            new String[]{"BOOLEAN3", "True"},
            new String[]{"BOOLEAN4", ""}
    };
    @BeforeAll
    public static void setup() throws IOException {
       setupConfigService();
       setupTestConfig();
    }

    private static void setupTestConfig() throws IOException {
        Properties props = new Properties();
        FileOutputStream out = new FileOutputStream(new File(ConfigurationTest.class.getResource(TEST_CONFIG_PATH).getFile()));

        for(String[] prop : testProperties){
            String key = prop[0];
            String value = prop[1];
            props.setProperty(PREFIX + "." + key, value);
        }

        props.store(out, "test config properties file for testing config service");
        out.flush();
        out.close();
    }

    public static void setupConfigService()throws IOException{
        Properties props = new Properties();
        FileOutputStream out = new FileOutputStream(new File(ConfigurationTest.class.getResource(CONFIGSERVICE_CONFIG_PATH).getFile()));

        props.setProperty("PREFIX", PREFIX);
        props.setProperty("LOCATION", ConfigurationTest.class.getResource(TEST_CONFIG_PATH).getFile());
        props.store(out, "configservice properties file for tests");
        out.flush();
        out.close();
    }

    @Test
    void get() {
        String value = Configuration.get("STRING");
        assertTrue("HELLO_MR_CONFIG".equals(value), "test if any value is found");
    }

    @Test
    void getInt() {
        int value = Configuration.getInt("INT");
        assertTrue(value == 123, "test if int is read correctly");
    }

    @Test
    void getDouble() {
        double value = Configuration.getDouble("DOUBLE");
        assertTrue(value == 123.123, "test if double is read correctly");
    }

    @Test
    void getBool() {
        boolean value1 = Configuration.getBool("BOOLEAN1"); //TRUE
        boolean value2 = Configuration.getBool("BOOLEAN2"); // true
        boolean value3 = Configuration.getBool("BOOLEAN3"); // True
        boolean value4 = Configuration.getBool("BOOLEAN4", true); // "" empty
        boolean value5 = Configuration.getBool("BOOLEAN4", false); // "" empty

        assertTrue(value1, "test if bool is read correctly");
        assertTrue(value2, "test if bool is read correctly");
        assertTrue(value3, "test if bool is read correctly");
        assertTrue(value4, "test if bool is read correctly");
        assertTrue(!value5, "test if bool is read correctly");
    }
}