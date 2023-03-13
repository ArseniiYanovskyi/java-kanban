import TaskData.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    public void addRegularTask(Task newTask);
    public void addEpicTask(EpicTask epicTask, ArrayList<SubTask> subTasks);
    public void addSubTask (SubTask newSubTask);

    public void editTask(Task editedTask);
    public void updateEpicTaskStatus(int taskId);


    public void deleteTaskByID(int taskId);
    public void deleteAllRegularTask();
    public void deleteAllEpicTasks();
    public void deleteAllSubTasks();
    public void deleteAllTasks();

    public void printAllTasks();
    public void printInProgress();
    public void printAllDone();
    public void getTaskInfoById(int id);

    public boolean isContains(int taskId);

    public Task getTask(int id);
    public EpicTask getEpic(int id);
    public SubTask getSubtask(int id);

    public List<Task> getHistory();
}
