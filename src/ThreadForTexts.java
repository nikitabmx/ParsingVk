import org.json.simple.JSONObject;
import org.openqa.selenium.*;

import java.util.logging.Logger;

public class ThreadForTexts extends Thread {
    private static Logger log = Logger.getLogger(ThreadForTexts.class.getName());
    private WebDriver driver;


    public ThreadForTexts(WebDriver driver) {
        this.driver = driver;
    }

    @Override
        public void run () {

            log.info("запущен поток парсинга текста");
            JSONObject MainObj;

            for (WebElement element : driver.findElements(By.className("feed_row"))) {
                WebElement id;
                WebElement text;

                try {
                    id = element.findElement(By.className("post"));
                    text = element.findElement(By.className("wall_post_text"));

                    // кидаем в функцию PreFileWriter( id   data post id)
                    MainObj = PreFileWrite(text.getText(), id.getAttribute("data-post-id"));


                    // добавляем в общий массив
                    Main.AddList(MainObj);


                } catch (NoSuchElementException | java.util.NoSuchElementException e) {
                    //System.out.println("Нет текста");
                }
            }

        }



    private JSONObject PreFileWrite(String wall_post_text, String id) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Post link", "https://vk.com/feed?w=wall" + id);
        jsonObject.put("Post text", wall_post_text);

        return (jsonObject);
    }

}