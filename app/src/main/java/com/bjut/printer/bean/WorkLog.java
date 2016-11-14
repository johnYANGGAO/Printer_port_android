package com.bjut.printer.bean;

public class WorkLog {
	private int id;
	private String sendtime;
	private String senddata;
	private int type;  //  1.X 2.Y 3.Z 4.backhome 5.gettemperature 6.location 7.E 8.heater 9.bed
					//	10.print_speed 11.mode 12.resume 13.retraction 14.unfinished 15.stop 16.error
	private String value;
	private String valuex;
	private String valuey;
	private String valuez;
	private String backtime;
	private String backdata;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
    // TODO  recreate construct for  collecting  parameters
	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getSenddata() {
		return senddata;
	}

	public void setSenddata(String senddata) {
		this.senddata = senddata;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuex() {
		return valuex;
	}

	public void setValuex(String valuex) {
		this.valuex = valuex;
	}

	public String getValuey() {
		return valuey;
	}

	public void setValuey(String valuey) {
		this.valuey = valuey;
	}

	public String getValuez() {
		return valuez;
	}

	public void setValuez(String valuez) {
		this.valuez = valuez;
	}

	public String getBacktime() {
		return backtime;
	}

	public void setBacktime(String backtime) {
		this.backtime = backtime;
	}

	public String getBackdata() {
		return backdata;
	}

	public void setBackdata(String backdata) {
		this.backdata = backdata;
	}

}
