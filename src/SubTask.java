public class SubTask extends Task{

    public SubTask(String title, String description, int id) {
        super(title, description, id);
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Является подзадачей \"Эпик\" задачи номер " + getBoundedTo());
    }
}
