package org.LetterRecognition.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Class for getting settings information from settings table in database */
public class SettingsDaoImpl {
    private static final Logger log = LoggerFactory.getLogger(SettingsDaoImpl.class);
    /** Used for receiving all settings in settings table from database
     * @return all settings listed in settings table from database
     */
    public static List<Boolean> getSettings() {
        ResultSet settingsList = Database.selectStatement("SELECT * FROM settings WHERE settings_ID = 1");
        List<Boolean> letterEnabled = new ArrayList<>(Arrays.asList());
        try {
            while (settingsList.next()){
                for (int index = 2; index < 54; index++){
                    letterEnabled.add(settingsList.getBoolean(index));
                }
            }
        } catch (SQLException e) {
            log.error("SQLException" + e);
        }

        return letterEnabled;
    }

    /** Used for adding settings record in settings table from database
     * @param lettersEnabled list of current settings
     * @return amount of rows updated in database indicating add was successful
     */
    public static int insertSettings(List<Boolean> lettersEnabled) {
        int rowsAffected = 0;
        String statement = "INSERT INTO settings ( settings_ID," +
                "41_enabled, 42_enabled, 43_enabled, 44_enabled, 45_enabled, 46_enabled, 47_enabled, " +
                "48_enabled, 49_enabled, 4a_enabled, 4b_enabled, 4c_enabled, 4d_enabled, 4e_enabled, " +
                "4f_enabled, 50_enabled, 51_enabled, 52_enabled, 53_enabled, 54_enabled, 55_enabled, " +
                "56_enabled, 57_enabled, 58_enabled, 59_enabled, 5a_enabled, " +
                "61_enabled, 62_enabled, 63_enabled, 64_enabled, 65_enabled, 66_enabled, 67_enabled, " +
                "68_enabled, 69_enabled, 6a_enabled, 6b_enabled, 6c_enabled, 6d_enabled, 6e_enabled, " +
                "6f_enabled, 70_enabled, 71_enabled, 72_enabled, 73_enabled, 74_enabled, 75_enabled, " +
                "76_enabled, 77_enabled, 78_enabled, 79_enabled, 7a_enabled, " +
                "Create_Date, Last_Update) " +
                "VALUES(1," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "NOW(),NOW())";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Database.connection.prepareStatement(statement);
            int enabledIndex;
            for (int index = 1; index < 53; index++) {
                enabledIndex = index - 1;
                preparedStatement.setBoolean(index, lettersEnabled.get(enabledIndex));
            }
            rowsAffected = preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            log.error("SQLException" + e);
        }
        return rowsAffected;
    }

    /** Used for updating settings record in settings table from database
     * @param lettersEnabled list of current settings
     * @return amount of rows updated in database indicating update was successful
     */
    public static int updateSettings(List<Boolean> lettersEnabled) {
        int rowsAffected = 0;
        String statement = "UPDATE settings SET " +
                "41_enabled = ?, 42_enabled = ?, 43_enabled = ?, 44_enabled = ?, 45_enabled = ?, 46_enabled = ?, " +
                "47_enabled = ?, 48_enabled = ?, 49_enabled = ?, 4a_enabled = ?, 4b_enabled = ?, 4c_enabled = ?, " +
                "4d_enabled = ?, 4e_enabled = ?, 4f_enabled = ?, 50_enabled = ?, 51_enabled = ?, 52_enabled = ?, " +
                "53_enabled = ?, 54_enabled = ?, 55_enabled = ?, 56_enabled = ?, 57_enabled = ?, 58_enabled = ?, " +
                "59_enabled = ?, 5a_enabled = ?," +
                "61_enabled = ?, 62_enabled = ?, 63_enabled = ?, 64_enabled = ?, 65_enabled = ?, 66_enabled = ?, " +
                "67_enabled = ?, 68_enabled = ?, 69_enabled = ?, 6a_enabled = ?, 6b_enabled = ?, 6c_enabled = ?, " +
                "6d_enabled = ?, 6e_enabled = ?, 6f_enabled = ?, 70_enabled = ?, 71_enabled = ?, 72_enabled = ?, " +
                "73_enabled = ?, 74_enabled = ?, 75_enabled = ?, 76_enabled = ?, 77_enabled = ?, 78_enabled = ?, " +
                "79_enabled = ?, 7a_enabled = ?," +
                "Last_Update = NOW() WHERE settings_ID = 1";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = Database.connection.prepareStatement(statement);
            int enabledIndex;
            for (int index = 1; index < 53; index++) {
                enabledIndex = index - 1;
                preparedStatement.setBoolean(index, lettersEnabled.get(enabledIndex));
            }
            rowsAffected = preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            log.error("SQLException" + e);
        }
        return rowsAffected;
    }
}
