package com.epam.storage.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository {
			
	public void createDir (String relPath);
	
	public void upload (String filename, byte[] data);
	
	public void uploadToFolder (String relPath, String filename, byte[] data);
	
	public Map<String, List<String>> filesInDirectory();
		
	public Map<String, List<String>> filesInDirectory (String relpath);
	
	public Map<String, List<String>> searchByContent (String content);
		
	byte[] download (String fileName);
	
	byte[] downloadFromFolder (String relpath, String filename);
		
	void deleteFile (String fileName);
	
	void deleteFileFromDir (String relPath, String filename);
	
}
