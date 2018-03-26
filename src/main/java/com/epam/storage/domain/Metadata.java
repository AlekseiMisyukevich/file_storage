package com.epam.storage.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Metadata implements Serializable {
	
	private static final long serialVersionUID = 5046815238233591018L;
		
	private static final Logger LOG = LoggerFactory.getLogger(Metadata.class);
	
	public static final String PROP_UUID = "uuid";
	public static final String PROP_FILE_NAME = "file-name";
	
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

	protected String uuid;
	protected String fileName;
	protected Calendar creationDate;
		
	public Metadata() {
		super();
	}	
	
	public Metadata (String fileName) {
		this.uuid = UUID.randomUUID().toString();
		this.fileName = fileName;		
	}

	public Metadata(String uuid, String fileName) {

		super();
		this.uuid = uuid;
		this.fileName = fileName;

	}
	
	public Metadata(Properties props) {
		this(props.getProperty(PROP_UUID), props.getProperty(PROP_FILE_NAME));		
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}		
	
	public Properties createProperties() {
		
        Properties props = new Properties();
        props.setProperty(PROP_UUID, getUuid());
        props.setProperty(PROP_FILE_NAME, getFileName());
        
        return props;        
	}
	

}
