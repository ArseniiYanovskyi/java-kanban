import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class InMemoryTaskManagerTest  extends TaskManagerTests<InMemoryTaskManager>{
    @BeforeEach
    public void creationOfInMemoryTaskManager() throws IOException, InterruptedException {
        super.taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}
