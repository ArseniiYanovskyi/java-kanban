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
                    taskManager.addRegularTask();
                    break;
                case 5:
                    taskManager.addEpicTask();
                    break;
                case 8:
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
}
