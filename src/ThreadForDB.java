import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;

public class ThreadForDB extends Thread {

    private static Logger log = Logger.getLogger(ThreadForDB.class.getName());
     private  Connection connection;

     ThreadForDB(Connection connection) {
        this.connection = connection;
    }


        // статья на хабре про preparestatment и JDBC
        //  https://habr.com/sandbox/41444/


    @Override
    public void run() {
        log.info("запущен поток для БД");
        DBWriteText();
        super.run();
    }

    private void SetStringer(PreparedStatement ps, String id, String value){
        try {
            ps.setString(1, id);
            ps.setString(2, value);
            ps.execute();
        } catch (SQLException e) {  }


    }
    private void SetStringerLinksPics(PreparedStatement state, String id, JSONArray value){

        try {
            // передали параметры , создаем объект стринг билдер
            StringBuilder Value = new StringBuilder();
            for (Object item : value) {
                Value.append((String) item).append(" ");
            }
            state.setString(1, id);
            state.setString(2, Value.toString());
            state.execute();
        } catch (SQLException e) {
            //TODO написать исключение
        }


    }




     private void DBWriteText(){
         JSONParser parse = new JSONParser();

         try {

             //  готовим выражения для вставки значений в таблицы
             PreparedStatement psText = connection.prepareStatement("INSERT INTO text (post_link, post_text) VALUES (?, ?)");
             PreparedStatement psLink = connection.prepareStatement("INSERT INTO link (post_link, links_in_post) VALUES (?, ?)");
             PreparedStatement psImage = connection.prepareStatement("INSERT INTO image (post_link, pics_in_post) VALUES (?, ?)");


             //
             JSONArray arr = (JSONArray) parse.parse(new FileReader("/home/nikita/IdeaProjects/Parsing/src/etc/file.json"));










             Arrays.stream(arr.toArray())

                     // фильтруем записи без текста
                     .filter(o ->((JSONObject) o).get("Post text") != "" )
                     // кидаем в функцию   выражение бд, айди,  текст записи
                     .forEach(o -> SetStringer(psText, (String) ((JSONObject) o).get("Post link"), (String) ((JSONObject) o).get("Post text"))   );







             try {
                 Arrays.stream(arr.toArray())
                         .filter(o -> ((JSONObject) o).get("Links in post") != null)
                         .forEach(o -> SetStringerLinksPics(psLink,(String) ((JSONObject) o).get("Post link"), (JSONArray) ((JSONObject) o).get("Links in post")));
             } catch (NullPointerException e) { System.out.println("Пустое множество  записи ссылок"); }






             try {
                 Arrays.stream(arr.toArray())
                         .filter(o -> ((JSONObject) o).get("Pics in post") != null)
                         .forEach(o -> SetStringerLinksPics(psImage, (String) ((JSONObject) o).get("Post link"), (JSONArray) ((JSONObject) o).get("Pics in post")));
             } catch (NullPointerException e) {
                 //System.out.println("Пустое множество в Image");
             }












         } catch (SQLException | IOException | ParseException e) { e.printStackTrace(); }


     }













//
//
//    private  void  DBWrite(){
//        JSONParser parse = new JSONParser();
//        try {
//
//            // подготавливаем выражения для вставки значений в бд
//            PreparedStatement StatementText = connection.prepareStatement("INSERT INTO text (post_id, text) VALUES (?, ?)");
//            PreparedStatement StatementLink = connection.prepareStatement("INSERT INTO link (post_id, link) VALUES (?, ?)"); // \n
//
//            //читаем данные из файла  и парсим джейсон массив
//            JSONArray arr = (JSONArray) parse.parse(new FileReader(""));
//
//            Arrays.stream(arr.toArray())
//                    .filter(o -> ((JSONObject) o).get("Text") != "");
//                   // .forEach(o -> Write(StatementText, (String) ((JSONObject) o).get("Id"), (String) ((JSONObject) o).get("Text")));
//
//
//            try {
//                Arrays.stream(arr.toArray())
//                        .filter(o -> ((JSONObject) o).get("Link") != null);
//                       // .forEach(o -> Write(StatementLink, (String) ((JSONObject) o).get("Id"), (JSONArray) ((JSONObject) o).get("Link")));
//            } catch (NullPointerException e) {
//                //System.out.println("Пустое множество в Link");
//            }
//
//        } catch (IOException | ParseException | SQLException e) {
//            //TODO написать исключение
//        }
//
//
//
//
//
//    }



}
