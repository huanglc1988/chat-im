package com.hantang.chat.model;

import java.util.List;

/**
 * @author HuangLongcan
 * @since 2017-05-14
 */
public class ImMessage {

    private int type; //消息类型 1-登录消息 2-点对点消息 3-广播消息 4-在线用户列表 5-用户登录 6-用户退出登录

    private String content; //消息内容

    private int status = 0;

    private long from; //消息来源人

    private long to; //消息目标人

    private String name;

    private List<User> users; //用户列表

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
