import TaskData.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> reviewHistory;
    InMemoryHistoryManager(){
        reviewHistory = new ArrayList<>();
    }
    @Override
    public void add(Task task){
        if(reviewHistory.contains(task)){
            reviewHistory.remove(task);
            reviewHistory.add(task);
        } else {
            reviewHistory.add(task);
        }

        if (reviewHistory.size() > 10){
            reviewHistory.remove(0);
        }
    }
    @Override
    public List<Task> getHistory(){
        return reviewHistory;
    }
}
