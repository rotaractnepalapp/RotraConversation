package com.rotaractnepalapp.rotraconversation;

/**
 * Created by bskes on 2/22/2018.
 */

public class Users {
    public String name;
    public String ridno;
    public String status;
    public String image;
    public String thumb_image;
    public String address;
    public String contactno;
    public String email;

    public Users(){

    }

    public Users(String name, String ridno, String status, String image, String thumb_image, String address, String contactno, String email) {
        this.name = name;
        this.ridno = ridno;
        this.status = status;
        this.image = image;
        this.thumb_image = thumb_image;
        this.address = address;
        this.contactno = contactno;
        this.email = email;
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

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
