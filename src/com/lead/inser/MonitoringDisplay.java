package com.lead.inser;

public interface MonitoringDisplay {
	public static final String NEW_MONITOR_DATA = "com.lead.inser.NEW_MONITOR_DATA";
	public static final String INDUCTIVE_RIGHT = "com.lead.inser.INDUCTIVE_RIGHT";
	public static final String INDUCTIVE_LEFT = "com.lead.inser.INDUCTIVE_LEFT";
	public static final String INDUCTIVE_KEY = "com.lead.inser.INDUCTIVE_KEY";
	public static final String INDUCTIVE_KEY_ATTACHED = "com.lead.inser.INDUCTIVE_KEY_ATTACHED";
	public static final String INDUCTIVE_KEY_DETACHED = "com.lead.inser.INDUCTIVE_KEY_DETACHED";
	public static final String INCLINATION_BODY = "com.lead.inser.INCLINATION_BODY";
	public static final String INCLINATION_KEY = "com.lead.inser.INCLINATION_LEFT";
	public static final String INCLINATION_RIGHT = "com.lead.inser.INCLINATION_RIGHT";
	public static final String PRESSURE = "com.lead.inser.PRESSURE";

	void inductive_left(boolean value);

	void inductive_right(boolean value);

	void inductive_key(boolean value);
	
	void inductive_key_attached(boolean value);
	
	void inductive_key_detached(boolean value);

	void inclination_body(double value);

	void inclination_right(double value);

	void inclination_key(double value);

	void pressure(double value);

}
