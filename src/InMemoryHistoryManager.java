import TaskData.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    public static final int MAX_HISTORY_SIZE = 10;
    private LinkedList<Task> reviewHistory;
    public InMemoryHistoryManager(){
        reviewHistory = new LinkedList<>();
    }
    @Override
    public void add(Task task){
        reviewHistory.add(task);
        if (reviewHistory.size() > MAX_HISTORY_SIZE){
            reviewHistory.removeFirst();
        }
    }
    @Override
    public List<Task> getHistory(){
        return (List<Task>) reviewHistory.clone();
    }
}