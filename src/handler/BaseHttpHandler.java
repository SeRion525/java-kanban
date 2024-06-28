package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_NOT_ACCEPTABLE = 406;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_BAD_GATEWAY= 502;
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    protected TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }

    protected void sendText(HttpExchange exchange, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, 0);
    }

    protected void sendNotFound(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }

    protected void sendBadRequest(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }
}
