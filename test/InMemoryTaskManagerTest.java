import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest  extends TaskManagerTests<InMemoryTaskManager>{
    @BeforeEach
    public void creationOfInMemoryTaskManager() {
        super.taskManager = (InMemoryTaskManager) Managers.getDefault();
    }
}
