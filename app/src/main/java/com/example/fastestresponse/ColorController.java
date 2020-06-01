package com.example.fastestresponse;

public class ColorController {

    private int colourResourceId;

    //going to be used to get the colour of arrow set by the user.
    public ColorController(int id, String colourName, int colourResourceId) {
        this.colourResourceId = colourResourceId;
    }

    public int getColourResourceId() {
        return colourResourceId;
    }
}
