package util;

import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldReturnNotNull() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();

        assertNotNull(tm, "Task Manager не инициализирован.");
        assertNotNull(hm, "History Manager не инициализирован.");
    }
}