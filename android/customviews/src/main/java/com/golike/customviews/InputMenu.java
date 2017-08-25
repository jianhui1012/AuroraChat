package com.golike.customviews;

import java.util.List;

/**
 * Created by admin on 2017/8/8.
 */

public class InputMenu {
    public String title;
    public List<String> subMenuList;

    public InputMenu() {
    }

    public static InputMenu obtain() {
        return new InputMenu();
    }
}

