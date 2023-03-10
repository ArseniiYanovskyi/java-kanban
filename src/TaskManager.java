import TaskData.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    public void addRegularTask(Task newTask);
    public void addEpicTask(EpicTask epicTask, ArrayList<SubTask> subTasks);
    public void addSubTask (SubTask newSubTask);
    public void editOrDelete(int taskId);
    public void printAllTasks();
    public void printInProgress();
    public void printAllDone();
    public void deleteAllTasks();
    public void getTaskInfoById(int id);
    public void getTask(int id);
    public void getEpic(int id);
    public void getSubtask(int id);
    public void historyView();
}
