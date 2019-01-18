package com.appnava.incomingcalllock;

public class PeopleObject {
    private String contactName;
    private String contactNo;
    private String image;
    private boolean selected;

    public String getName() {
        return this.contactName;
    }

    public void setName(String contactName) {
        this.contactName = contactName;
    }

    public String getNumber() {
        return this.contactNo;
    }

    public void setNumber(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
