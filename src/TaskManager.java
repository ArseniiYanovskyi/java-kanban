
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class TaskManager {
    private boolean programIsOn = true;
    private HashMap<Integer,Task> regularTasksData;
    private HashMap<Integer,EpicTask> epicTasksData;
    private Scanner scanner;
    Random random;
    TaskManager(Scanner scanner){
        this.scanner = scanner;
        regularTasksData = new HashMap<>();
        epicTasksData = new HashMap<>();
        random = new Random(); //для генерации ключа задания в HashMap
    }

    private @NotNull Task createTask(int id){
        System.out.println("Введите название задания:");
        String title = scanner.nextLine();
        System.out.println("Введите описание задания:");
        String description = scanner.nextLine();
        return new Task(title, description, id);
    }

    private @NotNull EpicTask createEpicTask(int id){
        System.out.println("Введите название \"Эпик\" задания:");
        String title = scanner.nextLine();
        System.out.println("Введите описание \"Эпик\" задания:");
        String description = scanner.nextLine();
        EpicTask newTask = new EpicTask(("\"Эпик\" " + title), description, id);
        System.out.println("Введите количество подзадач: ");
        int amount = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < amount; i++){
            System.out.println("Создание " + (i+1) + " подзадачи:");
            newTask.addPart(scanner, (i+1));
        }
        return newTask;
    }

    public void addRegularTask(){
        int newID = generateUniqueID();
        regularTasksData.put(newID, createTask(newID));
        System.out.println("Задание добавлено! (ID - " + newID + ")");
    }

    public void addEpicTask(){
        int newID = generateUniqueID();
        epicTasksData.put(newID, createEpicTask(newID));
        System.out.println("\"Эпик\" задание добавлено! (ID - " + newID + ")");
    }

    private int generateUniqueID(){
        int result = random.nextInt(1000);
        while ((!regularTasksData.isEmpty() && regularTasksData.containsKey(result))
                || (!epicTasksData.isEmpty() && epicTasksData.containsKey(result)) ) {
            result = random.nextInt(1000);
        }
        return result;
    }

    public void printAllTasks(){
        for (Integer key : regularTasksData.keySet()){
            System.out.println("\nТип задачи: Обычная задача");
            regularTasksData.get(key).printInfo();
        }
        for (Integer key : epicTasksData.keySet()){
            System.out.println("\nТип задачи: \"Эпик\" задача.");
            epicTasksData.get(key).printInfo();
        }
    }

    public void printInProgress(){
        System.out.println("Вывод обычных текущих задач: ");
        for (Integer key : regularTasksData.keySet()){
            if (regularTasksData.get(key).getStatus().equals("IN_PROGRESS")) {
                System.out.println("\nID задачи: " + key + ".");
                regularTasksData.get(key).printInfo();
            }
        }
        System.out.println("Вывод текущих \"Эпик\" задач: ");
        for (Integer key : epicTasksData.keySet()){
            if (epicTasksData.get(key).getStatus().equals("IN_PROGRESS")) {
                System.out.println("\nID задачи: " + key + ".");
                epicTasksData.get(key).printInfo();
            }
        }
    }

    public void printAllDone(){
        System.out.println("Вывод обычных завершенных задач: ");
        for (Integer key : regularTasksData.keySet()){
            if (regularTasksData.get(key).getStatus().equals("DONE")) {
                System.out.println("\nID задачи: " + key + ".");
                regularTasksData.get(key).printInfo();
            }
        }
        System.out.println("Вывод завершенных \"Эпик\" задач: ");
        for (Integer key : epicTasksData.keySet()){
            if (epicTasksData.get(key).getStatus().equals("DONE")) {
                System.out.println("\nID задачи: " + key + ".");
                epicTasksData.get(key).printInfo();
            }
        }
    }

    public void deleteAllTasks(){
        if (regularTasksData.isEmpty()){
            System.out.println("Список обычных задач пуст.");
        } else {
            for (Integer key : regularTasksData.keySet()) {
                regularTasksData.remove(key);
            }
            System.out.println("Все обычные задачи удалены.");
        }
        if (epicTasksData.isEmpty()){
            System.out.println("Список \"Эпик\" задач пуст.");
        } else {
            for (Integer key : epicTasksData.keySet()) {
                epicTasksData.remove(key);
            }
            System.out.println("Все \"Эпик\" задачи удалены.");
        }
    }

    public void editOrDelete(){
        System.out.println("Введите 1 для поиска обычной задачи. 2 - для \"Эпик\".");
        int command = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите ID задачи: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (command == 1 && regularTasksData.containsKey(id)){
            System.out.println("Введите 1 для продвижения статуса задачи. 2 - для удаления.");
            command = Integer.parseInt(scanner.nextLine());
            if (command == 1){
                System.out.println("Текущий статус задачи - " + regularTasksData.get(id).getStatus() + ".");
                regularTasksData.get(id).moveStatus();
                System.out.println("Новый статус задачи - " + regularTasksData.get(id).getStatus() + ".");
            } else if (command == 2) {
                regularTasksData.remove(id);
                System.out.println("Задача " + id + " удалена.");
            } else {
                System.out.println("Неизвестная команда.");
            }
        } else if (command == 2 && epicTasksData.containsKey(id)) {
            System.out.println("Введите 1 для редактирования статуса подзадачи, 2 - для удаления.");
            command = Integer.parseInt(scanner.nextLine());
            if (command == 1){
                System.out.println("Введите ID подзадачи: ");
                int epicPartId = Integer.parseInt(scanner.nextLine());
                epicTasksData.get(id).moveStatus(epicPartId);
                System.out.println("Текущий статус \"Эпик\" задачи - " + epicTasksData.get(id).getStatus());
            } else if (command == 2) {
                epicTasksData.remove(id);
                System.out.println("\"Эпик\" задача " + id + " удалена.");
            } else {
                System.out.println("Неизвестная команда.");
            }
        } else {
            System.out.println("Неизвестная команда или ID задания не совпадает с указанным типом.");
        }

    }

    public void printMenu(){
        System.out.println("\n1 - Вывод полного списка задач.");
        System.out.println("2 - Вывод списка текущих задач.");
        System.out.println("3 - Вывод списка завершенных задач.");
        System.out.println("4 - Создать новую задачу.");
        System.out.println("5 - Создать \"Эпик\".");
        System.out.println("8 - Редактирование и(или) удаление задачи или подзадачи.");
        System.out.println("9 - Удаление всех задач.");
        System.out.println("0 - Выход");
    }
}
