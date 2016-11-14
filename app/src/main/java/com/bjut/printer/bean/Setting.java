package com.bjut.printer.bean;

public class Setting {
	private int id;
	private String name;
	private String value;
	private String unit;
	private String rang;


	/**
	 *
	 * TODO ADD CONSTRUCT TO RETURN THIS OBJ
	 * */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getRang() {
		return rang;
	}
	public void setRang(String rang) {
		this.rang = rang;
	}
	
}
