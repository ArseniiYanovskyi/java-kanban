
import java.util.HashMap;
import java.util.Scanner;

public class EpicTask extends Task{
    public HashMap<Integer, Task> epicTaskParts;

    public EpicTask(String title, String description, int id) {
        super(title, description, id);
        epicTaskParts = new HashMap<>();
    }

    @Override
    public void printInfo(){
        super.printInfo();
        System.out.println("Количество подзадач: " + epicTaskParts.size() + ".");
        for (Integer taskID : epicTaskParts.keySet()){
            System.out.println("ID подзадачи: "+ taskID);
            epicTaskParts.get(taskID).printInfo();
        }
    }


    public void moveStatus(int id){
        epicTaskParts.get(id).moveStatus();
        System.out.println("Новый статус подзадачи " + id + " - " + epicTaskParts.get(id).getStatus() + ".");
        refreshStatus();
    }

    public void changeStatus(){
        this.moveStatus();
    }

    public void refreshStatus(){
        if (epicTaskParts != null){
            if (this.getStatus().equals("NEW")){
                for(Integer taskID : epicTaskParts.keySet()){
                    if (!epicTaskParts.get(taskID).getStatus().equals("NEW")){
                        this.changeStatus();
                        break;
                    }
                }
            }
            if (this.getStatus().equals("IN_PROGRESS")){
                boolean isDone = true;
                for(Integer taskID : epicTaskParts.keySet()){
                    if (!epicTaskParts.get(taskID).getStatus().equals("DONE")){
                        isDone = false;
                        break;
                    }
                }
                if (isDone){
                    this.changeStatus();
                }
            }
        }
    }

    public void addPart(Scanner scanner, int id){
        System.out.println("Введите название подзадачи:");
        String title = scanner.nextLine();
        System.out.println("Введите описание подзадачи:");
        String description = scanner.nextLine();
        epicTaskParts.put(id, new Task(title, description, id));
    }
}
