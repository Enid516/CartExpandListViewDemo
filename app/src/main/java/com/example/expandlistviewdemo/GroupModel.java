package com.example.expandlistviewdemo;

import java.util.List;

/**
 * Created by enid on 2016/3/24.
 */
public class GroupModel {
    private int id;
    private String groupName;

    private boolean isCheck;

    private List<ChildModel> childs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<ChildModel> getChilds() {
        return childs;
    }

    public void setChilds(List<ChildModel> childs) {
        this.childs = childs;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
