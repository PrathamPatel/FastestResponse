package com.example.fastestresponse;

import android.annotation.SuppressLint;
import android.content.Context;

public class WhichColour {

    @SuppressLint("StaticFieldLeak")
    private static WhichColour instance;

    private static ColorController[] colours;
    private static int index = 0;

    //setting the chosen colour by the user to the controller
    public static int COLOUR_BLUE = 0;
    public static int COLOUR_RED = 1;
    public static int COLOUR_GREEN = 2;
    public static int COLOUR_GREYISH = 3;

    private WhichColour(Context ctxt) {
        index = 0;
        colours = new ColorController[] {
                new ColorController(COLOUR_BLUE, "BLUE", ctxt.getColor(R.color.arrow_blue)),
                new ColorController(COLOUR_RED, "RED", ctxt.getColor(R.color.arrow_red)),
                new ColorController(COLOUR_GREEN, "GREEN", ctxt.getColor(R.color.arrow_green)),
                new ColorController(COLOUR_GREYISH, "GREY", ctxt.getColor(R.color.arrow_greyish))
        };
    }
    public static WhichColour getInstance(Context context) {
        //if empty instance, create a new instance
        if (instance == null)
            instance = new WhichColour(context);
        return instance;
    }

    //setting the colour id and index
    public int getCurrentColourId() {
        return colours[index].getColourResourceId();
    }

    public void setIndex(int index) {
        WhichColour.index = index;
    }

}
