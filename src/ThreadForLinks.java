import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public class ThreadForLinks extends Thread {
    private WebDriver driver;


    // передаем объект драйвер в конструктор
    ThreadForLinks(WebDriver driver){
        this.driver = driver;
    }


    private static Logger log = Logger.getLogger(ThreadForLinks.class.getName());




    @Override
    public void run() {
        log.info("запущен поток парсинга ссылок");


        JSONObject MainObj;
        for (WebElement element : driver.findElements(By.className("feed_row"))) {
            WebElement id;
            WebElement text;
            JSONArray list = new JSONArray();
            try {
                id = element.findElement(By.className("post"));
                text = element.findElement(By.className("wall_post_text"));
                Arrays.stream(text.findElements(By.tagName("a")).toArray())
                        .filter(Objects::nonNull)
                        .forEach(x -> list.add(((WebElement) x).getAttribute("href")));
                if (list.isEmpty()) {
                    continue;
                }
                MainObj = PreFileWrite(id.getAttribute("data-post-id"), list);
                Main.AddList(MainObj);
            } catch (NoSuchElementException | java.util.NoSuchElementException e) {
                System.out.println("Ссылок не обнаружено");
            }
        }

    }











    private JSONObject PreFileWrite(String id, JSONArray link) {
        JSONObject Obj = new JSONObject();
        JSONArray Link;
        String Id;
        Link = link;
        Id = id;
        Obj.put("Post link", "https://vk.com/feed?w=wall" + Id);
        Obj.put("Links in post", Link);
        return (Obj);
    }
}