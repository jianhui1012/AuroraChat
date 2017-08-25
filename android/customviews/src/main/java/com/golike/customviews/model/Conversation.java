package com.golike.customviews.model;

/**
 * Created by admin on 2017/8/8.
 */

public class Conversation  {


    public static enum ConversationType {
        NONE(0, "none"),
        PRIVATE(1, "private"),
        DISCUSSION(2, "discussion"),
        GROUP(3, "group"),
        CHATROOM(4, "chatroom"),
        CUSTOMER_SERVICE(5, "customer_service"),
        SYSTEM(6, "system"),
        APP_PUBLIC_SERVICE(7, "app_public_service"),
        PUBLIC_SERVICE(8, "public_service"),
        PUSH_SERVICE(9, "push_service");

        private int value = 1;
        private String name = "";

        private ConversationType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }

        public static Conversation.ConversationType setValue(int code) {
            Conversation.ConversationType[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Conversation.ConversationType c = arr$[i$];
                if(code == c.getValue()) {
                    return c;
                }
            }

            return PRIVATE;
        }
    }

}
