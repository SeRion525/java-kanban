package model;

public class SubTask extends Task{
    private int epicTaskId;

    public SubTask(String title, String description, Status status, int epicTaskId) {
        super(title, description, status);
        this.epicTaskId = epicTaskId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
    }

    public int getEpicTaskId() {
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
