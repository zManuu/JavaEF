package example;

import javaEF.Entity;

public class Player extends Entity {

    public int id;
    public String name;
    public long ip;
    public boolean online;

    public Player(int id, String name, long ip, boolean online) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public void setIp(long ip) {
        this.ip = ip;
    }
}
