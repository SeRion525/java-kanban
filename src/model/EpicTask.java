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

    public Integer getSubTask(int id) {
        int index = subTasksId.indexOf(id);
        return subTasksId.get(index);
    }

    public void addSubTaskId(int id) {
        subTasksId.add(id);
    }

    public void removeSubTask(int id) {
        int index = subTasksId.indexOf(id);
        subTasksId.remove(index);
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
