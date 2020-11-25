/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bewsoftware.mdj.core;

/**
 *
 * @author Bradley Willcott
 */
public final class Attributes {

    private Attributes() {
    }

    public static String addClass(String text) {
        if (text == null || text.isBlank()) {
            return "";
        } else {
            return " class=\"" + text + "\"";
        }
    }

    public static String addId(String text) {
        if (text == null || text.isBlank()) {
            return "";
        } else {
            return " id=\"" + text + "\"";
        }
    }

    public static String addStyle(String text) {
        if (text == null || text.isBlank()) {
            return "";
        } else {
            return " style=\"" + text + "\"";
        }
    }
}
