import java.util.ArrayList;

public class EpicTask extends Task{

    public EpicTask(String title, String description, int id) {
        super(title, description, id);
        subTasksIdentifiers = new ArrayList<>();
    }

    @Override
    public void printInfo(){
        super.printInfo();
        System.out.println("Количество подзадач: " + subTasksIdentifiers.size() + ".");
        System.out.print("Содержит подзадачи с ID: ");
        for (Integer subTaskId : subTasksIdentifiers){
            System.out.print(subTaskId + ", ");
        }
    }
}
