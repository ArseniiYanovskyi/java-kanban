package TaskData;

public class Task {
    protected enum Status{
        NEW,
        IN_PROGRESS,
        DONE
    }
    protected Status status;
    private String title;
    private String description;
    private int id;

    public Task(String title, String description){
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.id = 0;
    }

    public void setStatus(String status){
        this.status = Status.valueOf(status);
    }

    public String getStatus() {
        return status.toString();
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != o.getClass()) {
            return false;
        }

        return (this.id == ((Task)o).getId() &&
                this.status.equals(((Task)o).getStatus()) &&
                this.title.equals(((Task)o).getTitle()) &&
                this.description.equals(((Task)o).getDescription()));
    }
    public void printInfo(){
        System.out.println("ID задачи - '" + id  + '\''
                +"\nТекущий статус задачи - '" + status + '\''
                +"\nНазвание задачи - '" + title + '\''
                +"\nОписание задачи - '" + description + '\'');
    }
}
