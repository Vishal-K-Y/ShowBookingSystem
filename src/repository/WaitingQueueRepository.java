package repository;

import pojo.BookingStatus;

import java.util.LinkedList;
import java.util.Queue;

public class WaitingQueueRepository {
    private final Queue<BookingStatus> waitingQueue = new LinkedList<>();

    public void enqueue(BookingStatus status) {
        waitingQueue.offer(status);
    }

    public BookingStatus dequeue() {
        return waitingQueue.poll();
    }

    public boolean isEmpty() {
        return waitingQueue.isEmpty();
    }
}

