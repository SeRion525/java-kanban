package service;

import adapter.DurationAdapter;
import adapter.LocalDataTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import handler.EpicTaskHttpHandler;
import handler.ErrorHandler;
import handler.HistoryHttpHandler;
import handler.PrioritizedHttpHandler;
import handler.SubTaskHttpHandler;
import handler.TaskHttpHandler;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    private final TaskHttpHandler taskHandler;
    private final EpicTaskHttpHandler epicTaskHandler;
    private final SubTaskHttpHandler subTaskHandler;
    private final HistoryHttpHandler historyHandler;
    private final PrioritizedHttpHandler prioritizedHandler;

    public HttpTaskServer() {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = getGson();
        this.errorHandler = new ErrorHandler(gson);
        try {
            this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка запуска сервера", exception);
        }
        this.taskHandler = new TaskHttpHandler(taskManager, gson);
        this.epicTaskHandler = new EpicTaskHttpHandler(taskManager, gson);
        this.subTaskHandler = new SubTaskHttpHandler(taskManager, gson);
        this.historyHandler = new HistoryHttpHandler(taskManager, gson);
        this.prioritizedHandler = new PrioritizedHttpHandler(taskManager, gson);

        server.createContext("/tasks", this::taskHandler);
        server.createContext("/subtasks", this::subTaskHandler);
        server.createContext("/epics", this::epicsHandler);
        server.createContext("/history", this::historyHandler);
        server.createContext("/prioritized", this::prioritizedHandler);
    }

    private void taskHandler(HttpExchange exchange) {
        try (exchange) {
            try {
                taskHandler.handle(exchange);
            } catch (Exception exception) {
                errorHandler.handle(exchange, exception);
            }
        }
    }

    private void subTaskHandler(HttpExchange exchange) {
        try (exchange) {
            try {
                subTaskHandler.handle(exchange);
            } catch (Exception exception) {
                errorHandler.handle(exchange, exception);
            }
        }
    }

    private void epicsHandler(HttpExchange exchange) {
        try (exchange) {
            try {
                epicTaskHandler.handle(exchange);
            } catch (Exception exception) {
                errorHandler.handle(exchange, exception);
            }
        }
    }

    private void historyHandler(HttpExchange exchange) {
        try (exchange) {
            try {
                historyHandler.handle(exchange);
            } catch (Exception exception) {
                errorHandler.handle(exchange, exception);
            }
        }
    }

    private void prioritizedHandler(HttpExchange exchange) {
        try (exchange) {
            try {
                prioritizedHandler.handle(exchange);
            } catch (Exception exception) {
                errorHandler.handle(exchange, exception);
            }
        }
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDataTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void start() {
        System.out.println("Starting TaskServer on port " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Stopped TaskServer on port " + PORT);
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}
