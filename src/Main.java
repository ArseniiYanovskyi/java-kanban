import TaskData.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InMemoryTaskManager taskManager = new InMemoryTaskManager(scanner);
        boolean programIsOn = true;
        while (programIsOn) {
            printMenu();
            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    taskManager.printAllTasks();
                    break;
                case 2:
                    taskManager.printInProgress();
                    break;
                case 3:
                    taskManager.printAllDone();
                    break;
                case 4:
                    Task task = createTask(scanner);
                    taskManager.addRegularTask(task);
                    break;
                case 5:
                    EpicTask epicTask = createEpicTask(scanner);
                    ArrayList<SubTask> subTasks = createSubTasks(scanner);
                    taskManager.addEpicTask(epicTask, subTasks);
                    break;
                case 6:
                    SubTask newSubTask = createNewSubTask(scanner);
                    taskManager.addSubTask(newSubTask);
                    break;
                case 7:
                    System.out.println("Введите ID задачи: ");
                    int taskId = Integer.parseInt(scanner.nextLine());

                    taskManager.editOrDelete(taskId);
                    break;
                case 9:
                    taskManager.deleteAllTasks();
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
    public static @NotNull Task createTask (Scanner scanner) {
        System.out.println("Введите название задания:");
        String title = scanner.nextLine();
        System.out.println("Введите описание задания:");
        String description = scanner.nextLine();
        return new Task(title, description);
    }
    public static @NotNull EpicTask createEpicTask(Scanner scanner){
        System.out.println("Введите название \"Эпик\" задания:");
        String title = scanner.nextLine();
        System.out.println("Введите описание \"Эпик\" задания:");
        String description = scanner.nextLine();
        return new EpicTask(("\"Эпик\" " + title), description);
    }
    public static @NotNull ArrayList<SubTask> createSubTasks(Scanner scanner){
        ArrayList<SubTask> subTasks = new ArrayList<>();
        System.out.println("Введите количество подзадач: ");
        int amount = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < amount; i++){
            System.out.println("Создание " + (i+1) + " подзадачи:");
            System.out.println("Введите название подзадачи:");
            String title = scanner.nextLine();
            System.out.println("Введите описание подзадачи:");
            String description = scanner.nextLine();
            subTasks.add(new SubTask(title, description,0));
        }
        return subTasks;
    }

    public static @NotNull SubTask createNewSubTask(Scanner scanner){
        System.out.println("Введите ID \"Эпик\" задания, к которому будет привязана эта подзадача");
        int bound = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите название подзадачи:");
        String title = scanner.nextLine();
        System.out.println("Введите описание подзадачи:");
        String description = scanner.nextLine();
        return new SubTask(title, description, bound);
    }

    public static void printMenu(){
        System.out.println("\n1 - Вывод полного списка задач.");
        System.out.println("2 - Вывод списка текущих задач.");
        System.out.println("3 - Вывод списка завершенных задач.");
        System.out.println("4 - Создать новую задачу.");
        System.out.println("5 - Создать \"Эпик\".");
        System.out.println("6 - Создать новую подзадачу.");
        System.out.println("7 - Редактирование данных или статуса задачи по ID.");
        System.out.println("9 - Удаление всех задач.");
        System.out.println("0 - Выход");
    }
}
