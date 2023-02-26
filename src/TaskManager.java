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
    TaskManager(){
        scanner = new Scanner(System.in);
        regularTasksData = new HashMap<>();
        epicTasksData = new HashMap<>();
        random = new Random(); //для генерации ключа задания в HashMap
    }
    public void start (){
        while (programIsOn) {
            printMenu();
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    printAllTasks();
                    break;
                case 2:
                    printInProgress();
                    break;
                case 3:
                    break;
                case 4:
                    regularTasksData.put(generateUniqueID(), createTask());
                    System.out.println("Задание добавлено!");
                    break;
                case 5:
                    epicTasksData.put(generateUniqueID(), createEpicTask());
                    System.out.println("\"Эпик\" задание добавлено!");
                    break;
                case 8:
                    editOrDelete();
                    break;
                case 9:
                    deleteAllTasks();
                    break;
                case 0:
                    programIsOn = false;
                    scanner.close();
                    System.out.println("Пока!");
                    break;
                default:
                    System.out.println("Неверный ввод.");
            }
        }
    }

    private @NotNull Task createTask(){
        System.out.println("Введите название задания:");
        String title = scanner.nextLine();
        System.out.println("Введите описание задания:");
        String description = scanner.nextLine();
        return new Task(title, description);
    }

    private @NotNull EpicTask createEpicTask(){
        System.out.println("Введите название \"Эпик\" задания:");
        String title = scanner.nextLine();
        System.out.println("Введите описание \"Эпик\" задания:");
        String description = scanner.nextLine();
        EpicTask newTask = new EpicTask(("\"Эпик\" " + title), description);
        System.out.println("Введите количество подзадач: ");
        int amount = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < amount; i++){
            System.out.println("Создание " + i+1 + " подзадачи:");
            newTask.addPart(scanner, generateUniqueID());
        }
        return newTask;
    }

    private int generateUniqueID(){
        int result = random.nextInt(1000);
        if (!regularTasksData.isEmpty()) {
            while (regularTasksData.containsKey(result)) {
                result = random.nextInt(1000);
            }
        }
        return result;
    }

    private void printAllTasks(){
        for (Integer key : regularTasksData.keySet()){
            System.out.println("ID задачи: " + key + ". Тип задачи: Обычная задача");
            regularTasksData.get(key).printInfo();
        }
        for (Integer key : epicTasksData.keySet()){
            System.out.println("ID задачи: " + key + ". Тип задачи: \"Эпик\" задача.");
            regularTasksData.get(key).printInfo();
        }
    }

    private void printInProgress(){
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

    private void deleteAllTasks(){
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

    private void editOrDelete(){
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

    private void printMenu(){
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
