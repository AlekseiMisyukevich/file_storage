package com.epam.storage.domain;

import java.util.Properties;

public class Document extends Metadata {

	private static final long serialVersionUID = -4423979263146982282L;
	private byte[] data;

	public Document(String fileName, byte[] data) {
		super(fileName);
		this.data = data;
	}

	public Document(Properties props) {
		super(props);
	}

	public Document(Metadata metadata) {
		super(metadata.getUuid(), metadata.getFileName());
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public Metadata getMetadata() {
		return new Metadata (getUuid(), getFileName());
	}

}
