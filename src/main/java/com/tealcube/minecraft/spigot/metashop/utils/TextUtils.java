/*
 * This file is part of Strife, licensed under the ISC License.
 *
 * Copyright (c) 2015 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package com.tealcube.minecraft.spigot.metashop.utils;

import com.tealcube.minecraft.spigot.metashop.collections.CaselessMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class containing a few useful methods for dealing with text.
 */
public final class TextUtils {

    private static final Map<String, ChatColor> COLOR_MAP = new CaselessMap<>();

    static {
        for (ChatColor cc : ChatColor.values()) {
            String name = cc.name();
            COLOR_MAP.put("<" + name + ">", cc);
            COLOR_MAP.put("<" + name.replace("_", " ") + ">", cc);
            COLOR_MAP.put("<" + name.replace("_", "") + ">", cc);
            COLOR_MAP.put(cc.toString().replace('\u00A7', '&'), cc);
        }
    }

    private TextUtils() {
        // do nothing
    }

    public static ChatColor convertTag(String string) {
        for (Map.Entry<String, ChatColor> entry : COLOR_MAP.entrySet()) {
            if (entry.getKey().equals(string)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static String convertTag(ChatColor chatColor) {
        for (Map.Entry<String, ChatColor> entry : COLOR_MAP.entrySet()) {
            if (entry.getValue() == chatColor) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Returns a colored copy of the passed-in {@code List<String>}.
     *
     * @param pList List to color
     * @return colored copy of passed-in List
     */
    public static List<String> color(List<String> pList) {
        Validate.notNull(pList, "pList cannot be null");
        List<String> rList = new ArrayList<>();
        for (String s : pList) {
            rList.add(color(s));
        }
        return rList;
    }

    public static List<String> decolor(List<String> pList) {
        Validate.notNull(pList, "pList cannot be null");
        List<String> rList = new ArrayList<>();
        for (String s : pList) {
            rList.add(decolor(s));
        }
        return rList;
    }

    /**
     * Returns a colored copy of the passed-in String.
     *
     * @param pString String to color
     * @return colored copy of passed-in String
     */
    public static String color(String pString) {
        Validate.notNull(pString, "pString cannot be null");
        String ret = pString;
        for (Map.Entry<String, ChatColor> entry : COLOR_MAP.entrySet()) {
            ret = StringUtils.replace(ret, entry.getKey(), entry.getValue() + "");
        }
        return ret;
    }

    public static String decolor(String pString) {
        Validate.notNull(pString, "pString cannot be null");
        String ret = pString;
        for (ChatColor cc : ChatColor.values()) {
            ret = StringUtils.replace(ret, cc + "", convertTag(cc));
        }
        return ret;
    }

    /**
     * Returns a copy of the passed-in List with arguments replaced.
     *
     * @param pList List to replace arguments
     * @param args  Arguments to replace in List
     * @return copy of the passed-in List with arguments replaced
     */
    public static List<String> args(List<String> pList, String[][] args) {
        Validate.notNull(pList, "pList cannot be null");
        Validate.notNull(args, "args cannot be null");
        List<String> ret = new ArrayList<>();
        for (String s : pList) {
            ret.add(args(s, args));
        }
        return ret;
    }

    /**
     * Returns a copy of the passed-in String with arguments replaced. The below example will return "I like apples".
     * <pre>
     * <code>String val = TextUtils.args("I like %fruit%", new String[][]{{"%fruit%", "apples"}});</code>
     * </pre>
     *
     * @param pString String to replace arguments
     * @param args    Arguments to replace in String
     * @return copy of the passed-in String with arguments replaced
     */
    public static String args(String pString, String[][] args) {
        Validate.notNull(pString, "pString cannot be null");
        Validate.notNull(args, "args cannot be null");
        String ret = pString;
        for (String[] arg : args) {
            if (arg.length < 2) {
                continue;
            }
            ret = StringUtils.replace(ret, arg[0], arg[1]);
        }
        return ret;
    }

    /**
     * Returns the Levenshtein Distance between the two given Strings.
     *
     * @param str1 First String
     * @param str2 Second String
     * @return Levenshtein Distance
     */
    public static int levenshteinDistance(String str1, String str2) {
        Validate.notNull(str1, "str1 cannot be null");
        Validate.notNull(str2, "str2 cannot be null");

        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 1; j <= str2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                distance[i][j] = NumberUtils.min(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
            }
        }

        return distance[str1.length()][str2.length()];
    }

}
