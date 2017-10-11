package com.golike.customviews;

/**
 * Created by admin on 2017/8/8.
 */
public class InputBar {
    public InputBar() {
    }

    public enum Type {
        TYPE_DEFAULT,
        TYPE_CS_ROBOT,
        TYPE_CS_HUMAN,
        TYPE_CS_ROBOT_FIRST,
        TYPE_CS_HUMAN_FIRST;

        Type() {
        }
    }

    public enum Style {
        STYLE_SWITCH_CONTAINER_EXTENSION(291),
        STYLE_SWITCH_CONTAINER(288),
        STYLE_CONTAINER_EXTENSION(35),
        STYLE_EXTENSION_CONTAINER(800),
        STYLE_CONTAINER(32),
        STYLE_EMOTION(23);

        int v;

        Style(int v) {
            this.v = v;
        }

        public static InputBar.Style getStyle(int v) {
            InputBar.Style result = null;
            InputBar.Style[] arr$ = values();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                InputBar.Style style = arr$[i$];
                if (style.v == v) {
                    result = style;
                    break;
                }
            }

            return result;
        }
    }
}