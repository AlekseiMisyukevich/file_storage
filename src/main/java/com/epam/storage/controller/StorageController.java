package com.epam.storage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.storage.service.StorageService;

@RestController("/storage-handler")
public class StorageController {

	private static final Logger LOG = LoggerFactory.getLogger(StorageController.class);
	
	@Autowired 
	private StorageService service;	
	private String msg;
		
	@DeleteMapping(value = "/delete-file/{relpath}/{filename}")
	public HttpStatus handleDelete (
			@RequestParam(value = "relpath", required = false) String relpath,
			@RequestParam(value = "filename", required = true) String filename) {
		
		try {
			
			if (relpath.isEmpty() && !filename.isEmpty()) {
				service.delete(filename);
				return HttpStatus.OK;
			} else if (!relpath.isEmpty() && !filename.isEmpty()) {
				service.deleteFromDir(relpath, filename);
				return HttpStatus.OK;
			}
			
			return HttpStatus.BAD_REQUEST;
			
		} catch (Exception e) {
			
			msg = String.format("Failed to delete file > ", e.getCause());
			LOG.debug(msg);
			return HttpStatus.BAD_REQUEST;
		
		}
		
	}
	
	@PutMapping(value = "/create-folder/{relpath}")
	public HttpStatus handlePut ( @PathVariable(value = "relpath", required = true) String relpath) {
		
		if (!relpath.isEmpty()) {
			service.createFolder(relpath);
			return HttpStatus.CREATED;
			
		} else {
			return HttpStatus.BAD_REQUEST;
		}
		
	}
	
}
