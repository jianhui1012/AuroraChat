package com.golike.customviews.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by admin on 2017/8/16.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProviderTag {

    boolean showPortrait() default true;

    boolean centerInHorizontal() default false;

    boolean hide() default false;

    boolean showWarning() default true;

    boolean showProgress() default true;

    boolean showSummaryWithName() default true;

    boolean showReadState() default false;

    Class<? extends MessageContent> messageContent();
}
