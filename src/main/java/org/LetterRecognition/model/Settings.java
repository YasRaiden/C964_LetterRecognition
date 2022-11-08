package org.LetterRecognition.model;


import org.LetterRecognition.DAO.Database;
import org.LetterRecognition.DAO.SettingsDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Class controls settings for enabled letters.*/
public class Settings {
    private static final Logger log = LoggerFactory.getLogger(Database.class);

    public static final List<String> letters = Arrays.asList(
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "4a", "4b", "4c",
            "4d", "4e", "4f", "50", "51", "52", "53", "54", "55", "56", "57", "58",
            "59", "5a", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6a",
            "6b", "6c", "6d", "6e", "6f", "70", "71", "72", "73", "74", "75", "76",
            "77", "78", "79", "7a");

    private static List<Boolean> lettersEnabled = Arrays.asList(
            true, true, true, true, true, true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, true, true, true, true,
            true, true, true, true);

    private static int letterCount = updateLetterCounter();


    /** Set single specific letter as enabled from list.
     * @param letter the hex value of the specified letter
     * @param enabled specified value of enable or disable
     */
    public static void setLetterEnabled(String letter, boolean enabled){
        int letterIndex = letters.indexOf(letter);
        lettersEnabled.set(letterIndex, enabled);
        updateLetterCounter();
    }

    /** Returns whether a single letter is enabled or disabled.
     * @param letter the hex value of the specified letter
     * @return value if letter is enabled or disabled
     */
    public static boolean getLetterEnabled(String letter){
        int letterIndex = letters.indexOf(letter);
        return lettersEnabled.get(letterIndex);
    }

    /** Updates ALL upper case letters to be enabled. */
    public static void enableAllUpperCase(){
        for (int i = 0; i < 26; i++){
            lettersEnabled.set(i, true);
        }
        updateLetterCounter();
    }

    /** Updates ALL upper case letters to be disabled. */
    public static void disableAllUpperCase(){
        for (int i = 0; i < 26; i++){
            lettersEnabled.set(i, false);
        }
        updateLetterCounter();
    }

    /** Updates ALL lower case letters to be enabled. */
    public static void enableAllLowerCase(){
        for (int i = 26; i < 52; i++){
            lettersEnabled.set(i, true);
        }
        updateLetterCounter();
    }

    /** Updates ALL lower case letters to be disabled. */
    public static void disableAllLowerCase(){
        for (int i = 26; i < 52; i++){
            lettersEnabled.set(i, false);
        }
        updateLetterCounter();
    }

    /** Updates letter counter based on all letters currently enabled.
     * @return numerical count of all enabled letters
     */
    public static int updateLetterCounter(){
        int count = 0;
        for (int i = 0; i < 52; i++){
            if(lettersEnabled.get(i)){
                count++;
            }
        }
        letterCount = count;
        return count;
    }

    /**
     * @return numerical count of all enabled letters
     */
    public static int getLetterCount() {
        updateLetterCounter();
        return letterCount;
    }

    /** Loads current letter enabled status from database.
     * If no record exist in database a record will be created with default values of enabled.
     */
    public static void loadSettings() {
        if(Database.connectionOpen()) {
            List<Boolean> settingsList = SettingsDaoImpl.getSettings();
            if (settingsList.size() == 52) {
                lettersEnabled = settingsList;
            } else {
                SettingsDaoImpl.insertSettings(lettersEnabled);
            }
        }
        else {
                log.error("Connection to database is closed!!! Default settings will be used. Setting will not be able to save.");
        }
    }

    /** Saves current letter settings via lettersEnabled List to database. */
    public static void saveSettings() {
        if(Database.connectionOpen()) {
            SettingsDaoImpl.updateSettings(lettersEnabled);
        }
        else {
            log.error("Connection to database is closed!!!");
        }
    }

    /** Converts hex value to string value
     * @param hexValue the hex value of a letter
     * @return string value of a letter
     */
    public static String hexToChar(String hexValue){
        StringBuilder charOutput = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2) {
            String str = hexValue.substring(i, i + 2);
            charOutput.append((char) Integer.parseInt(str, 16));
        }
        return charOutput.toString();
    }

    /** Creates list of letters currently enabled and converts Hex to String.
     * @return Subset of letters enabled
     */
    public static List<String> generateLetterQueue(){
        List<String> letterQueue = new ArrayList<>(Arrays.asList());
        for (int i = 0; i < letters.size(); i++){
            Boolean enabled = lettersEnabled.get(i);
            if(enabled){
                String selectedLetter = hexToChar(letters.get(i));
                letterQueue.add(selectedLetter);
            }
        }
        return letterQueue;
    }

    /** Returns string letter by selected index from Hex value.
     * @param index index of selected letter
     * @return letter string value by index value
     */
    public static String getLetter(int index) {
        return hexToChar(letters.get(index));
    }

    /** Returns hex letter by selected index from Hex value list.
     * @param index index of selected letter
     * @return letter hex value by index value
     */
    public static String getHexLetter(int index) {
        return letters.get(index);
    }

}
