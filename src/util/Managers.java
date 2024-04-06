package util;

import service.InMemoryTaskManager;
import service.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
