package com.gk.htc.ahp.brand.common;

import com.gk.htc.ahp.brand.entity.OptionCheckDuplicate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class SMSUtils {

    static final Logger logger = Logger.getLogger(SMSUtils.class);
    private static final String HEXINDEX = "0123456789abcdef          ABCDEF";
    public static final String PARTNER_NAME = "AHP";
    public static String CHAR_PARSE_MT = "#@#";
    public static final String GSM_CHARACTERS_REGEX = "^[A-Za-z0-9 \\r\\n@£$¥èéùìòÇØøÅå\u0394_\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039EÆæßÉ!\"#$%&'()*+,\\-./:;<=>?¡ÄÖÑÜ§¿äöñüà^{}\\\\\\[~\\]|\u20AC]*$";
    public static int REJECT_MSG_LENG = -1;

    /*-
 * ^[A-Za-z0-9 \r\n@£$¥èéùìòÇØøÅå\u0394_\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039EÆæßÉ!"#$%&amp;'()*+,\-./:;&lt;=&gt;?¡ÄÖÑÜ§¿äöñüà^{}\\\[~\]|\u20AC]*$
 *
 * Assert position at the beginning of the string «^»
 * Match a single character present in the list below «[A-Za-z0-9 \r\n@£$¥èéùìòÇØøÅå\u0394_\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039EÆæßÉ!"#$%&amp;'()*+,\-./:;&lt;=&gt;?¡ÄÖÑÜ§¿äöñüà^{}\\\[~\]|\u20AC]*»
 *    Between zero and unlimited times, as many times as possible, giving back as needed (greedy) «*»
 *    A character in the range between "A" and "Z" «A-Z»
 *    A character in the range between "a" and "z" «a-z»
 *    A character in the range between "0" and "9" «0-9»
 *    The character " " « »
 *    A carriage return character «\r»
 *    A line feed character «\n»
 *    One of the characters "@£$¥èéùìòÇØøÅå" «@£$¥èéùìòÇØøÅå»
 *    Unicode character U+0394 «\u0394», Greek capital Delta
 *    The character "_" «_»
 *    Unicode character U+03A6 «\u03A6», Greek capital Phi
 *    Unicode character U+0393 «\u0393», Greek capital Gamma
 *    Unicode character U+039B «\u039B», Greek capital Lambda
 *    Unicode character U+03A9 «\u03A9», Greek capital Omega
 *    Unicode character U+03A0 «\u03A0», Greek capital Pi
 *    Unicode character U+03A8 «\u03A8», Greek capital Psi
 *    Unicode character U+03A3 «\u03A3», Greek capital Sigma
 *    Unicode character U+0398 «\u0398», Greek capital Theta
 *    Unicode character U+039E «\u039E», Greek capital Xi
 *    One of the characters "ÆæßÉ!"#$%&amp;'()*+," «ÆæßÉ!"#$%&amp;'()*+,»
 *    A - character «\-»
 *    One of the characters "./:;&lt;=&gt;?¡ÄÖÑÜ§¿äöñüà^{}" «./:;&lt;=&gt;?¡ÄÖÑÜ§¿äöñüà^{}»
 *    A \ character «\\»
 *    A [ character «\[»
 *    The character "~" «~»
 *    A ] character «\]»
 *    The character "|" «|»
 *    Unicode character U+20AC «\u20AC», Euro sign
 * Assert position at the end of the string (or before the line break at the end of the string, if any) «$»
     */
    /**
     * gsm =
     * "@£$¥èéùìòÇØøÅåΔ_ΦΓΛΩΠΨΣΘΞ^{}\[~]|€ÆæßÉ!\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà";
     * var letter = 'a'; var letterInAlfabet = gsm.indexOf(letter) !== -1;
     *
     *
     *
     */
//     function isGSMAlphabet(text) {
//    var regexp = new RegExp("^[A-Za-z0-9 \\r\\n@£$¥èéùìòÇØøÅå\u0394_\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039EÆæßÉ!\"#$%&'()*+,\\-./:;<=>?¡ÄÖÑÜ§¿äöñüà^{}\\\\\\[~\\]|\u20AC]*$");
//    return regexp.test(text);
//        }
//    String GSM = "@£$¥èéùìòÇØøÅåΔ_ΦΓΛΩΠΨΣΘΞ^{}\[~]|€ÆæßÉ!\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà"; // Javascript
    public static void main(String[] args) {
//        String str = "YAMAHA TAN THAI BINH 4 kinh chao Quy Khach! Kinh moi Quy Khach mang xe den bao duong toan phan de duoc tang CA nhua hoac nhan 1 NON bao hiem cao cap  va nhieu qua tang hap dan khac. Chi tiet lien he: 0616523048. Tran trong!";
//        System.out.println(countLengthGSM(str));
////        System.out.println(str.length());
//        if (!isUnicode(str)) {
//            System.out.println("la GSM_CHARACTERS");
//            System.out.println("byte[] length:" + str.getBytes().length);
//        } else {
//            System.out.println("khong phai la GSM_CHARACTERS");
//            System.out.println("byte[] length:" + str.getBytes().length);
//        }
        String s = "Nhan dip Nam Moi 2018 \"Tet Mau Tuat\",thay mat Ban Chap hanh Hiep hoi Doanh nghiep nho va vua thanh pho HN,toi than ai gui den Quy Doanh nghiep,doanh nhan,tap the Ban Lanh dao,toan the can bo cong nhan vien cung gia dinh loi chuc mung nam moi \"Suc Khoe-Hanh Phuc-May Man-An Khang-Thinh Vuong\".Chuc cac Doanh nghiep,Doanh nhan ngay cang doan ket,hop tac,cung phat trien,don mot nam moi voi nhieu co hoi moi va thang loi moi.\nThay mat BCH\nChu Tich\nDo Quang Hien";
        System.out.println(countLengthGSM(s));
    }

    private static String removeNonUtf8CompliantCharacters(final String inString) {
        if (null == inString) {
            return null;
        }
        byte[] byteArr = inString.getBytes();
        for (int i = 0; i < byteArr.length; i++) {
            byte ch = byteArr[i];
            // remove any characters outside the valid UTF-8 range as well as all control characters
            // except tabs and new lines
            if (!((ch > 31 && ch < 253) || ch == '\t' || ch == '\n' || ch == '\r')) {
                byteArr[i] = ' ';
            }
        }
        return new String(byteArr);
    }

    public static boolean checkDuplicateTelco(String operByPhone, OptionCheckDuplicate opt_checkDuplicate) {
        boolean check = true; // yeu cau check trung
        switch (operByPhone) {
//        VIETTEL("VTE"),
//        VINA("GPC"),
//        MOBI("VMS"),
//        VNM("VNM"),
//        BEELINE("BL");
            case "VTE":
                int checkDuplicateVTE = Integer.parseInt(opt_checkDuplicate.getVte());
                if (checkDuplicateVTE == 1) {
                    //bỏ check trung vs VTE
                    check = false;
                }
                break;
            case "GPC":
                int checkDuplicateVINA = Integer.parseInt(opt_checkDuplicate.getVina());
                if (checkDuplicateVINA == 1) {
                    //bỏ check trung vs VINA
                    check = false;
                }
                break;
            case "VMS":
                int checkDuplicateMOBI = Integer.parseInt(opt_checkDuplicate.getMobi());
                if (checkDuplicateMOBI == 1) {
                    //bỏ check trung vs MOBI
                    check = false;
                }
                break;
            case "VNM":
                int checkDuplicateVNM = Integer.parseInt(opt_checkDuplicate.getVnm());
                if (checkDuplicateVNM == 1) {
                    check = false;
                    //bỏ check trung vs VNM
                }
                break;
            case "BL":
                int checkDuplicateBL = Integer.parseInt(opt_checkDuplicate.getBl());
                if (checkDuplicateBL == 1) {
                    check = false;
                    //bỏ check trung vs BL
                }
                break;
            default:
                // Mac dinh yeu cau check trung
                check = true;
                break;
        }
        return check;
    }

    public static boolean isUnicode(String input) {
        boolean result = Boolean.FALSE;
        if (input == null) {
            return result;
        }
        boolean is7Bit = isGSM_7Bit(input);
        if (!is7Bit) {
            // Kiem tra xem co phai la Ky Tu GSM 7 bit khong? 
            // Neu khong phai tra ve ket qua luon
//            System.out.println("Khong phai GMS 7 bit");
            return true;
        }
        char[] arrChar = input.toCharArray();
        for (char c : arrChar) {
            if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
                System.out.println("Khong nam trong BASIC_LATIN");
                result = true;
                break;
            }
        }
        return result;
    }

    public static boolean isGSM_7Bit(String input) {
        boolean result;
        if (Tool.checkNull(input)) {
            return Boolean.FALSE;
        }
        Pattern pattern = Pattern.compile(GSM_CHARACTERS_REGEX);
        Matcher matcher = pattern.matcher(input);
        result = matcher.find();
        return result;
    }

    public static boolean isASCII(String input) {
        boolean isASCII = true;
        if (input == null) {
            return isASCII;
        }
        for (int i = 0; i < input.length(); i++) {
            int c = input.charAt(i);
            if (c > 0x7F) {
                isASCII = false;
                break;
            }
        }
        return isASCII;
    }
    private static final ArrayList<String> OTP_LIST = new ArrayList<String>();

    static {
        OTP_LIST.add("OTP");
        OTP_LIST.add("PIN");
        OTP_LIST.add("ma giao dich");
        OTP_LIST.add("ma xac thuc");
        OTP_LIST.add("ma xac nhan");
        OTP_LIST.add("chuoi chung thuc");
        OTP_LIST.add("chuoi xac nhan");
        OTP_LIST.add("chuoi xac thuc");
        OTP_LIST.add("chuoi kich hoat");
        OTP_LIST.add("mat khau");
        OTP_LIST.add("ma kich hoat");
        OTP_LIST.add("monitor");
        OTP_LIST.add("test");
        OTP_LIST.add("check");
        OTP_LIST.add("GD rut tien");
        OTP_LIST.add("Han muc con lai");
        OTP_LIST.add("vua chi tieu");
        OTP_LIST.add("VPBFC");
    }
    private static final ArrayList<String> KEYWORD_LIST = new ArrayList<String>();

    static {
        KEYWORD_LIST.add("dangcongsanvn");
        KEYWORD_LIST.add("Dangcongsan");
        KEYWORD_LIST.add("dang VN");
        KEYWORD_LIST.add("fuck you");
        KEYWORD_LIST.add("damn bitch");
        KEYWORD_LIST.add("địt mẹ");
        KEYWORD_LIST.add("đảng cộng sản");
        KEYWORD_LIST.add("đảng cộng sản việt nam");
        KEYWORD_LIST.add("đả đảo");
        KEYWORD_LIST.add("(QC)");
        KEYWORD_LIST.add("bieu tinh");
        KEYWORD_LIST.add("Fuck");
        KEYWORD_LIST.add("dangvn");
        KEYWORD_LIST.add("Sale off");
        KEYWORD_LIST.add("discount");
        KEYWORD_LIST.add("<QC>");
        KEYWORD_LIST.add("dang cong san");
        KEYWORD_LIST.add("discount off");
        KEYWORD_LIST.add("(K.Mại)");
    }

    public static boolean isOTP(String msg) {
        boolean result = false;
        try {
            if (!Tool.checkNull(msg)) {
                msg = msg.toLowerCase();
                for (String one : OTP_LIST) {
                    if (msg.contains(one.toLowerCase())) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    public static String PhoneTo84(String number) {
        if (number == null) {
            number = "";
            return number;
        }
        number = number.replaceAll("o", "0");
        number = number.replaceAll("\\+", "");
        if (number.startsWith("84")) {
            return number;
        } else if (number.startsWith("0")) {
            number = "84" + number.substring(1);
        } else if (number.startsWith("+84")) {
            number = number.substring(1);
        } else {
            number = "84" + number;
        }
        return number;
    }

    public static boolean isASCII(char ch) {
        return ch < 128;
    }

    public static void SendAlert8x65(String message, String phone) {

        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("userName", "htcfe");
            params.put("passWord", "ohmygod39458");
            params.put("phone", phone);
            params.put("mess", message);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            Tool.debug("PostData:" + postData);
            URL url = new URL("http://210.211.98.80:8765/service/notifybyPhone/MT" + "?" + postData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0;) {
                sb.append((char) c);
            }
            String resultPartner = sb.toString();
            Tool.debug("[===> ResultPartner:" + resultPartner);
            conn.disconnect();
        } catch (IOException e) {
            logger.error(Tool.getLogMessage(e));
        }
    }

    static int countLengthGSM(String input) {
//        long s = System.currentTimeMillis();
        int length = 0;
        for (int pos = 0; pos < input.length(); pos++) {
            char ch = input.charAt(pos);
            switch (ch) {
                case '|':
                case '^':
                case '€':
                case '{':
                case '}':
//                case '\n':
                case '[':
                case ']':
                case '~':
//                case '\\':
                    length += 2;
                    break;
                default:
                    length += 1;
            }
        }
//        long e = System.currentTimeMillis() - s;
//        System.out.println("Proces countLengthGSM:" + e);
        return length;
    }

    public static int countSmsBrandQC(String mess, String oper) {
        int count;
        if (!Tool.checkNull(mess)) {
            int length = countLengthGSM(mess);
            if (oper.equals(OPER.VIETTEL.val)) {
                if (length <= 123) {
                    count = 1;
                } else if (length > 123 && length <= 268) {
                    count = 2;
                } else if (length > 268 && length <= 421) {
                    count = 3;
                } else if (length > 421 && length <= 574) {
                    count = 4;
                } else {
                    count = REJECT_MSG_LENG;
                }
            } else if (oper.equals(OPER.VINA.val)) {
                if (length <= 122) {
                    count = 1;
                } else if (length > 122 && length <= 268) {
                    count = 2;
                } else if (length > 268 && length <= 421) {
                    count = 3;
                } else if (length > 421 && length <= 574) {
                    count = 4;
                } else {
                    count = REJECT_MSG_LENG;
                }
            } else if (oper.equals(OPER.MOBI.val)) {
                if (length <= 127) {
                    count = 1;
                } else if (length > 127 && length <= 273) {
                    count = 2;
                } else if (length > 273 && length <= 426) {
                    count = 3;
                } else if (length > 426 && length <= 579) {
                    count = 4;
                } else {
                    count = REJECT_MSG_LENG;
                }
            } else if (length <= 160) {
                count = 1;
            } else if (length > 160 && length <= 306) {
                count = 2;
            } else if (length > 306 && length <= 459) {
                count = 3;
            } else if (length > 459 && length <= 612) {
                count = 4;
            } else {
                count = REJECT_MSG_LENG;
            }
        } else {
            count = 0;
        }
        return count;
    }

//    private static String replaceExtention(String input) {
//        return input.replaceAll("[|^€{}\\[\\]~\\\\]", "xx");
//    }
//
//    private static int countLengthSMS(String input) {
//        int length = 0;
//        long s = System.currentTimeMillis();
//        input = input.replaceAll("[|^€{}\\[\\]~\\\\]", "xx");
//        length = input.length();
//        long e = System.currentTimeMillis() - s;
//        System.out.println("Proces replaceExtention:" + e);
//        return length;
//    }
    public static int countFast(String mess) {
        int tmpLeng = mess.length();
        int result = tmpLeng / 157;
        if (tmpLeng % 157 != 0) {
            result += 1;
        }
        return result;
    }

    public static int countFastUnicode(String mess) {
        int tmpLeng = mess.length();
        int result = tmpLeng / 67;
        if (tmpLeng % 67 != 0) {
            result += 1;
        }
        return result;
    }

    public static int countSmsBrandCSKH(String mess, String oper) {
        int count;
        if (!Tool.checkNull(mess)) {
            int length = countLengthGSM(mess);
            if (oper.equals(OPER.VIETTEL.val)) {
                if (length <= 160) {
                    count = 1;
                } else if (length > 160 && length <= 306) {
                    count = 2;
                } else if (length > 306 && length <= 459) {
                    count = 3;
//                } else if (length > 459 && length <= 612) {
//                    count = 4;
                } // VIETTEL BO TIN NHAN THU 4 DI
                else {
                    count = REJECT_MSG_LENG;
                }
            } else if (oper.equals(OPER.VINA.val)) {
                if (length <= 160) {
                    count = 1;
                } else if (length > 160 && length <= 306) {
                    count = 2;
                } else if (length > 306 && length <= 459) {
                    count = 3;
                } else if (length > 459 && length <= 612) {
                    count = 4;
                } else {
                    count = REJECT_MSG_LENG;
                }
            } else if (oper.equals(OPER.MOBI.val)) {
                if (length <= 160) {
                    count = 1;
                } else if (length > 160 && length <= 306) {
                    count = 2;
                } else if (length > 306 && length <= 459) {
                    count = 3;
                } else if (length > 459 && length <= 612) {
                    count = 4;
                } else {
                    count = REJECT_MSG_LENG;
                }
            } else if (length <= 160) {
                count = 1;
            } else if (length > 160 && length <= 306) {
                count = 2;
            } else if (length > 306 && length <= 459) {
                count = 3;
            } else if (length > 459 && length <= 612) {
                count = 4;
            } else {
                count = REJECT_MSG_LENG;
            }
        } else {
            count = 0;
        }
        return count;
    }

    public static int countSmsBrandCSKHUnicode(String mess) {
        int count;
        if (!Tool.checkNull(mess)) {

            int length = countLengthGSM(mess);
//            System.out.println("======:::::==============:::" + length);
            if (length <= 70) {
                count = 1;
            } else if (length > 70 && length <= 134) {
                count = 2;
            } else if (length > 134 && length <= 201) {
                count = 3;
            } else if (length > 201 && length <= 268) {
                count = 4; //southtelecom
//            } else if (length > 268 && length <= 335) {
//                count = 5; // VMG
            } else {
                count = 0;
            }

        } else {
            count = 0;
        }
        return count;
    }

    public static ArrayList string2List(String listPhone) {
        ArrayList list = new ArrayList();
        if (listPhone != null) {
            String[] arrPhone = listPhone.split("[,;: ]");
            if (arrPhone != null && arrPhone.length > 0) {
                for (String onePhone : arrPhone) {
                    // Valid 1 Phone
                    if (onePhone == null) {
                        continue;
                    }
                    if (checkPhoneNumber(onePhone) == 1) {
                        // So dien thoai hop le
                        list.add(PhoneTo84(onePhone));
                    } else {
                        Tool.debug("So dien thoai ko hop le:" + onePhone);
                        logger.error("So dien thoai ko hop le:" + onePhone);
                    }
                }
            }
        }
        return list;
    }

    public static ArrayList validList(ArrayList<String> listPhone) {
        ArrayList list = new ArrayList();
        // VIETTEL AND OTHER
        if (listPhone != null) {
            for (String onePhone : listPhone) {
                // Valid 1 Phone
                if (onePhone == null) {
                    continue;
                }
                if (SMSUtils.checkPhoneNumber(onePhone) == 1) {
                    list.add(onePhone);
                }
            }
        }
        return list;
    }

    public static boolean validTemplate(String input, String patten) {
        boolean result = false;
        try {
            Pattern p = Pattern.compile(patten);
            Matcher m = p.matcher(input);
            result = m.matches();
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    public static boolean validTemplate(String input) {
        boolean result = false;
        try {
            Pattern p = Pattern.compile(".{1,}", Pattern.DOTALL);
            Matcher m = p.matcher(input);
            result = m.matches();
            Tool.debug("result:" + result);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    public static boolean validTemplate(String input, String[] patten) {
        boolean result = false;
        try {
            if (patten != null && patten.length > 0) {
                for (String onePatten : patten) {
//                    Tool.debug("onePatten:" + onePatten);
                    Pattern p = Pattern.compile(onePatten, Pattern.DOTALL);
                    Matcher m = p.matcher(input);
                    result = m.matches();
//                    Tool.debug("result:" + result);
                    if (result) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return result;
    }

    public static boolean validPhoneVN(String phone) {
        String regex = "^(\\+849\\d{8})|"
                + "(849\\d{8})|"
                + "(09\\d{8})|"
                + "(\\+841\\d{9})|"
                + "(841\\d{9})|"
                + "(01\\d{9})|"
                + "(\\+848\\d{8})|"
                + "(848\\d{8})|"
                + "(08\\d{8})$";
        // Create a Pattern object
        Pattern pattern = Pattern.compile(regex);
        // Now create matcher object.
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static enum OPER {

        VIETTEL("VTE"),
        VINA("GPC"),
        MOBI("VMS"),
        VNM("VNM"),
        BEELINE("BL");
        public String val;

        public String getVal() {
            return val;
        }

        private OPER(String val) {
            this.val = val;
        }
    }

    /**
     * PLA TUAN Xap Xep COmmandCode Theo Do Dai giam dan
     *
     * @param allCommandCode
     * @return
     */
    public static String[] arrangeCommandCode(String[] allCommandCode) {
        try {
            for (int i = 0; i < allCommandCode.length; i++) {
                for (int j = i + 1; j < allCommandCode.length; j++) {
                    String stem;
                    if (allCommandCode[j].length() > allCommandCode[i].length()) {
                        stem = allCommandCode[i].toUpperCase();
                        allCommandCode[i] = allCommandCode[j].toUpperCase();
                        allCommandCode[j] = stem;
                    }
                }
            }
        } catch (Exception e) {
            return allCommandCode;
        }
        return allCommandCode;
    }

    /**
     * PLA TUAN Loai Bo Cac Ky Tu Dac biet trong Msg
     *
     * @param msg
     * @return
     */
    public static String sumNick(String nick) {
        if (nick == null || "".equals(nick)) {
            return null;
        }
        nick = nick.trim();
        int sum = 0;
        if (nick.length() < 2 && isNumberic(nick)) {
            return nick;
        }
        nick = nick.toUpperCase();
        for (int i = 0; i < nick.length(); i++) {
            char ch = nick.charAt(i);
            if (ch == 'A' || ch == 'J' || ch == 'S') {
                sum++;
                continue;
            }
            if (ch == 'B' || ch == 'K' || ch == 'T') {
                sum += 2;
                continue;
            }
            if (ch == 'C' || ch == 'L' || ch == 'U') {
                sum += 3;
                continue;
            }
            if (ch == 'D' || ch == 'M' || ch == 'V') {
                sum += 4;
                continue;
            }
            if (ch == 'E' || ch == 'N' || ch == 'W') {
                sum += 5;
                continue;
            }
            if (ch == 'F' || ch == 'O' || ch == 'X') {
                sum += 6;
                continue;
            }
            if (ch == 'G' || ch == 'P' || ch == 'Y') {
                sum += 7;
                continue;
            }
            if (ch == 'H' || ch == 'Q' || ch == 'Z') {
                sum += 8;
                continue;
            }
            if (ch == 'I' || ch == 'R') {
                sum += 9;
            }
        }

        String sTmp = (new StringBuilder()).append("").append(sum).toString();
        sum = 0;
        int iTmp;
        for (; sTmp.length() != 1; sTmp = String.valueOf(iTmp)) {
            iTmp = 0;
            for (int i = 0; i < sTmp.length(); i++) {
                char temp = sTmp.charAt(i);
                if (Character.isDigit(temp)) {
                    iTmp += Integer.parseInt(String.valueOf(temp));
                }
            }

        }
        return sTmp;
    }

    public static String buildMobileOperator(String userId) {
        String mobileOperator = "OTHER";
        if (//-
                userId.startsWith("+8491") || userId.startsWith("8491") || userId.startsWith("091") || userId.startsWith("91")
                || userId.startsWith("+8494") || userId.startsWith("8494") || userId.startsWith("094") || userId.startsWith("94")
                || userId.startsWith("+84123") || userId.startsWith("84123") || userId.startsWith("0123") || userId.startsWith("123")
                || userId.startsWith("+84124") || userId.startsWith("84124") || userId.startsWith("0124") || userId.startsWith("124")
                || userId.startsWith("+84125") || userId.startsWith("84125") || userId.startsWith("0125") || userId.startsWith("125")
                || userId.startsWith("+84127") || userId.startsWith("84127") || userId.startsWith("0127") || userId.startsWith("127")
                || userId.startsWith("+84129") || userId.startsWith("84129") || userId.startsWith("0129") || userId.startsWith("129")
                || userId.startsWith("+8488") || userId.startsWith("8488") || userId.startsWith("088") || userId.startsWith("88") // NEW
                ) {
            //VINA
            mobileOperator = OPER.VINA.val;
        } else if (userId.startsWith("+8490") || userId.startsWith("8490") || userId.startsWith("090") || userId.startsWith("90")
                || userId.startsWith("+8493") || userId.startsWith("8493") || userId.startsWith("093") || userId.startsWith("93")
                || userId.startsWith("+84120") || userId.startsWith("84120") || userId.startsWith("0120") || userId.startsWith("120")
                || userId.startsWith("+84121") || userId.startsWith("84121") || userId.startsWith("0121") || userId.startsWith("121")
                || userId.startsWith("+84122") || userId.startsWith("84122") || userId.startsWith("0122") || userId.startsWith("122")
                || userId.startsWith("+84126") || userId.startsWith("84126") || userId.startsWith("0126") || userId.startsWith("126")
                || userId.startsWith("+84128") || userId.startsWith("84128") || userId.startsWith("0128") || userId.startsWith("128")
                || userId.startsWith("+8489") || userId.startsWith("8489") || userId.startsWith("089") || userId.startsWith("89") // NEW
                ) {
            // MOBILE
            mobileOperator = OPER.MOBI.val;
        } else if (userId.startsWith("+8498") || userId.startsWith("8498") || userId.startsWith("098") || userId.startsWith("98")
                || userId.startsWith("+8497") || userId.startsWith("8497") || userId.startsWith("097") || userId.startsWith("97")
                || userId.startsWith("+8496") || userId.startsWith("8496") || userId.startsWith("096") || userId.startsWith("96") // EVN Cu
                || userId.startsWith("+8416") || userId.startsWith("8416") || userId.startsWith("016") || userId.startsWith("16")
                || userId.startsWith("+8486") || userId.startsWith("8486") || userId.startsWith("086") || userId.startsWith("86") // NEW
                || userId.startsWith("42") || userId.startsWith("042") || userId.startsWith("8442") || userId.startsWith("+8442")) {
            mobileOperator = OPER.VIETTEL.val;
        } else if (userId.startsWith("92") || userId.startsWith("092") || userId.startsWith("8492") || userId.startsWith("+8492")
                || userId.startsWith("188") || userId.startsWith("0188") || userId.startsWith("84188") || userId.startsWith("+84188")
                || userId.startsWith("187") || userId.startsWith("0187") || userId.startsWith("84187") || userId.startsWith("+84187")
                || userId.startsWith("186") || userId.startsWith("0186") || userId.startsWith("84186") || userId.startsWith("+84186")
                || userId.startsWith("184") || userId.startsWith("0184") || userId.startsWith("84184") || userId.startsWith("+84184")) {
            // VIET NAM MOBILE
            mobileOperator = OPER.VNM.val;
        } else if (userId.startsWith("99") || userId.startsWith("099") || userId.startsWith("8499") || userId.startsWith("+8499")
                || userId.startsWith("199") || userId.startsWith("0199") || userId.startsWith("84199") || userId.startsWith("+84199")) {
            mobileOperator = OPER.BEELINE.val;
        } else {
            mobileOperator = "OTHER";
        }
        return mobileOperator;
    }

    /**
     * PLA TUAN KIEM TRA SO DIEN THOAI HOP LE HAY KO
     *
     * @param userId
     * @return
     */
    public static int checkPhoneNumber(String userId) {
        userId = userId.replace('o', '0');
        int check = -1;
        try {
            long number = Long.parseLong(userId);
        } catch (NumberFormatException ne) {
            check = -2;      //"Số điện thoại bạn nhập không phải số";
            return check;
        }
        if (userId == null || "".equals(userId)) {
            return 0;   //"Bạn chưa nhập số điện thoại";
        } else if (!((userId.startsWith("88") || userId.startsWith("86") || userId.startsWith("89") || userId.startsWith("90") || userId.startsWith("91") || userId.startsWith("92") || userId.startsWith("93")
                || userId.startsWith("94") || userId.startsWith("95") || userId.startsWith("96") || userId.startsWith("97") || userId.startsWith("98"))
                && userId.length() == 9)
                && !((userId.startsWith("088") || userId.startsWith("086") || userId.startsWith("089") || userId.startsWith("090") || userId.startsWith("091") || userId.startsWith("092") || userId.startsWith("093")
                || userId.startsWith("094") || userId.startsWith("095") || userId.startsWith("096") || userId.startsWith("097") || userId.startsWith("098"))
                && userId.length() == 10)
                && !((userId.startsWith("8488") || userId.startsWith("8486") || userId.startsWith("8489") || userId.startsWith("8490") || userId.startsWith("8491") || userId.startsWith("8492") || userId.startsWith("8493")
                || userId.startsWith("8494") || userId.startsWith("8495") || userId.startsWith("8496") || userId.startsWith("8497") || userId.startsWith("8498"))
                && userId.length() == 11)
                && !((userId.startsWith("+8488") || userId.startsWith("+8486") || userId.startsWith("+8489") || userId.startsWith("+8490") || userId.startsWith("+8491") || userId.startsWith("+8492") || userId.startsWith("+8493")
                || userId.startsWith("+8494") || userId.startsWith("+8495") || userId.startsWith("+8496") || userId.startsWith("+8497") || userId.startsWith("+8498"))
                && userId.length() == 12)
                && !((userId.startsWith("0160") || userId.startsWith("0161") || userId.startsWith("0162") || userId.startsWith("0163") || userId.startsWith("0164")
                || userId.startsWith("0188") || userId.startsWith("0187") || userId.startsWith("0186") || userId.startsWith("0165") || userId.startsWith("0166") || userId.startsWith("0167") || userId.startsWith("0168") || userId.startsWith("0169")
                || userId.startsWith("0120") || userId.startsWith("0121") || userId.startsWith("0122") || userId.startsWith("0123")
                || userId.startsWith("0124") || userId.startsWith("0125") || userId.startsWith("0126")
                || userId.startsWith("0127") || userId.startsWith("0128") || userId.startsWith("0129")) && userId.length() == 11)
                && !((userId.startsWith("84160") || userId.startsWith("84161") || userId.startsWith("84162") || userId.startsWith("84163") || userId.startsWith("84164")
                || userId.startsWith("84188") || userId.startsWith("84187") || userId.startsWith("84186") || userId.startsWith("84165") || userId.startsWith("84166") || userId.startsWith("84167") || userId.startsWith("84168") || userId.startsWith("84169")
                || userId.startsWith("84120") || userId.startsWith("84121") || userId.startsWith("84122") || userId.startsWith("84123")
                || userId.startsWith("84124") || userId.startsWith("84125") || userId.startsWith("84126")
                || userId.startsWith("84127") || userId.startsWith("84128") || userId.startsWith("84129")) && userId.length() == 12)
                && !((userId.startsWith("+84160") || userId.startsWith("+84161") || userId.startsWith("+84162") || userId.startsWith("+84163") || userId.startsWith("+84164")
                || userId.startsWith("+84188") || userId.startsWith("+84187") || userId.startsWith("+84186") || userId.startsWith("+84165") || userId.startsWith("+84166") || userId.startsWith("+84167") || userId.startsWith("+84168") || userId.startsWith("+84169")
                || userId.startsWith("+84120") || userId.startsWith("+84121") || userId.startsWith("+84122") || userId.startsWith("+84123")
                || userId.startsWith("+84124") || userId.startsWith("+84125") || userId.startsWith("+84126")
                || userId.startsWith("+84127") || userId.startsWith("+84128") || userId.startsWith("+84129")) && userId.length() == 13)) {
            check = -3;
            //"Đầu số không hợp lệ";
            return check;
        } else {
            check = 1;
        }
        return check;
    }

    public static String getTimeString() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        String DATE_FORMAT = "HH:mm:ss";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());
    }

    public static byte[] hexToByte(String s) {
        int l = s.length() / 2;
        byte data[] = new byte[l];
        int j = 0;
        for (int i = 0; i < l; i++) {
            char c = s.charAt(j++);
            int n, b;
            n = HEXINDEX.indexOf(c);
            b = (n & 0xf) << 4;
            c = s.charAt(j++);
            n = HEXINDEX.indexOf(c);
            b += (n & 0xf);
            data[i] = (byte) b;
        }
        return data;
    }

    public static String stringToHexString(String str) {
        byte[] bytes;
        String temp = "";
        try {
            bytes = str.getBytes("US-ASCII");
        } catch (Exception ex) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            temp = temp + Integer.toHexString(bytes[i]);
        }
        return temp;
    }

    public static String stringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuilder strBuffer = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            strBuffer.append(Integer.toHexString((int) chars[i]));
        }
        return strBuffer.toString();
    }

    /**
     * Kiểm tra xem 1 String có phải là số
     *
     * @param sNumber
     * @return
     */
    public static boolean isNumberic(String sNumber) {
        if (sNumber == null || "".equals(sNumber)) {
            return false;
        }
        for (int i = 0; i < sNumber.length(); i++) {
            char ch = sNumber.charAt(i);
            char ch_max = '9';
            char ch_min = '0';
            if (ch < ch_min || ch > ch_max) {
                return false;
            }
        }

        return true;
    }

    public static String[] splitString(String s, int width) {
        try {
            if (width == 0) {
                String[] ret = new String[1];
                ret[0] = s;
                return ret;
            } else if (s.isEmpty()) {
                return new String[0];
            } else if (s.length() <= width) {
                String[] ret = new String[1];
                ret[0] = s;
                return ret;
            } else {
                int NumSeg = s.length() / width + 1;
                String[] ret = new String[NumSeg];
                int startPos = 0;

                for (int i = 0; i < NumSeg - 1; i++) {
                    ret[i] = s.substring(startPos, ((width * (i + 1))));
                    startPos = (i + 1) * width;
                }
                ret[NumSeg - 1] = s.substring(startPos, s.length());
                return ret;
            }
        } catch (Exception e) {
            return new String[0];
        }
    }
}
