package ru.otus.java.basic.homeworks.pw.server;

import java.util.LinkedList;
import java.util.Queue;

public class SpamProtection {
    private final Queue<Long> queue = new LinkedList<>();
    private final int maxMessages;
    private final int intervalSec;

    public SpamProtection(int maxMessages, int intervalSec) {
        this.maxMessages = maxMessages;
        this.intervalSec = intervalSec;
    }
    //17	Защита от спама (5 сообщений за 10 сек)
    public synchronized boolean checkAndRecord() {
        long now = System.currentTimeMillis();
        long cutoff = now - intervalSec * 1000L;
        while (!queue.isEmpty() && queue.peek() < cutoff) {
            queue.poll();
        }
        if (queue.size() >= maxMessages) {
            return false;
        }
        queue.offer(now);
        return true;
    }
}
