package org.openjfx;

public class NameCutter {
    public static String cut(String word){
        String name2=null;
        int nm = 0;
        for (int i = 0; i < word.length(); i++) {

            if (word.charAt(i) == 92) {
                nm = i;
            }
        }
        name2 = word.substring(nm + 1, word.length());
        return name2;
    }
}
