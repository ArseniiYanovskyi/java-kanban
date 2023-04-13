import java.io.IOException;
import java.util.Scanner;

public class Managers {
    public static TaskManager getDefault(){
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getFileBacked(String dataFile){
        return new FileBackedTaskManager(getDefaultHistory(), dataFile);
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
