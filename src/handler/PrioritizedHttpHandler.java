package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHttpHandler extends BaseHttpHandler {

    public PrioritizedHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals(GET)) {
            handleGetMethod(exchange);
        } else {
            sendBadRequest(exchange, "Метод " + method + " не используется");
        }
    }

    private void handleGetMethod(HttpExchange exchange) throws IOException{
        List<Task> PrioritizedTasks = taskManager.getPrioritizedTasks();
        sendText(exchange, HTTP_OK, gson.toJson(PrioritizedTasks));
    }
}
