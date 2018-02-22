package com.rotaractnepalapp.rotraconversation;

/**
 * Created by bskes on 2/22/2018.
 */

public class Users {
    public String name;
    public String ridno;
    public String status;
    public String image;

    public Users(){

    }

    public Users(String name, String ridno, String status, String image) {
        this.name = name;
        this.ridno = ridno;
        this.status = status;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRidno() {
        return ridno;
    }

    public void setRidno(String ridno) {
        this.ridno = ridno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
