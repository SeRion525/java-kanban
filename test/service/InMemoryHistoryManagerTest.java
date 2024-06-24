package service;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {
    @Override
    protected InMemoryHistoryManager createHistory() {
        return new InMemoryHistoryManager();
    }
}