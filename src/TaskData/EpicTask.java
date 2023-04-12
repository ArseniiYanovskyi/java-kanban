package TaskData;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Integer> subTasksIdentifiers;
    public EpicTask(String title, String description) {
        super(title, description);
        subTasksIdentifiers = new ArrayList<>();
    }
    public void addSubTaskId(int subTaskId){
        subTasksIdentifiers.add(subTaskId);
    }

    public ArrayList<Integer> getFullSubTasksList(){
        return subTasksIdentifiers;
    }

    public void removeSubTaskId(int subTaskId){
        subTasksIdentifiers.remove(Integer.valueOf(subTaskId));
    }

    public void resetStatus(){
        this.status = Status.NEW;
    }

    @Override
    public void printInfo(){
        super.printInfo();
        System.out.println("Количество подзадач: " + subTasksIdentifiers.size() + ".");
        System.out.print("Содержит подзадачи с ID: ");
        for (Integer subTaskId : subTasksIdentifiers){
            System.out.print(subTaskId + " ");
        }
        System.out.println("\n");
    }
}
