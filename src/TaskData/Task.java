package TaskData;

public class Task {
    private enum Status{
        NEW("NEW"), IN_PROGRESS("IN_PROGRESS"), DONE("DONE");
        private String status;
        Status(String status){
            this.status = status;
        }
    }
    private Status status;
    private String title;
    private String description;
    private int id;

    public Task(String title, String description){
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.id = 0;
    }
    public boolean isEqualStatus(String status){
        return this.status.toString().equals(status);
    }

    public void moveStatus(){
        switch (status){
            case IN_PROGRESS:
                status = Status.DONE;
                break;
            default:
                status = Status.IN_PROGRESS;
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
    public void printInfo(){
        System.out.println("ID задачи - '" + id  + '\''
                +"\nТекущий статус задачи - '" + status + '\''
                +"\nНазвание задачи - '" + title + '\''
                +"\nОписание задачи - '" + description + '\'');
    }
}
