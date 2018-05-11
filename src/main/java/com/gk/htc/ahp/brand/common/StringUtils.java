package com.gk.htc.ahp.brand.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.*;
import org.apache.log4j.Logger;

public class StringUtils {

    static final Logger logger = Logger.getLogger(StringUtils.class);
    static int RANDOM_STRING_LENGTH = 4;
    private static final Random RANDOM = new SecureRandom();

    public static String subString(String input, int leng) {
        if (!Tool.checkNull(input) && input.length() > leng) {
            return input.substring(0, leng);
        } else {
            return input;
        }
    }

    public static String decimalFormat(double num) {
        DecimalFormat df2 = new DecimalFormat("#,##0.0#;(#)");
        return df2.format(num);
    }

    public static String generateRandomPassword() {
        // Pick from some letters that won't be easily mistaken for each
        // other. So, for example, omit o O and 0, 1 l and L.
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";

        String pw = "";
        for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            pw += letters.substring(index, index + 1);
        }
        return pw;
    }
    static final char NINE = 57;
    static final char ZERO = 48;
    static final char CH_a = 97;
    static final char CH_z = 122;
    static final char CH_A = 65;
    static final char CH_Z = 90;
    static final String seperators[] = {
        " ", ".", ",", "-", "_", "="
    };
    private static char charArray[];
    private static Random random = null;

    static {
        charArray = null;
        int numOfChars = 26;
        int numOfDigits = 10;
        random = new Random();
        charArray = new char[numOfChars + numOfDigits];
        for (int i = 0; i < numOfChars; i++) {
            charArray[i] = (char) (65 + i);
        }

        for (int i = 0; i < numOfDigits; i++) {
            charArray[numOfChars + i] = (char) (48 + i);
        }

    }

    public StringUtils() {
    }

    public static String readLine(String input) {
        BufferedReader br = null;
        String result = "";
        try {
            String sCurrentLine;
            br = new BufferedReader(new StringReader(input));
            while ((sCurrentLine = br.readLine()) != null) {
                result += sCurrentLine + "\n";
            }
            if (result.endsWith("\n")) {
                result = result.substring(0, (result.length()) - ("\n".length()));
            }
        } catch (IOException e) {
            logger.error(Tool.getLogMessage(e));
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                logger.error(Tool.getLogMessage(ex));
            }
        }
        return result;
    }

    public static String str2OneLine(String input) {
        String cleaned = "";
        if (!Tool.checkNull(input)) {
            cleaned = input.replaceAll("\\s*[\\r\\n]+\\s*", "").trim();
        }
        return cleaned;
    }

    public static int string2Integer(String input) {
        int tem = 0;
        try {
            tem = Integer.parseInt(input.trim());
        } catch (Exception e) {
            tem = 0;
        }
        return tem;
    }

    public static String convert2NoSign(String org) {
        if (org == null) {
            org = "";
            return org;
        }
        char arrChar[] = org.toCharArray();
        char result[] = new char[arrChar.length];
        for (int i = 0; i < arrChar.length; i++) {
            switch (arrChar[i]) {
                case '\u00E1':
                case '\u00E0':
                case '\u1EA3':
                case '\u00E3':
                case '\u1EA1':
                case '\u0103':
                case '\u1EAF':
                case '\u1EB1':
                case '\u1EB3':
                case '\u1EB5':
                case '\u1EB7':
                case '\u00E2':
                case '\u1EA5':
                case '\u1EA7':
                case '\u1EA9':
                case '\u1EAB':
                case '\u1EAD':
                case '\u0203':
                case '\u01CE': {
                    result[i] = 'a';
                    break;
                }
                case '\u00E9':
                case '\u00E8':
                case '\u1EBB':
                case '\u1EBD':
                case '\u1EB9':
                case '\u00EA':
                case '\u1EBF':
                case '\u1EC1':
                case '\u1EC3':
                case '\u1EC5':
                case '\u1EC7':
                case '\u0207': {
                    result[i] = 'e';
                    break;
                }
                case '\u00ED':
                case '\u00EC':
                case '\u1EC9':
                case '\u0129':
                case '\u1ECB': {
                    result[i] = 'i';
                    break;
                }
                case '\u00F3':
                case '\u00F2':
                case '\u1ECF':
                case '\u00F5':
                case '\u1ECD':
                case '\u00F4':
                case '\u1ED1':
                case '\u1ED3':
                case '\u1ED5':
                case '\u1ED7':
                case '\u1ED9':
                case '\u01A1':
                case '\u1EDB':
                case '\u1EDD':
                case '\u1EDF':
                case '\u1EE1':
                case '\u1EE3':
                case '\u020F': {
                    result[i] = 'o';
                    break;
                }
                case '\u00FA':
                case '\u00F9':
                case '\u1EE7':
                case '\u0169':
                case '\u1EE5':
                case '\u01B0':
                case '\u1EE9':
                case '\u1EEB':
                case '\u1EED':
                case '\u1EEF':
                case '\u1EF1': {
                    result[i] = 'u';
                    break;
                }
                case '\u00FD':
                case '\u1EF3':
                case '\u1EF7':
                case '\u1EF9':
                case '\u1EF5': {
                    result[i] = 'y';
                    break;
                }
                case '\u0111': {
                    result[i] = 'd';
                    break;
                }
                case '\u00C1':
                case '\u00C0':
                case '\u1EA2':
                case '\u00C3':
                case '\u1EA0':
                case '\u0102':
                case '\u1EAE':
                case '\u1EB0':
                case '\u1EB2':
                case '\u1EB4':
                case '\u1EB6':
                case '\u00C2':
                case '\u1EA4':
                case '\u1EA6':
                case '\u1EA8':
                case '\u1EAA':
                case '\u1EAC':
                case '\u0202':
                case '\u01CD': {
                    result[i] = 'A';
                    break;
                }
                case '\u00C9':
                case '\u00C8':
                case '\u1EBA':
                case '\u1EBC':
                case '\u1EB8':
                case '\u00CA':
                case '\u1EBE':
                case '\u1EC0':
                case '\u1EC2':
                case '\u1EC4':
                case '\u1EC6':
                case '\u0206': {
                    result[i] = 'E';
                    break;
                }
                case '\u00CD':
                case '\u00CC':
                case '\u1EC8':
                case '\u0128':
                case '\u1ECA': {
                    result[i] = 'I';
                    break;
                }
                case '\u00D3':
                case '\u00D2':
                case '\u1ECE':
                case '\u00D5':
                case '\u1ECC':
                case '\u00D4':
                case '\u1ED0':
                case '\u1ED2':
                case '\u1ED4':
                case '\u1ED6':
                case '\u1ED8':
                case '\u01A0':
                case '\u1EDA':
                case '\u1EDC':
                case '\u1EDE':
                case '\u1EE0':
                case '\u1EE2':
                case '\u020E': {
                    result[i] = 'O';
                    break;
                }
                case '\u00DA':
                case '\u00D9':
                case '\u1EE6':
                case '\u0168':
                case '\u1EE4':
                case '\u01AF':
                case '\u1EE8':
                case '\u1EEA':
                case '\u1EEC':
                case '\u1EEE':
                case '\u1EF0': {
                    result[i] = 'U';
                    break;
                }

                case '\u00DD':
                case '\u1EF2':
                case '\u1EF6':
                case '\u1EF8':
                case '\u1EF4': {
                    result[i] = 'Y';
                    break;
                }
                case '\u0110':
                case '\u00D0':
                case '\u0089': {
                    result[i] = 'D';
                    break;
                }
                case (char) 160: {
                    result[i] = ' ';
                    break;
                }
                default:
                    result[i] = arrChar[i];
            }
        }
        return new String(result);
    }

    /**
     *
     * @param oldS
     * @param pos
     * @param s
     * @return
     */
    public static String appendString(String oldS, int pos, String s) {
        return (new StringBuilder()).append(oldS.substring(0, pos)).append(s).append(oldS.substring(pos)).toString();
    }

    /**
     * Thay thế một ký tự bằng 1 ký tự khác trong 1 String tại vị trí cho trước
     *
     * @param str : String ban đầu
     * @param pos : Vị trí
     * @param c : Ký tự cần thay thế
     * @return
     */
    public static String replaceCharAt(String str, int pos, char c) {
        StringBuilder buf = new StringBuilder(str);
        buf.setCharAt(pos, c);
        return buf.toString();
    }

    /**
     * Thay thế một ký tự bởi một String
     *
     * @param str
     * @param a
     * @param newStr
     * @return
     */
    public static String replaceChar(String str, char a, String newStr) {
        if (str == null) {
            return null;
        }
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char cur = str.charAt(i);
            if (cur == a) {
                newString.append(newStr);
            } else {
                newString.append(cur);
            }
        }

        return newString.toString();
    }

    /**
     * Loại bỏ một ký tự trong 1 xâu
     *
     * @param s
     * @param c
     * @return
     */
    public static String removeChar(String s, char c) {
        if (s == null) {
            return null;
        }
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char cur = s.charAt(i);
            if (cur != c) {
                newString.append(cur);
            }
        }

        return newString.toString();
    }

    /**
     * Loại bỏ một ký tự ở vị trí cho trước
     *
     * @param s
     * @param pos
     * @return
     */
    public static String removeCharAt(String s, int pos) {
        StringBuilder buf = new StringBuilder(s.length() - 1);
        buf.append(s.substring(0, pos)).append(s.substring(pos + 1));
        return buf.toString();
    }

    /**
     * Loai bỏ các ky tự đặc biệt ra khỏi String
     *
     * @param s
     * @return
     */
    public static String removeSpecialCharsInString(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
                buffer.append(ch);
            }
        }

        return buffer.toString();
    }

    /**
     * Loại bỏ nhiều khoảng trắng thành 1 khoảng trắng trong Xâu
     *
     * @param text
     * @return
     */
    public static String removeMoreSpace2OneSpaceBetweenWords(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        boolean lastCharIsSpace = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == ' ') {
                if (lastCharIsSpace) {
                    continue;
                }
                lastCharIsSpace = true;
            } else if (lastCharIsSpace) {
                lastCharIsSpace = false;
            }
            buffer.append(ch);
        }

        return buffer.toString();
    }

    public static String replaceMultiWhiteSpace(String input) {
        return input.replaceAll("  +|   +|\\t", " ");
//        return input.replaceAll("\\s{2,}", " ");  // Thay ca Xuong Dong bang Khoang Trang \s 	A whitespace character: [ \t\n\x0B\f\r]  
//        return input.replaceAll("\\s+", " ");     // Thay ca Xuong Dong bang Khoang Trang
    }

//    public static void main(String[] args) {
//        String str = "Stack Overflow is a a   community   of 6.3 million programmers, just like you, helping each other.\n"
//                + "Join them; it only takes a minute: ";
//        Tool.debug(str);
//        Tool.debug(replaceMultiWhiteSpace(str));
//    }
    /**
     * Chặt 1 xâu thành 1 Collection
     *
     * @param text
     * @param seperator
     * @return
     */
    public static List splitString(String text, String seperator) {
        List vResult = new ArrayList();
        if (text == null || "".equals(text)) {
            return vResult;
        }
        String tempStr = text.trim();
        String currentLabel = null;
        for (int index = tempStr.indexOf(seperator); index != -1; index = tempStr.indexOf(seperator)) {
            currentLabel = tempStr.substring(0, index).trim();
            if (!"".equals(currentLabel)) {
                vResult.add(currentLabel);
            }
            tempStr = tempStr.substring(index + 1);
        }

        currentLabel = tempStr.trim();
        if (!"".equals(currentLabel)) {
            vResult.add(currentLabel);
        }
        return vResult;
    }

    public static List parseString(String text) {
        List vResult = new ArrayList();
        if (text == null || "".equals(text)) {
            return vResult;
        }
        String tempStr = text.trim();
        String currentLabel = null;
        for (int index = getNextIndex(tempStr); index != -1; index = getNextIndex(tempStr)) {
            currentLabel = tempStr.substring(0, index).trim();
            if (!"".equals(currentLabel)) {
                vResult.add(currentLabel);
            }
            tempStr = tempStr.substring(index + 1);
        }

        currentLabel = tempStr.trim();
        if (!"".equals(currentLabel)) {
            vResult.add(currentLabel);
        }
        return vResult;
    }

    private static int getNextIndex(String text) {
        int index = 0;
        int newIdx = 0;
        boolean hasOne = false;
        for (int i = 0; i < seperators.length; i++) {
            newIdx = text.indexOf(seperators[i]);
            if (!hasOne) {
                if (newIdx != -1) {
                    index = newIdx;
                    hasOne = true;
                }
                continue;
            }
            if (newIdx != -1 && newIdx < index) {
                index = newIdx;
            }
        }

        if (!hasOne) {
            index = -1;
        }
        return index;
    }

    /**
     * Kiểm tra xem 1 String có phải là một số
     *
     * @param sNumber
     * @return
     */
    public static boolean isNumberic(String sNumber) {
        if (sNumber == null || "".equals(sNumber)) {
            return false;
        }
        char ch_max = '9';
        char ch_min = '0';
        for (int i = 0; i < sNumber.length(); i++) {
            char ch = sNumber.charAt(i);
            if (ch < ch_min || ch > ch_max) {
                return false;
            }
        }

        return true;
    }

    /**
     * Tạo ra một chuỗi ramdom
     *
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        char ch[] = new char[length];
        for (int i = 0; i < length; i++) {
            ch[i] = charArray[random.nextInt(charArray.length)];
        }

        return new String(ch);
    }

    public static String replaceString(String sStr, String oldStr, String newStr) {
        sStr = sStr != null ? sStr : "";
        String strVar = sStr;
        String tmpStr = "";
        String finalStr = "";
        int stpos = 0;
        int endpos = 0;
        int strLen = 0;
        do {
            strLen = strVar.length();
            stpos = 0;
            endpos = strVar.indexOf(oldStr, stpos);
            if (endpos != -1) {
                tmpStr = strVar.substring(stpos, endpos);
                tmpStr = tmpStr.concat(newStr);
                strVar = strVar.substring(endpos + oldStr.length() <= sStr.length() ? endpos + oldStr.length() : endpos, strLen);
                finalStr = finalStr.concat(tmpStr);
                stpos = endpos;
            } else {
                finalStr = finalStr.concat(strVar);
                return finalStr;
            }
        } while (true);
    }

    public static String[] arrangeString(String[] arrString) {
        try {
            for (int i = 0; i < arrString.length; i++) {
                if (arrString[i] == null || arrString[i].equals("")) {
                    continue;
                } else {
                    arrString[i] = arrString[i].trim();
                }
                for (int j = i + 1; j < arrString.length; j++) {
                    if (arrString[j] == null || arrString[j].equals("")) {
                        continue;
                    } else {
                        arrString[j] = arrString[j].trim();
                    }
                    String stem = "";
                    if (arrString[j].length() > arrString[i].length()) {
                        stem = arrString[i].toUpperCase();
                        arrString[i] = arrString[j].toUpperCase();
                        arrString[j] = stem;
                    }
                }
            }
        } catch (Exception e) {
            return arrString;
        }
        return arrString;
    }

    //---------------
    public static int string2Integer(String input, int defaultVal) {
        int tem = 0;
        try {
            tem = Integer.parseInt(input.trim());
        } catch (Exception e) {
            tem = defaultVal;
        }
        return tem;
    }

    /**
     * *
     *
     * @param input
     * @return
     */
    public static double string2Double(String input) {
        double tem = 0.0;
        try {
            tem = Double.parseDouble(input.trim().replaceAll(",", "."));
        } catch (Exception e) {
            tem = 0.0;
        }
        return tem;
    }

    public static float string2Float(String input) {
        float tem = 0;
        try {
            tem = Float.parseFloat(input.trim().replaceAll(",", "."));
        } catch (Exception e) {
            tem = 0;
        }
        return tem;
    }

    /**
     * string2Integer
     *
     * @param input
     * @return Default return 0 neu String = 0 or notvalid
     */
    public static long string2Long(String input) {
        long tem = 0;
        try {
            tem = Long.parseLong(input);
        } catch (Exception e) {
            tem = 0;
        }
        return tem;
    }

    public static boolean compareServiceNumLarge(String svNum, int svMin) {
        boolean flag = false;
        try {
            int tem = string2Integer(svNum, 6088);
            if (tem > svMin) {
                flag = true;
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }

        return flag;
    }

}
