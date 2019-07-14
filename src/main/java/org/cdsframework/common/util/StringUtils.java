/**
 * The MTS support core project contains client related utilities, data transfer objects and remote EJB interfaces for communication with the CDS Framework Middle Tier Service.
 *
 * Copyright (C) 2016 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see https://www.hln.com/services/open-source/ or send
 * correspondence to ice@hln.com.
 */
package org.cdsframework.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Provides a class to store commonly used string utility functions.
 *
 * @author HLN Consulting, LLC
 */
public class StringUtils {

    private static final Random random = new Random();

    private synchronized static Random getRandom() {
        return random;
    }

    /**
     * Take a list can convert it into sql like value: ("item1","item2", ...)
     *
     * @param likeItems
     * @return
     */
    public static String makeSqlLikeList(String[] likeItems) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        int count = 0;
        for (Object item : likeItems) {
            if (item instanceof String) {
                sb.append("'");
                sb.append(item);
                sb.append("'");
            } else if (item instanceof Integer || item instanceof Long) {
                sb.append(item.toString());
            } else {
                // unsupported type...
                sb.append("'");
                sb.append(item.toString());
                sb.append("'");
            }
            count++;
            if (count != likeItems.length) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Format a string for html output so that it breaks at a particular length.
     *
     * @param stringInput
     * @param length
     * @return formatted string
     */
    public static String splitStringByCount(String stringInput, int length) {
        Character next;
        Character prev = '*';
        StringBuilder sb = new StringBuilder();
        int nbOutputed = 0;
        for (int i = 0; i < stringInput.length(); i++) {
            next = stringInput.charAt(i);
            if (nbOutputed >= length) {
                if (!prev.equals(' ') && !next.equals(' ')) {
                    sb.append("&#172;");
                }
                sb.append("\n");
                nbOutputed = 0;
            }
            prev = next;
            sb.append(next);
            nbOutputed++;
        }
        return sb.toString();
    }

    /**
     * Apply titlecase to a string...
     *
     * @param inputString
     * @return
     */
    public static String titleCase(String inputString) {
        String convertedString = "";
        if (inputString != null) {
            for (int i = 0; i < inputString.length(); i++) {
                String next = inputString.substring(i, i + 1);
                if (i == 0) {
                    convertedString += next.toUpperCase();
                } else {
                    convertedString += next.toLowerCase();
                }
            }
        }
        return convertedString;
    }

    /**
     * Convert a camelCase string into a space separated one...
     *
     * @param inputString
     * @return
     */
    public static String unCamelize(String inputString) {
        Pattern p = Pattern.compile("\\p{Lower}\\p{Lu}|\\p{Lu}\\p{Lu}\\p{Lower}");
        Matcher m = p.matcher(inputString);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group().charAt(0) + " " + m.group().substring(1));
        }
        m.appendTail(sb);
        if (sb.length() > 0 && !inputString.isEmpty()) {
            sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
        }
        return sb.toString().trim();
    }

    public static String unCamelize(String s, String replacement) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"),
                replacement);
    }    

    /**
     * Returns a null is the string is empty.
     *
     * @param str
     * @return
     */
    public static String convEmptyToNull(String str) {
        if (str != null && str.trim().length() == 0) {
            return null;
        } else {
            return str;
        }
    }

    /**
     * Returns an empty string if the object is null.
     *
     * @param str
     * @return
     */
    public static String convNullToEmpty(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * Pad apostrophes.
     *
     * @param str
     * @return
     */
    public static String padApostrophes(String str) {
        return padApostrophes(str, true);
    }

    /**
     * Pad apostrophes.
     *
     * @param str
     * @param pConvToUpperCase
     * @return
     */
    public static String padApostrophes(String str, boolean pConvToUpperCase) {
        int i;
        if (str == null) {
            return null;
        }
        StringBuilder s = new StringBuilder((pConvToUpperCase ? str.toUpperCase() : str));
        int len = s.length();
        for (i = 0; i < len; i++) {
            if (s.charAt(i) == '\'') {
                s.insert(i, '\'');
                i++;
                len++;
            }
        }
        return s.toString();
    }

    /**
     * Returns boolean if a string is a valid medicaid number.
     *
     * @param p_input
     * @return
     */
    public static boolean isValidMedicaidNo(String p_input) {
        // not a required field, so null or empty is okay.
        if (isEmpty(p_input)) {
            return true;
        }
        String sTrimInput = p_input.trim().toUpperCase();
        // returns True if the passed String is 8 digits in 'AA#####A' format, False otherwise
        if (sTrimInput.length() != 8) {
            return false;
        }
        boolean ret = true;  // until proven False
        for (int i = 0; ret && (i < sTrimInput.length()); i++) {
            char c = sTrimInput.charAt(i);
            if (i == 0 || i == 1 || i == 7) {
                if (!Character.isLetter(c)) {
                    ret = false;
                }
            } else if (i == 2 || i == 3 || i == 4 || i == 5 || i == 6) {
                if (!Character.isDigit(c)) {
                    ret = false;
                }
            }
        }
        return ret;
    }

    /**
     * Returns a formatted telephone number.
     *
     * @param telephone
     * @return
     */
    public static String formatTelephone(String telephone) {
        if (isEmpty(telephone)) {
            return null;
        }
        char c;
        int l = telephone.length();
        StringBuilder strWorker = new StringBuilder(l);
        for (int i = 0; i < l; i++) {
            c = telephone.charAt(i);
            if (Character.isDigit(c)) {
                strWorker.append(c);
            }
        }
        return strWorker.toString();
    }

    /**
     * Returns a formatted telephone number with dashes.
     *
     * @param telephone
     * @return
     */
    public static String formatTelephoneDashes(String telephone) {
        if (isEmpty(telephone)) {
            return "";
        }
        if (!isValidTelephone(telephone)) {
            return telephone;
        }
        return getTelephoneAreaCode(telephone) + "-"
                + getTelephoneExchange(telephone) + "-"
                + getTelephoneExtn(telephone);
    }

    /**
     * Checks if a string is a valid telephone number.
     *
     * @param p_input
     * @return
     */
    public static boolean isValidTelephone(String p_input) {
        // returns True if the passed String is 10 digits, False otherwise
        if (p_input == null || p_input.trim().length() != 10) {
            return false;
        }
        boolean ret = true;  // until proven False
        String sTrimInput = p_input.trim();
        for (int i = 0; ret && (i < sTrimInput.length()); i++) {
            char c = sTrimInput.charAt(i);
            if (!Character.isDigit(c)) {
                ret = false;
            }
        }
        return ret;
    }

    public static String getNumbersOnly(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        if (string != null) {
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (Character.isDigit(c)) {
                    stringBuilder.append(c);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static String formatTelephoneWithOptionalExt(String telephoneNumber, String ext) {
        String trimmedTelephone = getNumbersOnly(telephoneNumber);
        if (trimmedTelephone.isEmpty()) {
            return trimmedTelephone;
        }
        String trimmedExt = getNumbersOnly(ext);
        String result = formatTelephoneDashes(trimmedTelephone);
        if (!trimmedExt.isEmpty()) {
            result += " x" + trimmedExt;
        }
        return result;
    }
    
    public static String formatTime(long timeSeconds) {
        long remainingSecs = timeSeconds;
        int nDays = 0;
        int nHours = 0;
        int nMins = 0;

        float fHours = (float)remainingSecs / (60f * 60f);
        nHours = (int)Math.floor(fHours);
        String sHours = (nHours <= 9 ? "0" : "") + nHours;
        if(nHours >= 1)
        {
            remainingSecs -= (nHours * 60*60);
        }

        float fMins = (float)remainingSecs / (60f);
        nMins = (int)Math.floor(fMins);
        String sMins = (nMins <= 9 ? "0" : "") + nMins;
        if(nMins >= 1)
        {
            remainingSecs -= (nMins * 60);
        }

        String sSecs = (remainingSecs <= 9 ? "0" : "") + remainingSecs;
        String ret = sHours + ":" + sMins;
        return ret;
    }

    /**
     * Returns the area code from a telephone number.
     *
     * @param p_phone
     * @return
     */
    public static String getTelephoneAreaCode(String p_phone) {
        if (p_phone == null || p_phone.trim().length() < 3) {
            return "";
        }
        String tmpPhone = p_phone.trim();
        return tmpPhone.substring(0, 3);
    }

    /**
     * Returns the telephone exchange of a telephone number.
     *
     * @param p_phone
     * @return
     */
    public static String getTelephoneExchange(String p_phone) {
        if (p_phone == null || p_phone.trim().length() < 6) {
            return "";
        }
        String tmpPhone = p_phone.trim();
        return tmpPhone.substring(3, 6);
    }

    /**
     * Returns the extension from a telephone number.
     *
     * @param p_phone
     * @return
     */
    public static String getTelephoneExtn(String p_phone) {
        if (p_phone == null || p_phone.trim().length() < 10) {
            return "";
        }
        String tmpPhone = p_phone.trim();
        return tmpPhone.substring(6, 10);
    }

    /**
     * Converts a string to an int.
     *
     * @param p_input
     * @return
     */
    public static int getIntFromString(String p_input) {
        if (isEmpty(p_input)) {
            return 0;
        }
        try {
            int n = Integer.parseInt(p_input);
            return n;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Converts a string to a long.
     *
     * @param p_input
     * @return
     */
    public static long getLongFromString(String p_input) {
        if (isEmpty(p_input)) {
            return (long) 0;
        }
        try {
            long n = Long.parseLong(p_input);
            return n;
        } catch (Exception e) {
            return (long) 0;
        }
    }

    /**
     * Converts a string to a float.
     *
     * @param p_input
     * @return
     */
    public static float getFloatFromString(String p_input) {
        if (isEmpty(p_input)) {
            return 0.0f;
        }
        try {
            float f = Float.parseFloat(p_input);
            return f;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    /**
     * Strip non alpha characters from a string.
     *
     * @param p_input
     * @return
     */
    public static String stripNonAlpha(String p_input) {
        if (isEmpty(p_input)) {
            return "";
        }
        StringBuilder buf = new StringBuilder(p_input.length());
        for (int i = 0; i < p_input.length(); i++) {
            char c = p_input.charAt(i);
            if (Character.isLetter(c)) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Strip non alpha numeric characters from a string.
     *
     * @param p_input
     * @return
     */
    public static String stripNonAlphanumeric(String p_input) {
        if (isEmpty(p_input)) {
            return "";
        }
        StringBuilder buf = new StringBuilder(p_input.length());
        for (int i = 0; i < p_input.length(); i++) {
            char c = p_input.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Strip alpha characters from a string.
     *
     * @param p_input
     * @return
     */
    public static String stripAlpha(String p_input) {
        if (isEmpty(p_input)) {
            return "";
        }
        StringBuilder buf = new StringBuilder(p_input.length());
        for (int i = 0; i < p_input.length(); i++) {
            char c = p_input.charAt(i);
            if (!Character.isLetter(c)) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Scrub a string.
     *
     * @param p_input
     * @return
     */
    public static String scrub(String p_input) {
        return scrub(p_input, false, true, false);
    }

    /**
     * Scrub a string.
     *
     * @param p_input
     * @param p_allow_digits
     * @return
     */
    public static String scrub(String p_input, boolean p_allow_digits) {
        return scrub(p_input, p_allow_digits, true, false);
    }

    /**
     * Scrub a string.
     *
     * @param p_input
     * @param p_allow_digits
     * @param toUpper
     * @param p_allow_asterisk
     * @return
     */
    public static String scrub(String p_input, boolean p_allow_digits, boolean toUpper, boolean p_allow_asterisk) {
        if (isEmpty(p_input)) {
            return "";
        }
        String sInput;
        if (toUpper) {
            sInput = p_input.trim().toUpperCase();
        } else {
            sInput = p_input.trim();
        }
        StringBuilder buf = new StringBuilder(sInput.length());
        for (int i = 0; i < sInput.length(); i++) {
            char c = sInput.charAt(i);
            if (Character.isLetter(c)
                    || (p_allow_digits && Character.isDigit(c))
                    || (p_allow_asterisk && c == '*')
                    || c == '-'
                    || c == '\''
                    || c == '.'
                    || c == ','
                    || c == ':'
                    || c == ' ') {
                buf.append(c);
            }
        }
        String ret = buf.toString();
        if (ret != null) {
            ret = ret.trim();
        }
        return ret;
    }

    /**
     * Check is a string is empty.
     *
     * @param p_input
     * @return
     */
    public static boolean isEmpty(String p_input) {
        return (p_input == null || p_input.trim().length() == 0);
    }
    
    /**
     * Check if a string is numeric.
     *
     * @param p_input
     * @return
     */
    public static boolean isNumeric(String p_input) {
        if (isEmpty(p_input)) {
            return false;
        }
        boolean ret = true;  // until proven False
        for (int i = 0; ret && i < p_input.length(); i++) {
            char c = p_input.charAt(i);
            if (!Character.isDigit(c)) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Check if a string contains only alphanumeric characters. Only [A-Za-z0-9] although this doesn't use the regex library for
     * performance reasons.
     *
     * @param str
     * @return
     */
    public static boolean isAlphanumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a) {
                return false;
            }
        }
        return true;
    }

    /**
     * Append an ellipsis to a string.
     *
     * @param strToAppend
     * @param lenLimit
     * @return
     */
    public static String appendEllipsis(String strToAppend, int lenLimit) {
        String retString = strToAppend;
        if (strToAppend != null) {
            int len = strToAppend.length();
            if (lenLimit < len) {
                retString = strToAppend.substring(0, lenLimit) + "...";
            }
        }
        return retString;
    }

    /**
     * Init caps a string.
     *
     * @param p_input
     * @return
     */
    public static String getInitCap(String p_input) {
        if (isEmpty(p_input)) {
            return "";
        }
        StringBuilder buf = new StringBuilder(p_input.length());
        boolean upcase = true;
        for (int i = 0; i < p_input.length(); i++) {
            char c = p_input.charAt(i);
            if (upcase) {
                buf.append(Character.toUpperCase(c));
            } else {
                buf.append(Character.toLowerCase(c));
            }
            upcase = !Character.isLetter(c);
        }
        return buf.toString();
    }

    /**
     * Escape HTML.
     *
     * @param s
     * @return
     */
    public static String escapeHTML(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                // single quote
                case 39:                    
                    sb.append("&#39;");
                    break;
                    
                /*                case 'à': sb.append("&agrave;");break;
                 case 'À': sb.append("&Agrave;");break;
                 case 'â': sb.append("&acirc;");break;
                 case 'Â': sb.append("&Acirc;");break;
                 case 'ä': sb.append("&auml;");break;
                 case 'Ä': sb.append("&Auml;");break;
                 case 'å': sb.append("&aring;");break;
                 case 'Å': sb.append("&Aring;");break;
                 case 'æ': sb.append("&aelig;");break;
                 case 'Æ': sb.append("&AElig;");break;
                 case 'ç': sb.append("&ccedil;");break;
                 case 'Ç': sb.append("&Ccedil;");break;
                 case 'é': sb.append("&eacute;");break;
                 case 'É': sb.append("&Eacute;");break;
                 case 'è': sb.append("&egrave;");break;
                 case 'È': sb.append("&Egrave;");break;
                 case 'ê': sb.append("&ecirc;");break;
                 case 'Ê': sb.append("&Ecirc;");break;
                 case 'ë': sb.append("&euml;");break;
                 case 'Ë': sb.append("&Euml;");break;
                 case 'ï': sb.append("&iuml;");break;
                 case '�?': sb.append("&Iuml;");break;
                 case 'ô': sb.append("&ocirc;");break;
                 case 'Ô': sb.append("&Ocirc;");break;
                 case 'ö': sb.append("&ouml;");break;
                 case 'Ö': sb.append("&Ouml;");break;
                 case 'ø': sb.append("&oslash;");break;
                 case 'Ø': sb.append("&Oslash;");break;
                 case 'ß': sb.append("&szlig;");break;
                 case 'ù': sb.append("&ugrave;");break;
                 case 'Ù': sb.append("&Ugrave;");break;
                 case 'û': sb.append("&ucirc;");break;
                 case 'Û': sb.append("&Ucirc;");break;
                 case 'ü': sb.append("&uuml;");break;
                 case 'Ü': sb.append("&Uuml;");break;
                 case '®': sb.append("&reg;");break;
                 case '©': sb.append("&copy;");break;
                 case '€': sb.append("&euro;"); break;
                 // be carefull with this one (non-breaking whitee space)
                 //case ' ': sb.append("&nbsp;");break;
                 */
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Force wrap a string.
     *
     * @param strToWrap
     * @param wrapPos
     * @return
     */
    public static String forceWrap(String strToWrap, int wrapPos) {
        final String METHODNAME = "forceWrap ";
        if (strToWrap == null) {
            return "";
        }
        int len = strToWrap.length();
        if (len == 0) {
            return "";
        }
        if (len <= wrapPos) {
            return strToWrap;
        }
        // Parse the string adding spaces
        String wrapString = strToWrap;
        StringBuilder newString = new StringBuilder();
        int counter = 0;
        while (true) {
            counter++;
            int lenOfString = wrapString.length();
            if (lenOfString > wrapPos) {
                String temp = wrapString.substring(0, wrapPos);
                int subPos = wrapPos;
                // Check if there are any wrapCharacters in the string
                int wrapCharPos = temp.lastIndexOf(" ");
                if (wrapCharPos > 0) {
                    // We have a wrap Character, we need to reposition
                    temp = temp.substring(0, wrapCharPos + 1);
                    subPos = wrapCharPos + 1;
                } else {
                    temp += " ";
                }
                newString.append(temp);
                wrapString = wrapString.substring(subPos);
            } else {
                newString.append(wrapString);
                break;
            }
        }
        return newString.toString();
    }

    /**
     * Check is a string is a valid medicare number.
     *
     * @param p_input
     * @return
     */
    public static boolean isValidMedicareNo(String p_input) {
        // not a required field, so null or empty is okay.
        if (isEmpty(p_input)) {
            return true;
        }
        String sTrimInput = p_input.trim().toUpperCase();
        // returns True if the passed String is 13 digits in '##########A' {1,3} format, False otherwise
        // 1234567891ABC
        if (sTrimInput.length() < 10 || sTrimInput.length() > 13) {
            return false;
        }
        boolean ret = true;  // until proven False
        for (int i = 0; ret && (i < sTrimInput.length()); i++) {
            char c = sTrimInput.charAt(i);
            if (i >= 10) {
                if (!Character.isLetter(c)) {
                    ret = false;
                }
            } else // positions 0-9 have to be digits
            {
                if (!Character.isDigit(c)) {
                    ret = false;
                }
            }
        }
        return ret;
    }

    /**
     * Format a medicare number.
     *
     * @param medicare
     * @return
     */
    public static String formatMedicareNum(String medicare) {
        if (isEmpty(medicare) || !isValidMedicareNo(medicare)) {
            return "";
        }
        String sMedicare = medicare.trim().toUpperCase();
        // ###-###-####A
        return sMedicare.substring(0, 3) + "-"
                + sMedicare.substring(3, 6) + "-"
                + sMedicare.substring(6);
    }

    /**
     * Check if a string is a valid SSN.
     *
     * @param p_input
     * @return
     */
    public static boolean isValidSSN(String p_input) {
        // not a required field, so null or empty is okay.
        if (isEmpty(p_input)) {
            return true;
        }
        String sTrimInput = p_input.trim().toUpperCase();
        // returns True if the passed String is 9 digits, False otherwise
        if (sTrimInput.length() != 9) {
            return false;
        }
        boolean ret = true;  // until proven False
        for (int i = 0; ret && (i < sTrimInput.length()); i++) {
            char c = sTrimInput.charAt(i);
            if (!Character.isDigit(c)) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Returns a formatted SSN.
     *
     * @param ssn
     * @return
     */
    public static String formatSSN(String ssn) {
        if (isEmpty(ssn) || !isValidSSN(ssn)) {
            return "";
        }
        String sSsn = ssn.trim();
        // ###-##-####
        return sSsn.substring(0, 3) + "-"
                + sSsn.substring(3, 5) + "-"
                + sSsn.substring(5);
    }

    /**
     * Format an SSN secure.
     *
     * @param ssn
     * @return
     */
    public static String formatSSNsecure(String ssn) {
        if (isEmpty(ssn) || !isValidSSN(ssn)) {
            return "";
        }
        String sSsn = ssn.trim();
        return "***-**-" + sSsn.substring(5);
    }

    /**
     * Set HTML line breaks.
     *
     * @param p_input
     * @return
     */
    public static String setHtmlLineBreaks(String p_input) {
        if (isEmpty(p_input)) {
            return "";
        }
        StringBuilder buf = new StringBuilder(p_input.length() * 2);
        char[] acInput = p_input.toCharArray();
        for (int i = 0; i < acInput.length; i++) {
            if (acInput[i] == 13) {
                buf.append("<BR>");
            } else if (acInput[i] != 10) {
                buf.append(acInput[i]);
            }
        }
        return buf.toString();
    }

    /**
     * Return a hash from a plain text password.
     *
     * @param password
     * @return
     */
    public static String getShaHashFromString(String password) {
        String hash = "";
        MessageDigest md;
        if (password == null) {
            throw new IllegalArgumentException("update bytes were null!");
        }
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] byteData = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException na) {
            System.out.println(ExceptionUtils.getStackTrace(na));
        }
        return hash;
    }

    /**
     * Returns a 32 character hexadecimal random string.
     *
     * @return
     */
    public static String getHashId() {
        return getHashId(32);
    }

    /**
     * Return a random string of a particular length.
     *
     * @param length
     * @return
     */
    public static String getHashId(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(Integer.toHexString(getRandom().nextInt()));
        }
        return sb.toString().substring(0, length);
    }

    public static String getStringFromArray(String delimiter, Object... objectArray) {
        String[] stringArray = new String[objectArray.length];
        int i = 0;
        for (Object object : objectArray) {
            if (object != null) {
                stringArray[i] = object.toString();
            } else {
                stringArray[i] = null;
            }
            i++;
        }
        return getStringFromArray(stringArray, delimiter);
    }

    public static String getStringFromArray(String delimiter, String... stringArray) {
        return getStringFromArray(stringArray, delimiter);
    }

    public static String getStringFromArray(String[] stringArray, String delimiter) {
        List<String> arrayList = new ArrayList();
        Collections.addAll(arrayList, stringArray);
        return getStringFromArray(arrayList, delimiter);
    }

    /**
     * Return a string from a String array.
     *
     * @param stringArrayList
     * @param delimiter
     * @return String
     */
    public static String getStringFromArray(List<String> stringArrayList, String delimiter) {
        StringBuilder retValue = new StringBuilder();
        for (String string : stringArrayList) {
//            System.out.println("getStringFromArray string= " + string);
            if (string == null) {
                retValue.append("(empty)");
            } else {
                retValue.append(string);
            }
            retValue.append(delimiter);
        }
//        System.out.println("getStringFromArray retValue= " + retValue);
//        System.out.println("getStringFromArray return= " + (retValue.length() == 0 ? "" : retValue.substring(0, retValue.length() - delimiter.length())));
        return retValue.length() == 0 ? "" : retValue.substring(0, retValue.length() - delimiter.length());
    }

    public static String booleanToYN(Boolean arg) {
        String retString = null;
        if (arg != null) {
            if (arg) {
                retString = "Y";
            } else {
                retString = "N";
            }
        }
        return retString;
    }

    public static String booleanToYN(boolean arg) {
        return booleanToYN(Boolean.valueOf(arg));
    }

    public static boolean stringToBoolean(String booleanString) {
        return stringToBoolean(booleanString, false);
    }

    public static Boolean stringToBooleanObject(String booleanString) {
        return stringToBoolean(booleanString, true);
    }

    public static Boolean stringToBoolean(String booleanString, boolean returnNull) {
        // Return a null if returnNull is true, otherwise false if returnNull is false
        Boolean retBool = null;
        if (!returnNull) {
            retBool = false;
        }
        if (!StringUtils.isEmpty(booleanString)) {
            if (booleanString.equalsIgnoreCase("y")) {
                retBool = true;
            } else if (booleanString.equalsIgnoreCase("n")) {
                retBool = false;
            } else if (booleanString.equalsIgnoreCase("true")) {
                retBool = true;
            } else if (booleanString.equalsIgnoreCase("false")) {
                retBool = false;
            } else if (booleanString.equalsIgnoreCase("1")) {
                retBool = true;
            } else if (booleanString.equalsIgnoreCase("0")) {
                retBool = false;
            }
            else {
                throw new IllegalArgumentException("BooleanString argument contains invalid value");
            }
        }
        return retBool;
    }

    public static String scrubLeadingSlash(String urlComponent) {
        String result = urlComponent;
        while (result.charAt(0) == '/') {
            result = result.substring(1);
        }
        return result;
    }

    public static String getName(String firstName, String lastName) {
        String fullName = null;
        if (!StringUtils.isEmpty(firstName)) {
            fullName = firstName;
        }
        if (!StringUtils.isEmpty(lastName)) {
            if (!StringUtils.isEmpty(fullName)) {
                fullName += " " + lastName;
            } else {
                fullName = lastName;
            }
        }
        return fullName;
    }

    public static String trimLeadingTrailingSpaces(String string) {
        return trimTrailingSpaces(trimLeadingSpaces(string));
    }

    public static String trimLeadingSpaces(String string) {
        return string.replaceAll("^\\s+", "");
    }

    public static String trimTrailingSpaces(String string) {
        return string.replaceAll("\\s+$", "");
    }

    public static String stripNTrim(String stringToString, String stripString) {
        int textPos = stringToString.indexOf(stripString);
        if (textPos >= 0) {
            stringToString = stringToString.substring(textPos + 1);
            textPos = stringToString.indexOf(stripString);
            if (textPos >= 0) {
                stringToString = stringToString.substring(0, textPos);
            }
        }
        return StringUtils.trimLeadingTrailingSpaces(stringToString);
    }

    public static String stripIgnoreCase(String textWithTextToStrip, String textToStrip) {
        while (true) {
            int textPos = textWithTextToStrip.toLowerCase().indexOf(textToStrip.toLowerCase());
            if (textPos >= 0) {
                textWithTextToStrip = textWithTextToStrip.substring(0, textPos) + textWithTextToStrip.substring(textPos + textToStrip.length());
            } else {
                break;
            }
        }
        return textWithTextToStrip;
    }

    public static String classToList(Class cls) {
        String propertyName = null;
        if (cls != null) {
            propertyName = cls.getSimpleName() + "s";
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }
        return propertyName;
    }
    final static Pattern INDEX_REGEX = Pattern.compile("(\\s*\\[\\s*(\\d*)\\s*\\]\\s*)$");

    public static String addNameIndex(String name) {
        String result = name;
        if (name != null) {
            Matcher matcher = INDEX_REGEX.matcher(name);
            if (matcher.find()) {
                if (matcher.groupCount() == 2) {
                    result = matcher.replaceAll(String.format(" [%d]", new Integer(matcher.group(2)) + 1));
                }
            } else {
                result = name.trim() + " [1]";
            }
        }
        return result;
    }

    public static String getStackTrace(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private static final String NON_ALPHANUMERIC_WO_WHITESPACE_PATTERN = "[^a-zA-Z\\d\\s_]";
    private static final String NON_ALPHANUMERIC_WO_WHITESPACE_UNDERSCORE_PATTERN = "[^a-zA-Z\\d\\s]";

    private static final String NON_ALPHANUMERIC_W_WHITESPACE_PATTERN = "[^a-zA-Z\\d_]";
    private static final String NON_ALPHANUMERIC_W_WHITESPACE_UNDERSCORE_PATTERN = "[^a-zA-Z\\d]";    

    
    public static String stripNonAlphaNumberic(String data, boolean keepWhitespace) {
        return stripNonAlphaNumberic(data, keepWhitespace, true, false);
    }

    public static String stripNonAlphaNumberic(String data, boolean keepWhitespace, boolean keepUnderscore, boolean toUpper) {
        String result = null;
        if (data != null) {
            String pattern = NON_ALPHANUMERIC_WO_WHITESPACE_PATTERN;
            if (keepWhitespace) {
                if (keepUnderscore) {
                    pattern = NON_ALPHANUMERIC_WO_WHITESPACE_PATTERN;
                }
                else {
                    pattern = NON_ALPHANUMERIC_WO_WHITESPACE_UNDERSCORE_PATTERN;
                }
            }
            else {
                if (keepUnderscore) {
                    pattern = NON_ALPHANUMERIC_W_WHITESPACE_PATTERN;
                }
                else {
                    pattern = NON_ALPHANUMERIC_W_WHITESPACE_UNDERSCORE_PATTERN;
                }
            }
            result = data.replaceAll(pattern, "");
            if (toUpper) {
                result = result.toUpperCase();
            }
        }
        return result;
    }

}
