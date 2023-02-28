import java.util.ArrayList;

public class Task {
    //private final String[] statuses = {"NEW", "IN_PROGRESS", "DONE"};
    private enum Status{
        NEW("NEW"), IN_PROGRESS("IN_PROGRESS"), DONE("DONE");
        private String status;
        Status(String status){
            this.status = status;
        }
    }
    protected Status status;
    public String title;
    public ArrayList<Integer> subTasksIdentifiers;
    public String description;
    public int id;
    public int boundedTo;

    public Task(String title, String description, int id){
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.id = id;
    }
    public Task(int id) {
        this.status = Status.NEW;
        this.id = id;
    }
    public Task(){
        this.status = Status.NEW;
    }

    public boolean isEqualStatus(String status){
        return this.status.toString().equals(status);
    }

    public void moveStatus(){
        switch (status){
            case NEW:
                status = Status.IN_PROGRESS;
                break;
            case IN_PROGRESS:
                status = Status.DONE;
                break;
            default:
                System.out.println("Невозможно изменить статус задачи.");
        }
    }
    public Status getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getBoundedTo() {
        return boundedTo;
    }
    public void setBoundedTo(int boundedTo) {
        this.boundedTo = boundedTo;
    }
    public void printInfo(){
        System.out.println("ID задачи - '" + id  + '\''
                +"\nТекущий статус задачи - '" + status + '\''
                +"\nНазвание задачи - '" + title + '\''
                +"\nОписание задачи - '" + description + '\'');
    }
}
