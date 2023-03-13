import TaskData.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class InMemoryTaskManager implements TaskManager{
    private HashMap<Integer, Task> tasksData;
    private Scanner scanner;
    private int idCounter;
    private HistoryManager history;
    public InMemoryTaskManager(Scanner scanner, HistoryManager history){
        this.scanner = scanner;
        tasksData = new HashMap<>();
        idCounter = 0;
        this.history = history;
    }

    @Override
    public void addRegularTask(Task newTask){
        newTask.setId(++idCounter);
        tasksData.put(idCounter, newTask);
        System.out.println("Задание добавлено! (ID - " + idCounter + ")");
    }

    @Override
    public void addEpicTask(EpicTask epicTask, ArrayList<SubTask> subTasks){
        epicTask.setId(++idCounter);
        for (SubTask subTask : subTasks) {
            subTask.setId(++idCounter);
            subTask.setBoundedTo(epicTask.getId());
            tasksData.put(idCounter, subTask);
            epicTask.addSubTaskId(idCounter);
        }
        System.out.println("Подзадачи добавлены!");
        tasksData.put(epicTask.getId(), epicTask);
        System.out.println("\"Эпик\" задание добавлено! (ID - " + idCounter + ")");
    }

    @Override
    public void addSubTask (SubTask newSubTask){
        if (tasksData.containsKey(newSubTask.getBoundedTo())){
            newSubTask.setId(++idCounter);
            tasksData.put(idCounter, newSubTask);
            ((EpicTask) tasksData.get(newSubTask.getBoundedTo())).addSubTaskId(idCounter);
            updateEpicTaskStatus(newSubTask.getBoundedTo());
        } else {
            System.out.println("Задача не была добавлена в список, \"Эпик\" привязки не был найден.");
        }
    }

    @Override
    public void editTask(Task editedTask){
        if (editedTask instanceof SubTask){
            tasksData.remove(editedTask.getId());
            tasksData.put(editedTask.getId(), editedTask);
            updateEpicTaskStatus(((SubTask) editedTask).getBoundedTo());
        } else {
            tasksData.remove(editedTask.getId());
            tasksData.put(editedTask.getId(), editedTask);
        }
    }

    @Override
    public void deleteAllRegularTask(){
        for (Integer taskId: tasksData.keySet()){
            if (!(tasksData.get(taskId) instanceof SubTask) &&
                !(tasksData.get(taskId) instanceof EpicTask)){
                tasksData.remove(taskId);
            }
        }
    }

    @Override
    public void deleteAllEpicTasks(){
        for (Integer taskId: tasksData.keySet()){
            if (tasksData.get(taskId) instanceof SubTask ||
                tasksData.get(taskId) instanceof EpicTask){
                tasksData.remove(taskId);
            }
        }
    }

    @Override
    public void deleteAllSubTasks(){
        int boundedId;
        for (Integer taskId: tasksData.keySet()){
            if (tasksData.get(taskId) instanceof SubTask){
                boundedId = (((SubTask) tasksData.get(taskId)).getBoundedTo());
                tasksData.remove(taskId);
                ((EpicTask) tasksData.get(boundedId)).removeSubTaskId(taskId);
                updateEpicTaskStatus(boundedId);
            }
        }
    }

    @Override
    public void deleteTaskByID(int taskId){
        if (tasksData.get(taskId) instanceof SubTask) {
            int updateId = ((SubTask) tasksData.get(taskId)).getBoundedTo();
            tasksData.remove(taskId);
            ((EpicTask) tasksData.get(updateId)).removeSubTaskId(taskId);
            updateEpicTaskStatus(updateId);
        } else if (tasksData.get(taskId) instanceof EpicTask) {
            for (Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(taskId))) {
                tasksData.remove(subTaskId);
            }
            tasksData.remove(taskId);
        } else {
            tasksData.remove(taskId);
        }
        System.out.println("Задача " + taskId + " удалена из списка.");
    }

    @Override
    public void deleteAllTasks() {
        if (tasksData.isEmpty()){
            System.out.println("Список обычных задач пуст.");
        } else {
            deleteAllRegularTask();
            deleteAllSubTasks();
            deleteAllEpicTasks();
            System.out.println("Все задачи удалены.");
        }
    }

    public boolean isContains(int taskId){
        return tasksData.containsKey(taskId);
    }

    private ArrayList<Integer> subTasksIdentifiers(EpicTask epicTask){
        return epicTask.getFullSubTasksList();
    }

    @Override
    public void updateEpicTaskStatus(int epicTaskId) {
        ((EpicTask) tasksData.get(epicTaskId)).resetStatus();

        if (((EpicTask) tasksData.get(epicTaskId)).getFullSubTasksList() != null){
            for(Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(epicTaskId))){
                if (!tasksData.get(subTaskId).getStatus().equals("NEW")){
                    tasksData.get(epicTaskId).setStatus("IN_PROGRESS");
                    break;
                }
            }
        } else {
            return;
        }

        if (tasksData.get(epicTaskId).getStatus().equals("IN_PROGRESS")){
            boolean isDone = true;
            for(Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(epicTaskId))){
                if (!tasksData.get(subTaskId).getStatus().equals("DONE")){
                    isDone = false;
                    break;
                }
            }
            if (isDone){
                tasksData.get(epicTaskId).setStatus("DONE");
            }
        } else if (tasksData.get(epicTaskId).getStatus().equals("DONE")){
            for(Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(epicTaskId))){
                if (!tasksData.get(subTaskId).getStatus().equals("DONE")){
                    tasksData.get(epicTaskId).setStatus("IN_PROGRESS");
                    break;
                }
            }
        }
    }

    @Override
    public void printAllTasks() {
        for (Integer taskId : tasksData.keySet()) {
            if (tasksData.get(taskId) instanceof EpicTask){
                System.out.println("\nТип задачи: \"Эпик\" задача");
                tasksData.get(taskId).printInfo();
                for (Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(taskId))){
                    System.out.println("Подзадача \"Эпик\" задачи номер " + taskId + ":");
                    tasksData.get(subTaskId).printInfo();
                }
            } else if (!(tasksData.get(taskId) instanceof SubTask)) {
                System.out.println("\nТип задачи: Обычная задача");
                tasksData.get(taskId).printInfo();
            }
        }
    }

    @Override
    public void printInProgress() {
        System.out.println("Вывод текущих задач: ");
        for (Integer taskId : tasksData.keySet()) {
            if (tasksData.get(taskId).getStatus().equals("IN_PROGRESS")) {
                tasksData.get(taskId).printInfo();
            }
        }
    }

    @Override
    public void printAllDone() {
        System.out.println("Вывод текущих задач: ");
        for (Integer taskId : tasksData.keySet()) {
            if (tasksData.get(taskId).getStatus().equals("DONE")) {
                tasksData.get(taskId).printInfo();
            }
        }
    }



    @Override
    public void getTaskInfoById(int taskId){
        if (tasksData.containsKey(taskId)) {
            System.out.println("Информация о задаче:");
            tasksData.get(taskId).printInfo();
        } else {
            System.out.println("Задача с таким идентификатором не содержится в списке.");
            return;
        }

        if (tasksData.get(taskId) instanceof EpicTask){
            getEpic(taskId).printInfo();
            history.add(tasksData.get(taskId));
        } else if (tasksData.get(taskId) instanceof SubTask){
            getSubtask(taskId).printInfo();
            history.add(tasksData.get(taskId));
        } else {
            getTask(taskId).printInfo();
            history.add(tasksData.get(taskId));
        }
    }

    //нужно ли в следующих трёх методах возвращать копию объекта?
    //я их сделал просто потому что в задании они указаны, а зачем они - для редактирования?
    @Override
    public Task getTask(int taskId){
        return tasksData.get(taskId);
    }

    @Override
    public EpicTask getEpic(int taskId){
        return ((EpicTask) tasksData.get(taskId));
    }

    @Override
    public SubTask getSubtask(int taskId){
        return ((SubTask) tasksData.get(taskId));
    }

    @Override
    public List<Task> getHistory(){
        return history.getHistory();
    }
}