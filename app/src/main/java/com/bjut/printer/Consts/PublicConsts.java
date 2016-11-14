package com.bjut.printer.Consts;

/**
 * Created by johnsmac on 9/11/16.
 */
public interface PublicConsts {


    String WEBADDRESS = "http://42.96.195.234/3d/Home/Mobile/";
    String kEntryVersionName = "version";
    String kEntryRatioName = "ratio";
    int timeoutConnection = 3000;
    int timeoutSocket = 3000;
    String UPDATE_INFO = "update_info";
    String PROGRESS_RATE_ACTION = "progress_rate";
    String DOWNLOAD_DIALOG_ACTION = "download_action";
    String DOWNLOAD_OVER_ACTION = "download_finished";

    String ERROR_ACTION = "error_action";
    String WARNING_ACTION = "warning_action";
    String WARNING_CONTINUE_PRINT_ACTION = "warning_continue_print";
    String ERROR_ACTION_REBOOT = "reboot_error";
    String NOTIFICATION_SUCCESS = "success_title";
    String START_SERVERSOCKETSERVICE_ACTION = "socketService";
    String MECHANICAL_STATUS = "mechanical_status";
    String GATE_STATUS = "gate_status";
    String GATE_PLAY = "gate_paly";
    String PROGRESS_LABEL = "progress_label";
    String ACTION_HEATER_CURRENT_B = "text_heater_current_b";
    String MODIFY_DISPLAY_ACTION = "modify_display";
    String ACTION_HEATER_TARGET_B = "text_heater_target_b";
    String ACTION_MULTIPLE_HEATER_TARGET_TEXT = "multiple_text";
    String ACTION_HEATER_TARGET = "text_heater_target";
    String ACTION_BUNDLE_DISPLAY = "bundle_display";
    String ACTION_HEATER_CURRENT_PROGRESS = "heaterCurrent_progress";
    String ACTION_HEATER_CURRENT_PROGRESS_B = "heaterCurrent_progress_b";
    String ACTION_TEXT_X = "text_x";
    String ACTION_TEXT_Y = "text_y";
    String ACTION_TEXT_Z = "text_z";
    String ACTION_BET_TARGET = "bed_target";
    String ACTION_TEXT_SPEED = "text_speed";
    String ACTION_TEXT_XYZ = "text_xyz";
    String ACTION_TEXT_VERSION = "text_version";
    String ACTION_LEVELING_HIGH="leveling_high";

    String ACTION_LEVELING_NOTHING="leveling_nothing";
    String ACTION_LEVELING_OK="leveling_ok";
    String ACTION_LEVELING_LOW="leveling_low";

    int LEVELING_HIGH_WHAT=22;
    int LEVELING_LOW_WHAT=20;
    int LEVELING_NOTHING_WHAT=19;
    int LEVELING_OK_WHAT=21;
    String TURN_FAN_OFF="turn_fan_off";
    String TURN_FAN_ON="turn_fan_on";
    int FAN_OFF_WHAT=25;
    int FAN_ON_WHAT=23;

    String ACTION_BEGIN_PRINT = "begin_print";
    int BEGIN_PRINT = 26;

    int TEXT_VERSION_WHAT = 29;
    int DOWNLOAD_OVER_WHAT = 30;
    int DOWNLOAD_DIALOG_WHAT = 31;
    int TEXT_SPEED_WHAT = 39;
    int TEXT_X_WHAT = 37;
    int TEXT_Y_WHAT = 36;
    int TEXT_Z_WHAT = 35;
    int TEXT_XYZ_WHAT = 34;
    int PROGRESS_RATE_WHAT = 32;
    int BET_TARGET_WHAT = 40;
    int HEATER_TARGET_WHAT = 41;
    int HEATER_TARGET_ALL_WHAT = 44;
    int HEATER_CURRENT_PROGRESS_WHAT = 43;
    int HEATER_CURRENT_PROGRESS_B_WHAT = 38;
    int UPDATE_INFO_WHAT = 33;
    int BUNDLE_DISPLAY_WHAT = 42;
    int HEATER_TARGET_B_WHAT = 45;
    int HEATER_CURRENT_B_WHAT = 46;
    int PROGRESS_LABEL_WHAT = 47;
    int GATE_PLAY_WHAT = 48;
    int GATE_STATUS_WHAT = 49;
    int MECHANICAL_STATUS_MSG_WHAT = 50;
    int START_SOCKETSERVICE = 51;
    int MODIFY_DISPLAY = 52;
    int GCODE_LENGTH_SHORT = 11;
    int GCODE_LENGTH = 5;
    int BEGINPRINT_NOTE = 1;
    int POPUPWINDOW_CONFIRM = 28;
    int POPUP_PRINT_WHAT = 27;
    int USB_STATUS_THREAD_ON = 12;
    int USB_STATUS_THREAD_OFF = 13;

    String PAUSE_PRINT = "pause_print";
    int PAUSE_PRINT_WHAT = 24;

    String PARSE_TEMP = "pars_temp";
    int PARSE_TEMP_WHAT = 4;
}
