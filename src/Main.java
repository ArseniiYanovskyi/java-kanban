import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager(scanner);
        boolean programIsOn = true;
        while (programIsOn) {
            taskManager.printMenu();
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
                    taskManager.editOrDelete();
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
        Task newTask = new Task();
        newTask.setTitle(title);
        newTask.setDescription(description);
        return newTask;
    }
    public static @NotNull EpicTask createEpicTask(Scanner scanner){
        System.out.println("Введите название \"Эпик\" задания:");
        String title = scanner.nextLine();
        System.out.println("Введите описание \"Эпик\" задания:");
        String description = scanner.nextLine();
        EpicTask newEpicTask = new EpicTask(("\"Эпик\" " + title), description, 0);
        return newEpicTask;
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
            subTasks.add(new SubTask(title, description, (i+1)));
        }
        return subTasks;
    }
}
