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
    InMemoryTaskManager(Scanner scanner){
        this.scanner = scanner;
        tasksData = new HashMap<>();
        idCounter = 0;
        history = Managers.getDefaultHistory();
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
            addSubTaskToEpic(idCounter, tasksData.get(newSubTask.getBoundedTo()));
            updateEpicTaskStatus(newSubTask.getBoundedTo());
        } else {
            System.out.println("Задача не была добавлена в список, \"Эпик\" привязки не был найден.");
        }
    }

    @Override
    public void editOrDelete(int taskId) {

        if (tasksData.containsKey(taskId)) {
            System.out.println("Информация о задаче:");
            tasksData.get(taskId).printInfo();
        } else {
            System.out.println("Задача с таким идентификатором не содержится в списке.");
            return;
        }

        System.out.println("Введите 1 для редактирования. "
                + "2 - для продвижения статуса. 3 - удаления. "
                + "или любой другой символ для отмены.");
        String command = scanner.nextLine();

        switch (command) {
            case "1":
                System.out.println("Введите 1 для редактирования названия, 2 для описания.");
                command = scanner.nextLine();
                switch (command) {
                    case "1":
                        System.out.println("Введите новое название подзадачи:");
                        String newTitle = scanner.nextLine();
                        tasksData.get(taskId).setTitle(newTitle);
                    case "2":
                        System.out.println("Введите новое описание подзадачи:");
                        String newDescription = scanner.nextLine();
                        tasksData.get(taskId).setDescription(newDescription);
                }
                break;
            case"2":
                    if (tasksData.get(taskId) instanceof EpicTask) {
                        System.out.println("Статус \"Эпик\" задачи изменяется по мере изменения статуса подзадач.");
                    } else if (tasksData.get(taskId) instanceof SubTask) {
                        tasksData.get(taskId).moveStatus();
                        updateEpicTaskStatus(getBoundId(tasksData.get(taskId)));
                    } else {
                        tasksData.get(taskId).moveStatus();
                    }
                break;
            case "3":
                if (tasksData.get(taskId) instanceof SubTask) {
                    int updateId = getBoundId(tasksData.get(taskId));
                    tasksData.remove(taskId);
                    removeSubTaskId(tasksData.get(updateId), taskId);
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
    }

    private ArrayList<Integer> subTasksIdentifiers(EpicTask epicTask){
        return epicTask.getFullSubTasksList();
    }

    private void addSubTaskToEpic(int subTaskId, Object epicTask){
        ((EpicTask) epicTask).addSubTaskId(subTaskId);
    }

    private int getBoundId(Object subTask){
        return ((SubTask) subTask).getBoundedTo();
    }

    private void removeSubTaskId(Object epicTask, int id){
        ((EpicTask) epicTask).removeSubTaskId(id);
    }

    private void updateEpicTaskStatus(int epicTaskId) {
        if (tasksData.get(epicTaskId).getStatus().equals("NEW")){
            for(Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(epicTaskId))){
                if (!tasksData.get(subTaskId).getStatus().equals("NEW")){
                    tasksData.get(epicTaskId).moveStatus();
                    break;
                }
            }
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
                tasksData.get(epicTaskId).moveStatus();
            }
        } else if (tasksData.get(epicTaskId).getStatus().equals("DONE")){
            for(Integer subTaskId : subTasksIdentifiers((EpicTask) tasksData.get(epicTaskId))){
                if (!tasksData.get(subTaskId).getStatus().equals("DONE")){
                    tasksData.get(epicTaskId).moveStatus();
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
    public void deleteAllTasks() {
        if (tasksData.isEmpty()){
            System.out.println("Список обычных задач пуст.");
        } else {
            for (Integer key : tasksData.keySet()) {
                tasksData.remove(key);
            }
            System.out.println("Все задачи удалены.");
        }
    }


    @Override
    public void getTask(int taskId){
        tasksData.get(taskId).printInfo();
        history.add(tasksData.get(taskId));
    }

    @Override
    public void getEpic(int taskId){
        tasksData.get(taskId).printInfo();
        history.add(tasksData.get(taskId));
    }

    @Override
    public void getSubtask(int taskId){
        tasksData.get(taskId).printInfo();
        history.add(tasksData.get(taskId));
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
            getEpic(taskId);
        } else if (tasksData.get(taskId) instanceof SubTask){
            getSubtask(taskId);
        } else {
            getTask(taskId);
        }
    }

    @Override
    public void historyView(){
        for (Task task : history.getHistory()){
            task.printInfo();
        }
    }
}