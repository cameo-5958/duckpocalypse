package moe.cameo.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moe.cameo.render.Widget;

public class Menu {
    // Create a list of Widgets and a 
    // method that exposes them
    private final List<Widget> MENU_WIDGETS = new ArrayList<>();

    public void add(Widget w) {
        MENU_WIDGETS.add(w);
    }

    public void addAll(Widget ...w) {
        MENU_WIDGETS.addAll(Arrays.asList(w));
    }

    public List<Widget> get() {
        return MENU_WIDGETS;
    }
}
