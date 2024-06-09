package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicTaskId;

    public SubTask(String title, String description, Status status, int epicTaskId) {
        super(title, description, status);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(String title, String description, int id, Status status, LocalDateTime startTime, Duration duration,
                   int epicTaskId) {
        super(title, description, id, status, startTime, duration);
        this.epicTaskId = epicTaskId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
    }

    @Override
    public Integer getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        return "SubTask{epicId=" + epicTaskId +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
