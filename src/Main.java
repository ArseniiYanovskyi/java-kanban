import TaskData.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ManagerSaveException {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = Managers.getFileBacked("dataFile.csv");
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
                    System.out.println("1 - Создать новую простую задачу.");
                    System.out.println("2 - Создать \"Эпик\".");
                    System.out.println("3 - Создать новую подзадачу.");
                    System.out.println("4 - Назад.");
                    option = Integer.parseInt(scanner.nextLine());
                    switch (option) {
                        case 1:
                            Task task = createTask(scanner);
                            taskManager.addRegularTask(task);
                            break;
                        case 2:
                            EpicTask epicTask = createEpicTask(scanner);
                            ArrayList<SubTask> subTasks = createSubTasks(scanner);
                            taskManager.addEpicTask(epicTask, subTasks);
                            break;
                        case 3:
                            SubTask newSubTask = createNewSubTask(scanner);
                            taskManager.addSubTask(newSubTask);
                            break;
                        case 4:
                            break;
                    }
                    break;
                case 5:
                    System.out.println("Введите ID задачи: ");
                    option = Integer.parseInt(scanner.nextLine());

                    taskManager.getTask(option).printInfo();
                    break;
                case 6:
                    for (Task task : taskManager.getHistory()){
                        task.printInfo();
                    }
                    break;
                case 7:
                    System.out.println("Введите ID задачи: ");
                    option = Integer.parseInt(scanner.nextLine());
                    if (taskManager.getTask(option) != null) {
                        taskManager.getTask(option).printInfo();
                        System.out.println("Введите 1 для редактирования. "
                                + "2 - для продвижения статуса. 3 - удаления. "
                                + "или любой другой символ для отмены.");
                        String command = scanner.nextLine();
                        Task editTask = taskManager.getTask(option);
                        switch (command) {
                            case "1":
                                System.out.println("Введите 1 для редактирования названия, 2 для описания.");
                                command = scanner.nextLine();
                                switch (command) {
                                    case "1":
                                        System.out.println("Введите новое название подзадачи:");
                                        String newTitle = scanner.nextLine();
                                         editTask.setTitle(newTitle);
                                         break;
                                    case "2":
                                        System.out.println("Введите новое описание подзадачи:");
                                        String newDescription = scanner.nextLine();
                                        editTask.setDescription(newDescription);
                                }
                                break;
                            case"2":
                                if (editTask instanceof EpicTask) {
                                    System.out.println("Статус \"Эпик\" задачи изменяется по мере изменения статуса подзадач.");
                                } else if (editTask instanceof SubTask) {
                                    System.out.println("Введите новый статус:");
                                    command = scanner.nextLine();
                                    editTask.setStatus(command);
                                    taskManager.updateEpicTaskStatus(((SubTask) editTask).getBoundedTo());
                                } else {
                                    command = scanner.nextLine();
                                    editTask.setStatus(command);
                                }
                                break;
                            case"3":
                                taskManager.deleteTaskByID(option);
                        }
                    }
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
        String title = scanner.nextLine() + "";
        System.out.println("Введите описание задания:");
        String description = scanner.nextLine() + "";
        return new Task(title, description);
    }
    public static @NotNull EpicTask createEpicTask(Scanner scanner){
        System.out.println("Введите название \"Эпик\" задания:");
        String title = scanner.nextLine() + "";
        System.out.println("Введите описание \"Эпик\" задания:");
        String description = scanner.nextLine() + "";
        return new EpicTask(("\"Эпик\" " + title), description);
    }
    public static @NotNull ArrayList<SubTask> createSubTasks(Scanner scanner){
        ArrayList<SubTask> subTasks = new ArrayList<>();
        System.out.println("Введите количество подзадач: ");
        int amount = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < amount; i++){
            System.out.println("Создание " + (i+1) + " подзадачи:");
            System.out.println("Введите название подзадачи:");
            String title = scanner.nextLine() + "";
            System.out.println("Введите описание подзадачи:");
            String description = scanner.nextLine() + "";
            subTasks.add(new SubTask(title, description,0));
        }
        return subTasks;
    }

    public static @NotNull SubTask createNewSubTask(Scanner scanner){
        System.out.println("Введите ID \"Эпик\" задания, к которому будет привязана эта подзадача");
        int bound = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите название подзадачи:");
        String title = scanner.nextLine() + "";
        System.out.println("Введите описание подзадачи:");
        String description = scanner.nextLine() + "";
        return new SubTask(title, description, bound);
    }

    public static void printMenu(){
        System.out.println("\n1 - Вывод полного списка задач.");
        System.out.println("2 - Вывод списка текущих задач.");
        System.out.println("3 - Вывод списка завершенных задач.");
        System.out.println("4 - Создать новую задачу.");
        System.out.println("5 - Просмотр задачи по ID.");
        System.out.println("6 - История просмотров.");
        System.out.println("7 - Редактирование данных или статуса задачи по ID.");
        System.out.println("9 - Удаление всех задач.");
        System.out.println("0 - Выход");
    }
}
