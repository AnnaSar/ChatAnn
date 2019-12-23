package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ChatAnnProp {

//    private int PORT_NUM;
//    private String HOST = "localhost";
//    private String DB_DRIVER = "org.postgresql.Driver";
//    private String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
//    private String DB_USER = "postgres";
//    private String DB_PASSWORD = "fIfugo83";
//    private int CNT_ERRPASS = 3;

     //путь к нашему файлу конфигураций
    public static final String PATH_TO_PROPERTIES = "src/resources/config.properties";

    public static Properties setProp() {

        FileInputStream fileInputStream;
        //инициализируем специальный объект Properties
        //типа Hashtable для удобной работы с данными
        Properties prop = new Properties();

        try {
            //обращаемся к файлу и получаем данные
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);

//            this.setPORT_NUM(Integer. parseInt(prop.getProperty("portnum")));
//            this.setCNT_ERRPASS(Integer. parseInt(prop.getProperty("cnt_err")));
//            this.setHOST(prop.getProperty("host"));
//            this.setDB_DRIVER(prop.getProperty("driver"));
//            this.setDB_URL(prop.getProperty("url"));
//            this.setDB_USER(prop.getProperty("user"));
//            this.setDB_PASSWORD(prop.getProperty("pass"));

        } catch (IOException e) {
            System.out.println("Ошибка в программе: файл " + PATH_TO_PROPERTIES + " не обнаружено");
            e.printStackTrace();
        }
        return prop;
    }
}
