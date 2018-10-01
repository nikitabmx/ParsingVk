import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public class ThreadForPics extends Thread {
    private static Logger log = Logger.getLogger(ThreadForPics.class.getName());
    private WebDriver driver;

    public ThreadForPics(WebDriver driver) {
       this.driver = driver;
    }


    @Override
    public void run() {
        log.info("запущен поток парсинга картинок");


        JSONObject MainObj;
        for (WebElement element : driver.findElements(By.className("feed_row"))) {
            WebElement id;
            JSONArray list = new JSONArray();
            try {
                id = element.findElement(By.className("post"));
                Arrays.stream(element.findElements(By.className("image_cover")).toArray())
                        .filter(Objects::nonNull)
                        .forEach(x -> list.add((GetPhotosName(GetURL(((WebElement) x).getAttribute("style"))))));
                if (list.isEmpty()) continue;
                MainObj = PreFileWrite(id.getAttribute("data-post-id"), list);
                Main.AddList(MainObj);
            } catch (NoSuchElementException e) {
                System.out.println("Нет картинок");
            }
        }

    }





    private JSONObject PreFileWrite(String id, JSONArray list) {
        JSONObject Obj = new JSONObject();
        JSONArray imagename;
        String PicId;
        imagename = list;
        PicId = ("https://vk.com/feed?w=wall").concat(id);
        Obj.put("Post link",  PicId);
        Obj.put("Pics in post", imagename);
        return (Obj);
    }

    private String GetURL(String str) {
        int n = 0;
        int m = 0;
        StringBuilder newstr = new StringBuilder();
        for (int i = 0; i <= str.length(); i++) {
            if (str.charAt(i) == '"') {
                n = i + 1;
                break;
            }
        }
        for (int i = n; i <= str.length(); i++) {
            if (str.charAt(i) == '"') {
                m = i - 1;
                break;
            }
        }
        for (int i = n; i <= m; i++) {
            newstr.append(str.charAt(i));
        }
        LoadPhoto(newstr.toString());
        return (newstr.toString());
    }


    private String GetPhotosName(String Url) {
        StringBuilder newname = new StringBuilder();
        int count = 0;
        int n = 0;
        for (int i = 0; i <= Url.length(); i++) {
            if (Url.charAt(i) == '/') {
                count++;
            }
            if (count == 6) {
                n = i + 1;
                break;
            }
        }
        for (int i = n; i < Url.length(); i++) {
            newname.append(Url.charAt(i));
        }
        return (newname.toString());
    }

    private void LoadPhoto(String url) {
        try {
            String fileName;
            fileName = GetPhotosName(url);
            BufferedImage img = ImageIO.read(new URL(url));
            File file = new File("/home/nikita/IdeaProjects/Parsing/src/Pics/" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            ImageIO.write(img, "jpg", file);
        } catch (Exception e) {
            //TODO написать исключение
        }
    }








}