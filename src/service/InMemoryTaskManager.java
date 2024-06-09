package service;

import exception.NotFoundException;
import exception.ValidationException;
import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;
import util.Managers;
import util.TasksStartTimeComparator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int allTaskCount = 0;
    protected final Map<Integer, Task> tasksById;
    protected final Map<Integer, EpicTask> epicTasksById;
    protected final Map<Integer, SubTask> subTasksById;
    protected final Set<Task> sortedTasksAndSubTasks;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasksById = new HashMap<>();
        this.epicTasksById = new HashMap<>();
        this.subTasksById = new HashMap<>();
        this.sortedTasksAndSubTasks = new TreeSet<>(new TasksStartTimeComparator());
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasksById.values());
    }

    @Override
    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasksById.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasksById.values());
    }

    @Override
    public List<SubTask> getSubTasksFromEpicTaskId(int epicTaskId) {
        EpicTask epicTask = epicTasksById.get(epicTaskId);

        if (epicTask != null) {
            return epicTask.getSubTasksId().stream()
                    .map(subTasksById::get)
                    .toList();
        }

        return null;
    }

    public List<Task> getPrioritizedTasks() {
        return sortedTasksAndSubTasks.stream().toList();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasksById.get(id);
        if (task == null) {
            throw new NotFoundException("Не найдена задача: " + id);
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask epicTask = epicTasksById.get(id);
        if (epicTask == null) {
            throw new NotFoundException("Не найден эпик: " + id);
        }
        historyManager.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasksById.get(id);
        if (subTask == null) {
            throw new NotFoundException("Не найдена подзадача: " + id);
        }
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void createTask(Task task) {
        int taskId = giveId();
        task.setId(taskId);

        if (task.getStartTime() != null) {
            if (getPrioritizedTasks().stream().noneMatch((savedTask -> isTimeIntersection(task, savedTask)))) {
                sortedTasksAndSubTasks.add(task);
            } else {
                throw new ValidationException("Пересечение по времени у задачи " + task.getId());
            }
        }

        tasksById.put(taskId, task);


    }

    @Override
    public void createEpicTask(EpicTask epicTask) {
        int taskId = giveId();
        epicTask.setId(taskId);
        epicTasksById.put(taskId, epicTask);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        int taskId = giveId();
        subTask.setId(taskId);

        if (subTask.getStartTime() != null) {
            if (getPrioritizedTasks().stream().noneMatch((savedTask -> isTimeIntersection(subTask, savedTask)))) {
                sortedTasksAndSubTasks.add(subTask);
            } else {
                throw new ValidationException("Пересечение по времени у подзадачи " + subTask.getId());
            }
        }

        subTasksById.put(taskId, subTask);

        EpicTask epicTask = epicTasksById.get(subTask.getEpicTaskId());
        epicTask.addSubTaskId(subTask.getId());
        updateEpicTaskTime(epicTask.getId());

    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasksById.get(task.getId());

        if (task.getStartTime() != null) {
            if (getPrioritizedTasks().stream().noneMatch((savedTask -> isTimeIntersection(task, savedTask)))) {
                sortedTasksAndSubTasks.remove(oldTask);
                sortedTasksAndSubTasks.add(task);
            } else {
                throw new ValidationException("Пересечение по времени у задачи " + task.getId());
            }
        }

        tasksById.put(task.getId(), task);
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        epicTasksById.put(epicTask.getId(), epicTask);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask oldSubTask = subTasksById.get(subTask.getId());

        if (subTask.getStartTime() != null) {
            if (getPrioritizedTasks().stream().noneMatch((savedTask -> isTimeIntersection(subTask, savedTask)))) {
                sortedTasksAndSubTasks.remove(oldSubTask);
                sortedTasksAndSubTasks.add(subTask);
            } else {
                throw new ValidationException("Пересечение по времени у подзадачи " + subTask.getId());
            }
        }

        subTasksById.put(subTask.getId(), subTask);

        EpicTask epicTask = epicTasksById.get(subTask.getEpicTaskId());
        if (epicTask == null) {
            throw new NotFoundException("Не найден эпик: " + subTask.getEpicTaskId());
        }
        updateEpicTaskStatus(epicTask.getId());
        updateEpicTaskTime(epicTask.getId());
    }

    @Override
    public void removeAllTasks() {
        tasksById.keySet().stream()
                .peek(historyManager::remove)
                .peek(id -> sortedTasksAndSubTasks.remove(tasksById.get(id)))
                .close();

        tasksById.clear();
    }

    @Override
    public void removeAllEpicTasks() {
        epicTasksById.keySet().stream()
                .peek(historyManager::remove)
                .close();

        subTasksById.keySet().stream()
                .peek(historyManager::remove)
                .peek(id -> sortedTasksAndSubTasks.remove(subTasksById.get(id)))
                .close();

        epicTasksById.clear();
        subTasksById.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (SubTask subTask : subTasksById.values()) {

            EpicTask epicTask = epicTasksById.get(subTask.getEpicTaskId());

            epicTask.removeSubTask(subTask.getId());
            updateEpicTaskStatus(epicTask.getId());
            updateEpicTaskTime(epicTask.getId());

            historyManager.remove(subTask.getId());
            sortedTasksAndSubTasks.remove(subTask);
        }

        subTasksById.clear();
    }

    @Override
    public void removeTask(int id) {
        Task task = tasksById.remove(id);

        if (task != null) {
            historyManager.remove(id);
            sortedTasksAndSubTasks.remove(task);

        } else if ((task = epicTasksById.remove(id)) != null) {
            EpicTask epicTask = (EpicTask) task;
            for (int subTaskId : epicTask.getSubTasksId()) {
                sortedTasksAndSubTasks.remove(subTasksById.remove(subTaskId));
                historyManager.remove(subTaskId);
            }
            historyManager.remove(id);

        } else if ((task = subTasksById.remove(id)) != null) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = epicTasksById.get(subTask.getEpicTaskId());

            epicTask.removeSubTask(subTask.getId());
            updateEpicTaskStatus(epicTask.getId());
            updateEpicTaskTime(epicTask.getId());
            historyManager.remove(id);
            sortedTasksAndSubTasks.remove(subTask);
        }
    }

    private int giveId() {
        int id = allTaskCount;
        allTaskCount++;
        return id;
    }

    private void updateEpicTaskStatus(int id) {
        EpicTask epicTask = epicTasksById.get(id);
        List<Integer> subTasksIdList = epicTask.getSubTasksId();

        if (subTasksIdList.isEmpty()) {
            epicTask.setStatus(Status.NEW);
            return;
        }

        boolean allSubTasksIsNew = true;
        boolean allSubTasksIsDone = true;

        for (int subTaskId : subTasksIdList) {
            Status subTaskStatus = subTasksById.get(subTaskId).getStatus();

            if (Status.NEW.equals(subTaskStatus) && allSubTasksIsNew) {
                allSubTasksIsDone = false;
            } else if (Status.DONE.equals(subTaskStatus) && allSubTasksIsDone) {
                allSubTasksIsNew = false;
            } else {
                allSubTasksIsNew = false;
                allSubTasksIsDone = false;
                break;
            }
        }

        if (allSubTasksIsNew) {
            epicTask.setStatus(Status.NEW);
        } else if (allSubTasksIsDone) {
            epicTask.setStatus(Status.DONE);
        } else {
            epicTask.setStatus(Status.IN_PROGRESS);
        }

    }

    private void updateEpicTaskTime(int id) {
        EpicTask epicTask = epicTasksById.get(id);
        List<Integer> subTasksIdList = epicTask.getSubTasksId();

        if (subTasksIdList.isEmpty()) {
            return;
        }

        Duration sumDuration = Duration.ZERO;
        LocalDateTime epicStartTime = LocalDateTime.MAX;
        LocalDateTime epicEndTime = LocalDateTime.MIN;

        for (Integer subTaskId : subTasksIdList) {
            SubTask subTask = subTasksById.get(subTaskId);

            if (subTask.getStartTime() == null) {
                continue;
            }

            if (subTask.getStartTime().isBefore(epicStartTime)) {
                epicStartTime = subTask.getStartTime();
            }

            if (subTask.getEndTime().isAfter(epicEndTime)) {
                epicEndTime = subTask.getEndTime();
            }

            sumDuration = sumDuration.plus(subTask.getDuration());
        }

        if (epicStartTime.equals(LocalDateTime.MAX)) {
            return;
        }

        epicTask.setStartTime(epicStartTime);
        epicTask.setDuration(sumDuration);
        epicTask.setEndTime(epicEndTime);
    }

    private boolean isTimeIntersection(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        return startTime1.equals(startTime2) ||
                (startTime1.isAfter(startTime2) && startTime1.isBefore(endTime2)) ||
                (startTime1.isBefore(startTime2) && endTime1.isAfter(startTime2));
    }
}
