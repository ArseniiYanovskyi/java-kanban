import TaskData.EpicTask;
import TaskData.SubTask;
import TaskData.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

abstract class TaskManagerTests<T extends TaskManager> {
        T taskManager;

        @Test
        public void shouldReturnCorrectRegularTaskInfo() {
                Task newTask = new Task("RegularTaskTitle", "RegularTaskDesc");
                taskManager.addRegularTask(newTask);
                Assertions.assertEquals("NEW", taskManager.getTask(1).getStatus());

                newTask.setStatus("IN_PROGRESS");
                taskManager.editRegularTask(newTask);
                Assertions.assertEquals("IN_PROGRESS", taskManager.getTask(1).getStatus());

                newTask.setStatus("DONE");
                taskManager.editRegularTask(newTask);
                Assertions.assertEquals("DONE", taskManager.getTask(1).getStatus());

                Assertions.assertEquals("RegularTaskTitle", taskManager.getTask(1).getTitle());

                Assertions.assertEquals("RegularTaskDesc", taskManager.getTask(1).getDescription());
        }


        @Test
        public void shouldReturnCorrectSubTaskInfoAndEpicStatus() {
                EpicTask testEpicTask = new EpicTask("testEpicTitle", "TestEpicDesc");
                taskManager.addEmptyEpicTask(testEpicTask);

                SubTask testSubTaskFirst = new SubTask("FirstSubTaskDesc", "FirstSubTaskDesc", 1);
                taskManager.addSubTask(testSubTaskFirst);

                Assertions.assertEquals(1, taskManager.getSubtask(2).getBoundedTo());
                Assertions.assertEquals("NEW", taskManager.getEpic(1).getStatus());
                Assertions.assertEquals("NEW", taskManager.getSubtask(2).getStatus());
                Assertions.assertEquals("FirstSubTaskDesc", taskManager.getSubtask(2).getTitle());
                Assertions.assertEquals("FirstSubTaskDesc", taskManager.getSubtask(2).getDescription());
        }

        @Test
        public void shouldBeNullForWrongTaskId() {
                Assertions.assertNull(taskManager.getTask(1));
        }

        @DisplayName("Should return correct edited task info")
        @Test
        public void shouldReturnCorrectEditedTaskInfo(){
                Task newTask = new Task("newTaskTitle", "newTaskDesc");

                taskManager.addRegularTask(newTask);
                Assertions.assertEquals(1, taskManager.getTask(newTask.getId()).getId());
                Assertions.assertEquals("newTaskTitle", taskManager.getTask(newTask.getId()).getTitle());
                Assertions.assertEquals("newTaskDesc", taskManager.getTask(newTask.getId()).getDescription());

                Task editingTask = taskManager.getTask(1);
                editingTask.setTitle("editedTitle");
                editingTask.setDescription("editedDescription");

                taskManager.addRegularTask(editingTask);
                Assertions.assertEquals(1, taskManager.getTask(newTask.getId()).getId());
                Assertions.assertEquals("editedTitle", taskManager.getTask(newTask.getId()).getTitle());
                Assertions.assertEquals("editedDescription", taskManager.getTask(newTask.getId()).getDescription());
        }

        @DisplayName("Should delete correctly")
        @Test
        public void shouldDeleteCorrect(){
                Task newTask = new Task("newTaskTitle", "newTaskDesc");
                Task anotherTask = new Task("anotherTaskTitle", "anotherTaskDesc");

                taskManager.addRegularTask(newTask);
                taskManager.addRegularTask(anotherTask);

                EpicTask newEpicTask = new EpicTask("newEpicTaskTitle", "newEpicTaskDesc");

                taskManager.addEmptyEpicTask(newEpicTask);

                EpicTask anotherEpicTask = new EpicTask("anotherEpicTaskTitle", "anotherEpicTaskDesc");
                SubTask newSubTask = new SubTask("newSubTaskTitle", "newSubTaskDesc", 4);
                SubTask anotherSubTask = new SubTask("anotherSubTaskTitle", "anotherSubTaskDesc", 4);

                ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
                subTaskArrayList.add(newSubTask);
                subTaskArrayList.add(anotherSubTask);

                taskManager.addEpicTask(anotherEpicTask, subTaskArrayList);

                taskManager.deleteTaskByID(1);
                Assertions.assertNull(taskManager.getTask(1));

                taskManager.deleteAllTasks();
                Assertions.assertNull(taskManager.getTask(2));
                Assertions.assertNull(taskManager.getTask(3));
                Assertions.assertNull(taskManager.getTask(4));
                Assertions.assertNull(taskManager.getTask(5));
                Assertions.assertNull(taskManager.getTask(6));
        }
}
