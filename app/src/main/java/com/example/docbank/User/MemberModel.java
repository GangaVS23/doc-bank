package com.example.docbank.User;

public class MemberModel {
    String mid,name,relation,uId;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public MemberModel(String mid, String name, String relation, String uId) {
        this.mid = mid;
        this.name = name;
        this.relation = relation;
        this.uId = uId;
    }
}
