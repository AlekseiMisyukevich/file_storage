package com.epam.storage.service;

import java.util.List;
import java.util.Map;

public interface StorageService {

	void save (String filename, byte[] data);
	
	void saveTo(String relPath, String filename, byte[] data);
	
	Map<String, List <String>> searchFile (String content);
	
	byte[] download(String fileName);
	
	byte[] downloadFromDir(String path, String filename);
	
	Map<String, List <String>> listFiles();
	
	Map<String, List <String>> listFilesInDir(String path);
	
	void createFolder (String relpath);
	
	void delete(String fileName);
	
	void deleteFromDir(String relPath, String filename);
	
}
