package model;

public class SubTask extends Task{
    private EpicTask epicTask;

    public SubTask(String title, String description, int id, Status status, EpicTask epicTask) {
        super(title, description, id, status);
        this.epicTask = epicTask;
    }

    public EpicTask getEpic() {
        return epicTask;
    }

    public void setEpicTask(EpicTask epicTask) {
        this.epicTask = epicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicTask.getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
