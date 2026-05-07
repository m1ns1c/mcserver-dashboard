package com.minsic.mcserver_dashboard;

public class PlayerStats {
    private String nickname;
    private String lastSeen;
    private String totalPlayTime;
    private int loginCount;

    public PlayerStats(String nickname, String lastSeen, String totalPlayTime, int loginCount) {
        this.nickname = nickname;
        this.lastSeen = lastSeen;
        this.totalPlayTime = totalPlayTime;
        this.loginCount = loginCount;
    }

    public String getNickname() { return nickname; }
    public String getLastSeen() { return lastSeen; }
    public String getTotalPlayTime() { return totalPlayTime; }
    public int getLoginCount() { return loginCount; }
}