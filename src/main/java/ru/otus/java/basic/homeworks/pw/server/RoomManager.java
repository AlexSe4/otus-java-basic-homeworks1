package ru.otus.java.basic.homeworks.pw.server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {
    private final AuthenticatedProvider auth;
    private final ConcurrentHashMap<String, Integer> roomCache = new ConcurrentHashMap<>();

    public RoomManager(AuthenticatedProvider auth) {
        this.auth = auth;
        for (String name : auth.getAllPublicRooms()) {
            roomCache.put(name, auth.getRoomIdByName(name));
        }
        roomCache.putIfAbsent("Общая комната", auth.getRoomIdByName("Общая комната"));
    }
    //создает комнату
    public boolean createRoom(String name, String password, int ownerId) {
        if (roomCache.containsKey(name)) return false;
        boolean ok = auth.createRoom(name, password, ownerId);
        if (ok) roomCache.put(name, auth.getRoomIdByName(name));
        return ok;
    }

    public void syncAfterDeletion(List<ChatClientHandler> clients) {
        auth.deleteOldRooms();
        roomCache.clear();
        for (String name : auth.getAllPublicRooms()) {
            roomCache.put(name, auth.getRoomIdByName(name));
        }
        roomCache.putIfAbsent("Общая комната", auth.getRoomIdByName("Общая комната"));
        for (ChatClientHandler c : clients) {
            String cur = c.getCurrentRoom();
            if (!roomCache.containsKey(cur)) {
                c.setCurrentRoom("Общая комната");
                c.sendMsg("Ваша комната была удалена. Вы переведены в общую комнату.");
                auth.loadChatHistory(c, "Общая комната");
            }
        }
    }
}
