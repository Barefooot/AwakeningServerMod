package net.spirangle.awakening.util;

import com.wurmonline.server.utils.StringUtil;

import java.text.DecimalFormat;


public class StringUtils extends StringUtil {

    public static final DecimalFormat decimalFormat = new DecimalFormat("##0.00");
    public static final DecimalFormat bigdecimalFormat = new DecimalFormat("###0.00##");
    public static final DecimalFormat longDecimalFormat = new DecimalFormat("##0.000000");
    public static final DecimalFormat intFormat = new DecimalFormat("##0");

    public static String capitalize(String string) {
        final int sl = string.length();
        final StringBuilder sb = new StringBuilder(sl);
        boolean lod = false;
        for(int s = 0; s<sl; ++s) {
            final int cp = string.codePointAt(s);
            sb.appendCodePoint(lod? Character.toLowerCase(cp) : Character.toUpperCase(cp));
            lod = Character.isLetterOrDigit(cp);
            if(!Character.isBmpCodePoint(cp)) ++s;
        }
        return sb.toString();
    }

    public static String bmlString(String string) {
        if(string==null) return "";
        if(string.indexOf('"') >= 0)
            string = string.replace('"','»');
        return string;
    }
}
