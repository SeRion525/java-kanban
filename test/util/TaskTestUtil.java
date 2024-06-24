package util;

import model.EpicTask;
import model.SubTask;
import model.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTestUtil {

    public static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getTitle(), actual.getTitle(), message + ", title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
        if (expected.getStartTime() != null) {
            assertEquals(expected.getStartTime(), actual.getStartTime(), message + ", startTime");
            assertEquals(expected.getDuration(), actual.getDuration(), message + ", duration");
        }
    }

    public static void assertEqualsSubTask(SubTask expected, SubTask actual, String message) {
        assertEqualsTask(expected, actual, message);
        assertEquals(expected.getEpicTaskId(), actual.getEpicTaskId(), message + ", epicTaskId");
    }

    public static void assertEqualsEpicTask(EpicTask expected, EpicTask actual, String message) {
        assertEqualsTask(expected, actual, message);
        assertEquals(expected.getSubTasksId(), actual.getSubTasksId(), message + ", epicTaskId");
    }

    public static Task copyTask(Task task) {
        Task copy;

        switch (task.getType()) {
            case TASK:
                if (task.getStartTime() != null) {
                    copy = new Task(task.getTitle(), task.getDescription(), task.getStatus(), task.getStartTime(), task.getDuration());
                } else {
                    copy = new Task(task.getTitle(), task.getDescription(), task.getStatus());
                }
                break;
            case EPIC_TASK:
                EpicTask epicTask = (EpicTask) task;
                EpicTask epicTaskCopy = new EpicTask(task.getTitle(), task.getDescription());
                epicTask.getSubTasksId().stream()
                        .forEach(epicTaskCopy::addSubTaskId);

                copy = epicTaskCopy;
                break;
            case SUB_TASK:
                SubTask subTask = (SubTask) task;
                if (task.getStartTime() != null) {
                    copy = new SubTask(subTask.getTitle(), subTask.getDescription(), subTask.getStatus(), subTask.getEpicTaskId(), subTask.getStartTime(),
                            subTask.getDuration());
                } else {
                    copy = new SubTask(subTask.getTitle(), subTask.getDescription(), subTask.getStatus(), subTask.getEpicTaskId());
                }
                break;
            default:
                copy = null;
        }

        copy.setId(task.getId());
        return copy;
    }
}
