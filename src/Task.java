public class Task {
    private final String[] statuses = {"NEW", "IN_PROGRESS", "DONE"};
    protected String status;
    public String title;
    public String description;

    public Task(String title, String description) {
        this.status = statuses[0];
        this.title = title;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void moveStatus(){
        if (status.equals(statuses[0])){
            status = statuses[1];
        } else if (status.equals(statuses[1])){
            status = statuses[2];
        }
    }

    public void printInfo(){
        System.out.println("Текущий статус задачи - '" + status + '\'' +"\nНазвание задачи - '" + title + '\'' +
                "\nОписание задачи - '" + description + '\'');
    }
}
