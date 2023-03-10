import TaskData.*;

import java.util.ArrayList;

public interface TaskManager {
    public void addRegularTask(Task newTask);
    public void addEpicTask(EpicTask epicTask, ArrayList<SubTask> subTasks);
    public void addSubTask (SubTask newSubTask);
    public void editOrDelete(int taskId);
    public void printAllTasks();
    public void printInProgress();
    public void printAllDone();
    public void deleteAllTasks();
}
