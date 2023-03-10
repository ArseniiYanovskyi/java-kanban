import java.util.Scanner;

public class Managers {
    public static TaskManager getDefault(){
        return new InMemoryTaskManager(new Scanner(System.in));
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
