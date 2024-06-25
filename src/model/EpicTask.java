package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> subTasksId;

    public EpicTask(String title, String description) {
        super(title, description, Status.NEW);
        this.subTasksId = new ArrayList<>();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC_TASK;
    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void addSubTaskId(int id) {
        subTasksId.add(id);
    }

    public void removeSubTask(int id) {
        subTasksId.remove(Integer.valueOf(id));
    }

    public void setStartTime(LocalDateTime startTime) {
        super.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        super.duration = duration;
    }

    public void setEndTime(LocalDateTime endTime) {
        super.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{id=" + id +
                ", status=" + status +
                ", subTasksId=" + subTasksId +
                '}';
    }
}
