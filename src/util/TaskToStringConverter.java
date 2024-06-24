package util;

import model.Task;

public class TaskToStringConverter {
    public static String toString(Task task) {
        if (task.getStartTime() != null) {
            return String.format("%d,%s,%s," +
                            "%s,%s,%d," +
                            "%d,%s",
                    task.getId(), task.getType(), task.getTitle(),
                    task.getStatus(), task.getDescription(), task.getEpicTaskId(),
                    task.getDuration().toMinutes(), task.getStartTime());
        } else {
            return String.format("%d,%s,%s," +
                            "%s,%s,%d," +
                            "%s,%s",
                    task.getId(), task.getType(), task.getTitle(),
                    task.getStatus(), task.getDescription(), task.getEpicTaskId(),
                    null, null);
        }
    }
}
