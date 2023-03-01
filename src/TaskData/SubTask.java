package TaskData;

public class SubTask extends Task {
    private int boundedTo;

    public SubTask(String title, String description, int boundedTo) {
        super(title, description);
        this.boundedTo = boundedTo;
    }

    public int getBoundedTo() {
        return boundedTo;
    }

    public void setBoundedTo(int boundedTo) {
        this.boundedTo = boundedTo;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Является подзадачей \"Эпик\" задачи номер " + getBoundedTo());
    }
}
