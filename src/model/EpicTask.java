package model;

import java.util.*;

public class EpicTask extends Task {
    private final List<Integer> subTasksId;

    public EpicTask(String title, String description, int id) {
        super(title, description, id, Status.NEW);
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

    public void removeAllSubTasks() {
        subTasksId.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subTasksId=" + subTasksId +
                '}';
    }
}
