import java.util.Scanner;

public class Managers {
    public static TaskManager getDefault(){

        return new InMemoryTaskManager(getDefaultHistory());
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
