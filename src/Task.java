
public class Task {
    private final String[] statuses = {"NEW", "IN_PROGRESS", "DONE"};
    private enum Status{
        NEW("NEW"), IN_PROGRESS("IN_PROGRESS"), DONE("DONE");
        private String status;
        Status(String status){
            this.status = status;
        }
    }
    protected Status status;
    public String title;
    public String description;
    public int id;

    public Task(String title, String description, int id) {
        this.status = Status.NEW;
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public String getStatus() {
        return status.toString();
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

    public void printInfo(){
        System.out.println("ID задачи - '" + id  + '\''
                +"\nТекущий статус задачи - '" + status + '\''
                +"\nНазвание задачи - '" + title + '\''
                +"\nОписание задачи - '" + description + '\'');
    }
}
