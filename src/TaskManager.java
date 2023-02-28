import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    private HashMap<Integer,Task> tasksData;
    private Scanner scanner;
    private int idCounter;
    TaskManager(Scanner scanner){
        this.scanner = scanner;
        tasksData = new HashMap<>();
        idCounter = 0; //для генерации ключа задания в HashMap
    }

    public void addRegularTask(Task newTask){
        newTask.setId(++idCounter);
        tasksData.put(idCounter, newTask);
        System.out.println("Задание добавлено! (ID - " + idCounter + ")");
    }

    public void addEpicTask(EpicTask epicTask, ArrayList<SubTask> subTasks){
        epicTask.setId(++idCounter);
        tasksData.put(idCounter, epicTask);
        System.out.println("\"Эпик\" задание добавлено! (ID - " + idCounter + ")");
        for (SubTask subTask : subTasks) {
            subTask.setBoundedTo(epicTask.getId());
            tasksData.put(++idCounter, subTask);
            epicTask.subTasksIdentifiers.add(idCounter);
        }
        System.out.println("Подзадачи добавлены!");
    }

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

    public void editOrDelete() {
        System.out.println("Введите ID задачи: ");
        int taskId = Integer.parseInt(scanner.nextLine());
        if (tasksData.containsKey(taskId)) {
            System.out.println("Информация о задаче:");
            tasksData.get(taskId).printInfo();
        } else {
            System.out.println("Задача с таким идентификатором не содержится в списке.");
            return;
        }
        System.out.println("Введите 1 для продвижения статуса задачи. 2 - для удаления. " +
                "или любой другой символ для отмены.");
        String command = scanner.nextLine();
        switch (command) {
            case "1":
                if (tasksData.get(taskId).getClass().toString().equals("class EpicTask")) {
                    System.out.println("Статус \"Эпик\" задачи изменяется по мере изменения статуса подзадач.");
                } else if(tasksData.get(taskId).getClass().toString().equals("class SubTask")) {
                    tasksData.get(taskId).moveStatus();
                    updateEpicTaskStatus(tasksData.get(taskId).getBoundedTo());
                } else {
                    tasksData.get(taskId).moveStatus();
                }
                break;
            case "2":
                if (tasksData.get(taskId).getClass().toString().equals("class SubTask")) {
                    int updateId = tasksData.get(taskId).getBoundedTo();
                    tasksData.remove(taskId);
                    updateEpicTaskStatus(updateId);
                } else if (tasksData.get(taskId).getClass().toString().equals("class EpicTask")) {
                    for (Integer subTaskId : tasksData.get(taskId).subTasksIdentifiers) {
                        tasksData.remove(subTaskId);
                    }
                    tasksData.remove(taskId);
                } else {
                    tasksData.remove(taskId);
                }
                System.out.println("Задача " + taskId + " удалена из списка.");
            default:
        }
    }

    public void updateEpicTaskStatus(int epicTaskId) {
        if (tasksData.get(epicTaskId).isEqualStatus("NEW")){
            boolean isNew = true;
            for(Integer subTaskId : tasksData.get(epicTaskId).subTasksIdentifiers){
                if(!tasksData.containsKey(subTaskId)){
                    tasksData.get(epicTaskId).subTasksIdentifiers.remove(subTaskId);
                } else if (!tasksData.get(subTaskId).isEqualStatus("NEW")){
                    isNew = false;
                }
            }
            if (!isNew){
                tasksData.get(epicTaskId).moveStatus();
            }
        }
        if (tasksData.get(epicTaskId).isEqualStatus("IN_PROGRESS")){
            boolean isDone = true;
            for(Integer subTaskId : tasksData.get(epicTaskId).subTasksIdentifiers){
                if(!tasksData.containsKey(subTaskId)){
                    tasksData.get(epicTaskId).subTasksIdentifiers.remove(subTaskId);
                } else if (!tasksData.get(subTaskId).isEqualStatus("DONE")){
                    isDone = false;
                }
            }
            if (isDone){
                tasksData.get(epicTaskId).moveStatus();
            }
        }
    }

    public void printAllTasks() {
        for (Integer taskId : tasksData.keySet()) {
            switch (tasksData.get(taskId).getClass().toString()){
                case("class Task"):
                    System.out.println("\nТип задачи: Обычная задача");
                    tasksData.get(taskId).printInfo();
                    break;
                case("class EpicTask"):
                    System.out.println("\nТип задачи: \"Эпик\" задача");
                    tasksData.get(taskId).printInfo();
                    for (Integer subTaskId : tasksData.get(taskId).subTasksIdentifiers){
                        System.out.println("Подзадача \"Эпик\" задачи номер " + taskId + ":");
                        tasksData.get(subTaskId).printInfo();
                    }
                    break;
                default:
            }
        }
    }

    public void printInProgress() {
        System.out.println("Вывод текущих задач: ");
        for (Integer taskId : tasksData.keySet()) {
            if (tasksData.get(taskId).isEqualStatus("IN_PROGRESS")) {
                tasksData.get(taskId).printInfo();
            }
        }
    }

    public void printAllDone() {
        System.out.println("Вывод текущих задач: ");
        for (Integer taskId : tasksData.keySet()) {
            if (tasksData.get(taskId).isEqualStatus("DONE")) {
                tasksData.get(taskId).printInfo();
            }
        }
    }

    public void printMenu(){
        System.out.println("\n1 - Вывод полного списка задач.");
        System.out.println("2 - Вывод списка текущих задач.");
        System.out.println("3 - Вывод списка завершенных задач.");
        System.out.println("4 - Создать новую задачу.");
        System.out.println("5 - Создать \"Эпик\".");
        System.out.println("6 - Редактирование и(или) удаление задачи или подзадачи.");
        System.out.println("9 - Удаление всех задач.");
        System.out.println("0 - Выход");
    }








}
