package com.epam.storage.compression;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.http.HttpHeaders;

public class GZIPResponseWrapper extends HttpServletResponseWrapper {
	
	private GZIPResponseStream responseStream;
	private ServletOutputStream outputStream;
	
	
	public GZIPResponseWrapper(HttpServletResponse response) {
		super(response);
		response.addHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
	}
	
	
	
	

}
