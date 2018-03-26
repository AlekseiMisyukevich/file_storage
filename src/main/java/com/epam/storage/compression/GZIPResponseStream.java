package com.epam.storage.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

public class GZIPResponseStream extends ServletOutputStream {
	
	protected ByteArrayOutputStream baos = null;
	protected GZIPOutputStream gzipstream = null;
	
	protected HttpServletResponse res = null;
	protected ServletOutputStream output = null;
	
	private WriteListener listener = null;
		
	public GZIPResponseStream (HttpServletResponse response) throws IOException {
		
		super();
		
		
	}
	
	@Override
	public boolean isReady() {
		
		return false;
	}

	@Override
	public void setWriteListener (WriteListener arg0) {
		
		this.listener = arg0;
		
	}

	@Override
	public void write (int arg0) throws IOException {
		
		
	}
	
	public void flush() {
		
	}
	
	

}
