package ru.otus.java.basic.homeworks.pw.server;

import java.time.LocalDateTime;
import java.util.List;

public interface AuthenticatedProvider {
    boolean authenticate(ChatClientHandler handler, String login, String password);
    boolean registration(ChatClientHandler handler, String login, String password, String username);
    boolean changeNickname(ChatClientHandler handler, String newNick);

    void setAdmin(String username);
    void removeAdmin(String username);

    boolean addBan(String usernameToBan, LocalDateTime banEnd, String reason);
    BanInfo getBanInfo(String username);
    boolean removeBan(String usernameToUnban);
    Integer getInviterId(String roomName, int inviteeId);
    String getUsernameById(int userId);
    int getUserIdByUsername(String username);
    String getUsernameByLogin(String login);

    Integer getRoomIdByName(String name);
    boolean createRoom(String name, String password, int ownerId);
    boolean enterRoom(ChatClientHandler client, String roomName, String password);
    void leaveCurrentRoom(ChatClientHandler client);
    boolean deleteRoom(String roomName, int userId);
    boolean isRoomOwner(String roomName, int userId);

    void handleChatMessage(ChatClientHandler sender, String message);
    void loadChatHistory(ChatClientHandler client, String roomName);

    boolean inviteUserToRoom(String roomName, String inviteeUsername, int inviterId);
    List<String> getInvitesForUser(int userId);
    void removeInvite(String roomName, int userId);

    List<String> getAllPublicRooms();
    List<String> getUserCreatedRooms(int userId);
    List<String> getUserInvitedRooms(int userId);
    int getUserRoomCount(int userId);


    void updateUserActivity(int userId);
    String getLastActivity(String username);
    String getLastRoomName(int userId);
    void updateLastRoomName(int userId, String roomName);
    void deleteOldRooms();
    String getRoomInfo(String roomName);
}
