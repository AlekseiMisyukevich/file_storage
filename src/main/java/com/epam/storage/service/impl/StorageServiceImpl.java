package com.epam.storage.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.storage.dao.FileRepository;
import com.epam.storage.service.StorageService;

@Service
public class StorageServiceImpl implements StorageService {
	
	@Autowired
	private FileRepository repo;
		
	public StorageServiceImpl() {}
	
	@Override
	public void save(String filename, byte[] data) {
			
		repo.upload(filename, data);		
	}

	@Override
	public void saveTo(String relPath, String filename, byte[] data) {
		repo.uploadToFolder(relPath, filename, data);
	}

	@Override
	public byte[] download(String fileName) {
		return repo.download(fileName);
	}

	@Override
	public byte[] downloadFromDir(String relpath, String filename) {
		return repo.downloadFromFolder(relpath, filename);
	}

	@Override
	public Map<String, List <String>> listFiles() {
		return repo.filesInDirectory();
	}

	@Override
	public Map<String, List <String>> listFilesInDir(String path) {
		return repo.filesInDirectory(path);
	}

	@Override
	public void delete(String fileName) {
		repo.deleteFile(fileName);		
	}

	@Override
	public void deleteFromDir(String relPath, String filename) {
		repo.deleteFile(relPath);
	}

	@Override
	public Map<String, List<String>> searchFile(String content) {
		return repo.searchByContent(content);
	}

	@Override
	public void createFolder(String relpath) {
				
	}

}
