package models;

import TaskData.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    public void addRegularTask(Task newTask);
    public void addEpicTask(EpicTask epicTask, ArrayList<SubTask> subTasks);
    public void addEmptyEpicTask(EpicTask epicTask);
    public void addSubTask (SubTask newSubTask);

    public void editRegularTask(Task editedTask);
    public void editEpicTask(EpicTask epicTask);
    public void editSubTask(SubTask subTask);
    public void updateEpicTaskStatus(int taskId);


    public void deleteTaskByID(int taskId);
    public void deleteAllRegularTask();
    public void deleteAllEpicTasks();
    public void deleteAllSubTasks();
    public void deleteAllTasks();

    public void printAllTasks();
    public void printInProgress();
    public void printAllDone();

    public Task getTask(int id);
    public EpicTask getEpic(int id);
    public SubTask getSubtask(int id);

    public TreeSet<Task> getPrioritizedTasks();

    public List<Task> getHistory();
}
