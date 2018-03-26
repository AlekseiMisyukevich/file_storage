package com.epam.storage.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epam.storage.service.StorageService;

@RestController("/upload-handler")
public class UploadController {
	
private static final Logger LOG = LoggerFactory.getLogger(StorageController.class);
	
	@Autowired 
	private StorageService service;	
	private String msg;
	
	@PostMapping(value = "/file-upload", headers=("content-type = multipart/*"))
	public ResponseEntity<?> handleUpload(
			@RequestBody(required = true) MultipartFile file) {
		
		if(!file.isEmpty()) {
			
			try {
				service.save(file.getOriginalFilename(), file.getBytes());
				return new ResponseEntity<>(HttpStatus.CREATED); 
			}		

			catch (IOException e) {
				msg = String.format("Failed to upload file > ", e.getMessage());
				LOG.debug(msg);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		
		else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@PostMapping(value = "/file-upload-to", headers = {"content-type = multipart/*"})
	public ResponseEntity<?> handleUpload( 
			@RequestBody(required = true) MultipartFile file,
			@RequestParam (value = "relPath", required = true) String relPath) {
		
		if (file.isEmpty() || relPath.isEmpty() || file.getSize() == 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		try {
			service.saveTo(relPath, file.getOriginalFilename(), file.getBytes());
		}
		
		catch (IOException e) {
			LOG.debug(e.getCause().toString());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
		
		
		
	
	
}
