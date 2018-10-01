import java.io.FileWriter;
import java.util.logging.Logger;

public class ThreadForFileWriting extends Thread{
    private static Logger log = Logger.getLogger(ThreadForFileWriting.class.getName());
    @Override
    public void run() {
        log.info("запущен поток для записи в файл");
        Main.FileWrite();
        super.run();
    }
}
