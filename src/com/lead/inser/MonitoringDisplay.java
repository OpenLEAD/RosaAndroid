package com.lead.inser;

public interface MonitoringDisplay {
	public static final String NEW_MONITOR_DATA = "com.lead.inser.NEW_MONITOR_DATA";
	public static final String INDUCTIVE2 = "com.lead.inser.INDUCTIVE2";
	public static final String INCLINATION = "com.lead.inser.INCLINATION";

	void inductive2(boolean value);
	void inclination(double value);
	
}
