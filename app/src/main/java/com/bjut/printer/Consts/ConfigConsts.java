package com.bjut.printer.Consts;

/**
 * Created by johnsmac on 9/11/16.
 */
public interface ConfigConsts {

    String pla_heater_temperature = "200", pla_bed_temperature = "50";
    String abs_heater_temperature = "230", abs_bed_temperature = "75";
    String retract_length = "0", retract_feedrate = "0", Z_lift = "0", accel_retract = "0";
    String extruder_test_step = "5", extruder_test_feedrate = "100";
    String pid_p = "7.00", pid_i = "0.10", pid_d = "1.00";
    String flow = "100", accel = "3000";
    String v_x = "500", v_y = "500", v_z = "5", v_e = "25";
    String a_x = "9000", a_y = "9000", a_z = "100", a_e = "10000";
    String steps_x = "80.33", steps_y = "80.33", steps_z = "3200", steps_e = "96.4";
    int head_num = 2;

}
