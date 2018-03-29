package com.epam.storage.controller;

import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.epam.storage.service.StorageService;

@RestController
@RequestMapping("/")
public class DownloadController {
	
private static final Logger LOG = LoggerFactory.getLogger(StorageController.class);
	
	@Autowired
	private StorageService service;
	private String msg;
	
		
	@GetMapping(path = "/root-files", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getAllFiles() {

		try {
			
			Map<String, List <String>> map = service.listFiles();
			HttpHeaders headers = new HttpHeaders();
						
			headers.setContentType(MediaType.ALL);
			headers.setContentLength(map.size());
			
			return new ResponseEntity <Map<String, List<String>> >(map, headers, HttpStatus.OK);
			
		} catch (Exception e) {
			msg = String.format("Failed to retrieve files > %s", e.getCause());
			LOG.debug(msg);
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping(value = "/download-file/{relpath}/{filename}", produces = "application/*")
	@ResponseBody
	public ResponseEntity<byte[]> handleDownload(
			@PathVariable(name = "relpath", required = false) String relpath,
			@PathVariable(name = "filename", required = true) String filename) {

		try {			
			if (relpath.isEmpty() && !filename.isEmpty()) {
				
				final byte[] bytes = service.download(filename);
						
				MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
				HttpHeaders headers = new HttpHeaders();
				String mimeType = mimeTypesMap.getContentType(filename);
			
				if (mimeType.equals(null)) {	
					headers.setContentType(MediaType.TEXT_PLAIN);				
				} else {
					headers.setContentType(MediaType.parseMediaType(mimeType));
				}
						
				headers.add("content-disposition", "attachment; filename = " + filename);
				headers.setContentLength(bytes.length);
			
				return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
				
			} else if (!relpath.isEmpty() && !filename.isEmpty()) {
				
				final byte[] bytes = service.downloadFromDir(relpath, filename);
				
				MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
				HttpHeaders headers = new HttpHeaders();
				String mimeType = mimeTypesMap.getContentType(filename);
			
				if (mimeType.equals(null)) {	
					headers.setContentType(MediaType.TEXT_PLAIN);				
				} else {
					headers.setContentType(MediaType.parseMediaType(mimeType));
				}
						
				headers.add("content-disposition", "attachment; filename = " + filename);
				headers.setContentLength(bytes.length);
			
				return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);			
			}	
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} 
		
		catch (Exception e) {
			msg = String.format("Failed to download file %s > ", e.getMessage());
			LOG.debug(msg);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}	
	
	@GetMapping(value = "/search-by-content", produces = "application/json")
	@ResponseBody
	public ResponseEntity < Map <String, List <String>> > handleSearch ( @RequestParam (value = "file", required = true) String query ) {
		
		if (!query.isEmpty()) {
			
			Map<String, List <String>> map = service.searchFile(query);
			HttpHeaders headers = new HttpHeaders();
						
			headers.setContentType(MediaType.ALL);
			headers.setContentLength(map.size());
			
			return new ResponseEntity <Map<String, List<String>> >(map, headers, HttpStatus.OK);			
			
		} else {
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		}	
	}
}
