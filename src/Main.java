import com.mysql.fabric.jdbc.FabricMySQLDriver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.logging.Logger;

public class Main {








    private static Logger log = Logger.getLogger(Main.class.getName());

    private static WebDriver driver = new ChromeDriver();

    private static JSONArray mainArr;


    // данные для бд
    private final static String login = "root";
    private final static String password = "";
    private final static String url =
            "jdbc:mysql://localhost:3306/seleniumtest?useUnicode=true&characterEncoding=UTF8";
    //инициализируем и запускаем потоки
    private static ThreadForLinks link_thread;
    private static ThreadForPics pics_thread;
    private static ThreadForTexts text_thread;
    private static ThreadForDB db_thread;
    private static ThreadForFileWriting file_thread;




    public static void main(String[] args) {




        JSONParser parse = new JSONParser();

        JSONObject loginobj;


            try {
                loginobj = (JSONObject) parse.parse(new FileReader("/home/nikita/Документы/login.JSON"));

                 String email =  loginobj.get("login").toString();
                 String pass =  loginobj.get("pass").toString();
                 vklogin(email,  pass);



            } catch (IOException | ParseException e) { System.out.println("ну кек"); }







        mainArr = new JSONArray();
        Connection connection;
        Read();
        ClearFile();

//        int j = 0;
//        By myElem = By.className("author");
//        for (int i = 1; i < 1000; i++) {
//            List<WebElement> myElements = driver.findElements(myElem);
//
//            for (WebElement e : myElements) {
//                e.sendKeys(Keys.ARROW_DOWN);
//
//                j++;
//                //  e.sendKeys(Keys.ARROW_DOWN);
//                System.out.print("Имя источника: " + e.getText() + "\n " + j);
//            }
//
//        }
        new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.tagName("a")));
        try {


            //подключаемся к бд
            Driver driversql = new FabricMySQLDriver();
            DriverManager.registerDriver(driversql);
            connection = DriverManager.getConnection(url, login, password);

            if (!connection.isClosed()) {
                log.info("Открыли соединение бд");
                System.out.println("Соединение с базой данных установлено");
            }

            // удаляем все записи в таблицах
            Statement statement = connection.createStatement();
            statement.execute("TRUNCATE TABLE text");
            statement.execute("TRUNCATE TABLE image");
            statement.execute("TRUNCATE TABLE link");
            // ждем, пока элемент не будет виден
            new WebDriverWait(driver, 20).until(ExpectedConditions.presenceOfElementLocated(By.className("wall_post_text")));


            // создаем объекты классов и передаем драйвер в конструкторы классов
            link_thread = new ThreadForLinks(driver);

            text_thread = new ThreadForTexts(driver);

            pics_thread = new ThreadForPics(driver);

            db_thread = new ThreadForDB(connection);

            file_thread = new ThreadForFileWriting();


            //запускаем потоки для парсинга картинок ссылок и текста
            link_thread.start();
            pics_thread.start();
            text_thread.start();

            //ставим их в ....?????
            try {
                link_thread.join();
                pics_thread.join();
                text_thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            file_thread.setDaemon(true);
            file_thread.start();
            db_thread.setDaemon(true);
            db_thread.start();
            try {
                db_thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //закрываем соединение

            connection.close();
            if (connection.isClosed()) {

                System.out.println("Соединение с бд разорвано");
                log.info("Закрыли соединение бд");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void vklogin(String email, String pass) {

        driver.get("https://www.vk.com");
        driver.findElement(By.id("index_email"));
        driver.findElement(By.id("index_email")).sendKeys(email);
        driver.findElement(By.id("index_pass")).sendKeys(pass);
        driver.findElement(By.id("index_login_button"));
        driver.findElement(By.id("index_login_button")).click();
        log.info("parsing vk");


    }






    private static void ClearFile() {
        try {

            FileWriter file = new FileWriter("/home/nikita/IdeaProjects/Parsing/src/etc/file.json");
            BufferedWriter bufferfile = new BufferedWriter(file);
            bufferfile.write("");
            log.info("чистим файл");
            bufferfile.close();
        } catch (IOException ex) {
            //TODO написать исключение
        }
    }




    private static synchronized void Read() {
        try {
            log.info(" парсим и читаем файл");
            JSONParser parse = new JSONParser();
            mainArr = (JSONArray) parse.parse(new FileReader("/home/nikita/IdeaProjects/Parsing/src/etc/file.json"));
            System.out.println(mainArr.get(0));

        } catch (IOException | ParseException e) {
            //TODO написать исключение

        }
    }



    static synchronized void FileWrite() {
        try {
            FileWriter file = new FileWriter("/home/nikita/IdeaProjects/Parsing/src/etc/file.json", true);
            BufferedWriter bufferfile = new BufferedWriter(file);
            bufferfile.write(mainArr.toJSONString());
            bufferfile.close();
        } catch (IOException ex) {
            //TODO написать исключение
        }
    }

    static synchronized void AddList(JSONObject obj) {
        if (!mainArr.contains(obj)) {
            mainArr.add(obj);
            //System.out.println(obj);
        }
    }

}



