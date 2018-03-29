package com.epam.storage.dao.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.epam.storage.dao.FileRepository;

@Repository
public class FileRepositoryImpl implements FileRepository {

	private static final Logger LOG = LoggerFactory.getLogger(FileRepositoryImpl.class);
	private FileSystem fs;
	private static final String ROOT_PATH = "/root/Documents/storage";

	public FileRepositoryImpl() {
		this.fs = FileSystems.getDefault();
	}

	@PostConstruct
	private void init() {
		createDir();
	}
		
	public void createDir() {

		final Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwx------");
		final FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(permissions);

		try {

			Path absPath = fs.getPath(ROOT_PATH);
			
			if (!Files.exists(absPath, LinkOption.NOFOLLOW_LINKS)) {
				Files.createDirectory(absPath, attrs);
			}
		}

		catch (IOException | InvalidPathException e) {
			LOG.debug(e.getMessage());
			throw new RuntimeException(e);
		}

	}
	
	@Override
	public void createDir(String relPath) {

		final Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwx------");
		final FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(permissions);

		try {

			Path absPath = fs.getPath(ROOT_PATH, relPath);
			Files.createDirectory(absPath, attrs);

		}

		catch (IOException | InvalidPathException e) {
			LOG.debug(e.getMessage());
			throw new RuntimeException(e);
		}

	}

	@Override
	public void upload(String filename, byte[] data) {

		try {

			final Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwx------");
			final FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(permissions);

			Path gzFile = Files.createFile(fs.getPath(ROOT_PATH, filename), attrs);
			compress(gzFile, data);

		} catch (InvalidPathException e) {

			String msg = String.format("Invalid Path > ", e.getCause().toString());
			LOG.error(msg);
			throw new RuntimeException(e);

		} catch (IOException e) {

			String msg = String.format(" Error occured while saving file - %s", filename);
			LOG.error(msg);
			throw new RuntimeException(e);

		}

	}

	@Override
	public void uploadToFolder(String relPath, String filename, byte[] data) {

		try {

			final Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwx------");
			final FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(permissions);

			Path gzFile = Files.createFile(fs.getPath(ROOT_PATH, relPath, filename), attrs);

			compress(gzFile, data);

		} catch (InvalidPathException e) {

			String msg = String.format(e.getCause().toString());
			LOG.debug(msg);
			throw new RuntimeException(e);

		} catch (IOException e) {

			String msg = String.format(" Error ocured while saving file > ", e.getCause());
			LOG.debug(msg);
			throw new RuntimeException(e);

		}

	}

	@Override
	public Map<String, List<String>> filesInDirectory() {

		try {
			return getDirContent();
		} catch (IOException e) {
			String msg = String.format("Error occured while gathering metadata > ", e.getMessage());
			LOG.debug(msg);
			throw new RuntimeException(e);
		}

	}

	@Override
	public Map<String, List<String>> filesInDirectory(String relativePath) {

		try {
			return getDirContent(Paths.get(ROOT_PATH, relativePath));
		} catch (InvalidPathException | IOException e) {
			LOG.debug(e.getMessage().toString());
			throw new RuntimeException(e);
		}

	}

	@Override
	public Map<String, List<String>> searchByContent(String content) {

		try {

			Path path = fs.getPath(ROOT_PATH);
			return seekMatches(path, content);

		} catch (InvalidPathException e) {
			LOG.debug(e.getCause().toString());
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.debug(e.getCause().toString());
			throw new RuntimeException(e);

		}
	}

	@Override
	public byte[] download(String filename) {

		try {
			return decompress(fs.getPath(ROOT_PATH, filename));
		}

		catch (IOException | InvalidPathException e) {
			LOG.debug(e.getMessage().toString());
			throw new RuntimeException(e);
		}

	}

	@Override
	public byte[] downloadFromFolder(String relpath, String filename) {

		try {
			return decompress(fs.getPath(ROOT_PATH, relpath, filename));
		} catch (InvalidPathException | IOException e) {
			LOG.debug(e.getMessage());
			throw new RuntimeException(e);
		}

	}

	@Override
	public void deleteFile(String fileName) {

		try {
			Files.deleteIfExists(fs.getPath(ROOT_PATH, fileName));
		} catch (IOException e) {
			String msg = String.format("Files cannot be erased > ", e.getCause());
			LOG.debug(msg);
			throw new RuntimeException(msg);
		}

	}

	@Override
	public void deleteFileFromDir(String relPath, String filename) {

		Path path = fs.getPath(ROOT_PATH, relPath, filename);
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			LOG.debug("File cannot be erased > %s", e.getCause().toString());
			throw new RuntimeException(e);
		}

	}

	/**
	 * private methods
	 */

	private Map<String, List<String>> seekMatches(Path start, String content) throws IOException {

		Map<String, List<String>> map = new LinkedHashMap<>();

		Files.walkFileTree(start, null, Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {

				try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ);
						GZIPInputStream gin = new GZIPInputStream(in);
						ByteArrayOutputStream fos = new ByteArrayOutputStream();) {

					byte[] buff = new byte[1024];
					int len;

					while ((len = gin.read(buff)) != -1) {

						String text = new String(buff);

						if (text.contains(content)) {

							String filename = path.getFileName().toString();
							String creationTime = attrs.creationTime().toString();
							map.put(Paths.get(ROOT_PATH).relativize(path).toString(), Arrays.asList(filename, creationTime));
							break;

						}

					}

				}

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {

				try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ);
						GZIPInputStream gin = new GZIPInputStream(in);
						ByteArrayOutputStream fos = new ByteArrayOutputStream();) {

					byte[] buff = new byte[1024];
					int len;

					while ((len = gin.read(buff)) != -1) {

						String text = new String(buff);

						if (text.contains(content)) {

							String filename = path.getFileName().toString();
							String creationTime = attrs.creationTime().toString();
							map.put(Paths.get(ROOT_PATH).relativize(path).toString(), Arrays.asList(filename, creationTime));
							break;
						}

					}

				}

				return FileVisitResult.CONTINUE;
			}
		});

		return map;

	}

	private Map<String, List<String>> getDirContent() throws IOException, InvalidPathException {

		Map<String, List<String>> map = new LinkedHashMap<>();
		Path root = fs.getPath(ROOT_PATH);

		DirectoryStream.Filter<? super Path> filter = f -> Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS)
				|| Files.isRegularFile(f, LinkOption.NOFOLLOW_LINKS);

		DirectoryStream<Path> stream = Files.newDirectoryStream(root, filter);
		BasicFileAttributes attrs = null;

		for (Path file : stream) {

			attrs = Files.readAttributes(file, BasicFileAttributes.class);
			String filename = file.getFileName().toString();
			String creationTime = attrs.creationTime().toString();
			map.put(attrs.fileKey().toString(), Arrays.asList(filename, creationTime));

		}

		return map;

	}

	private Map<String, List<String>> getDirContent(Path dir) throws InvalidPathException, IOException {

		Map<String, List<String>> map = new LinkedHashMap<>();

		DirectoryStream.Filter<? super Path> filter = f -> Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS)
				|| Files.isRegularFile(f, LinkOption.NOFOLLOW_LINKS);
		DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter);
		BasicFileAttributes attrs = null;

		for (Path file : stream) {

			attrs = Files.readAttributes(file, BasicFileAttributes.class);
			String filename = file.getFileName().toString();
			String creationTime = attrs.creationTime().toString();
			map.put(attrs.fileKey().toString(), Arrays.asList(filename, creationTime));

		}

		return map;

	}

	private void compress(Path archive, byte[] data) throws IOException, InvalidPathException {

		try (OutputStream out = Files.newOutputStream(archive, StandardOpenOption.WRITE);
				GZIPOutputStream zip = new GZIPOutputStream(new BufferedOutputStream(out));

		) {
			zip.write(data);
		}

	}

	private byte[] decompress(Path path) throws IOException, InvalidPathException {

		try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ);
				GZIPInputStream gin = new GZIPInputStream(in);
				ByteArrayOutputStream fos = new ByteArrayOutputStream();) {
			byte[] buff = new byte[2048];
			int len;

			while ((len = gin.read(buff)) != -1) {
				fos.write(buff, 0, len);
			}

			return fos.toByteArray();
		}

	}

}
