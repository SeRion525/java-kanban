package util;

import model.Task;

public class TaskToStringConverter {
    public static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%d",
                task.getId(), task.getType(), task.getTitle(),
                task.getStatus(), task.getDescription(), task.getEpicTaskId());
    }
}
