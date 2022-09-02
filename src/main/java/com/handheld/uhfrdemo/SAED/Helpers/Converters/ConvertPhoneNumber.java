package com.handheld.uhfrdemo.SAED.Helpers.Converters;

public class ConvertPhoneNumber {

    /**
     * Convert any phone number entered into the regular syrian phone format with a number of digits 9 <br>
     * if phone was 009639xxxxxxxx => 9xxxxxxxx<br>
     * if phone was +9639xxxxxx=> 9xxxxxxxx<br>
     * if phone was 9xxxxx=> 9xxxxxx<br>
     * If the wrong phone number is entered, a blank String is returned
     *
     * @param phoneNumber syrian phone number
     * @return regular syrian phone format
     */
    public static String convertPhoneNumberToRegular(String phoneNumber) {
        StringBuilder phone = new StringBuilder(phoneNumber);
        if (phone.charAt(0) == '0') {
            if (phone.charAt(1) == '0') {
                phone.delete(0, 5);
            } else {
                phone.deleteCharAt(0);
            }
            return phone.toString();
        } else if (phone.charAt(0) == '+') {
            phone.delete(0, 4);
            return phone.toString();
        } else if (phone.charAt(0) == '9') {
            return phone.toString();
        } else {
            return "";
        }
    }
}
