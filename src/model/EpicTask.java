package model;

import java.util.*;

public class EpicTask extends Task {
    private final Map<Integer, SubTask> subTasksById;

    public EpicTask(String title, String description, int id, Status status) {
        super(title, description, id, status);
        this.subTasksById = new HashMap<>();
    }

    public List<SubTask> getSubTasks() {
        List<SubTask> subTasks = new ArrayList<>(subTasksById.values());
        return subTasks;
    }

    public Set<Integer> getSubTasksId() {
        return subTasksById.keySet();
    }

    public SubTask getSubTask(int id) {
        return subTasksById.get(id);
    }

    public void addSubTask(SubTask subTask) {
        subTasksById.put(subTask.getId(), subTask);
    }

    public void removeSubTask(SubTask subTask) {
        subTasksById.remove(subTask.getId());
    }

    public void removeAllSubTasks() {
        subTasksById.clear();
    }

    public void updateStatus() {
        if (subTasksById.isEmpty()) {
            status = Status.NEW;
            return;
        }

        boolean allSubTaskIsNew = true;
        boolean allSubTaskIsDone = true;
        for (SubTask subTask : subTasksById.values()) {
            if (Status.NEW.equals(subTask.getStatus()) && allSubTaskIsNew) {
                allSubTaskIsDone = false;
            } else if (Status.DONE.equals(subTask.getStatus()) && allSubTaskIsDone){
                allSubTaskIsNew = false;
            } else {
                allSubTaskIsNew = false;
                allSubTaskIsDone = false;
                break;
            }
        }

        if (allSubTaskIsNew) {
            this.status = Status.NEW;
        } else if (allSubTaskIsDone) {
            this.status = Status.DONE;
        } else {
            this.status = Status.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subTasksById=" + subTasksById +
                '}';
    }
}
