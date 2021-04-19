package org.iotcity.iot.framework.core.util.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Properties;

/**
 * File I/O util
 * @author Ardon
 */
public final class FileHelper {

	/**
	 * The separator in current system path (e.g. "\")
	 */
	public static final String Separator = File.separator;
	/**
	 * The application root path (no ending separator)
	 */
	private static String root = null;
	/**
	 * The application root path (with ending separator)
	 */
	private static String rootWithSeparator = null;

	// --------------------------- Public methods for path or directory ----------------------------

	/**
	 * Gets application root directory (JVM runtime directory)
	 * @param endsWidthSeparator Whether to attach the end separator of the directory
	 * @return String Application root directory
	 */
	public static String getRoot(boolean endsWidthSeparator) {
		if (root == null) {
			String path = new File("").getAbsolutePath();
			if (path.endsWith(File.separator)) {
				if (path.length() > 1) {
					path = path.substring(0, path.length() - 1);
				} else {
					path = "";
				}
			}
			root = path;
			rootWithSeparator = path + File.separator;
		}
		return endsWidthSeparator ? rootWithSeparator : root;
	}

	/**
	 * Modify the directory to the local directory
	 * @param dir The directory to be modified
	 * @param withEndedSeparator Whether to attach the end separator of the directory
	 * @return String Local directory result
	 */
	public static String toLocalDirectory(String dir, boolean withEndedSeparator) {
		if (dir == null || dir.length() == 0) return getRoot(withEndedSeparator);
		if (dir.indexOf(":\\") == 1) {
			return replaceSeparator(dir, Separator, withEndedSeparator);
		} else {
			if (dir.startsWith("/")) {
				return replaceSeparator(dir, Separator, withEndedSeparator);
			} else {
				return replaceSeparator(getRoot(true) + dir, Separator, withEndedSeparator);
			}
		}
	}

	/**
	 * Replaces the path with the specified directory separator
	 * @param path File name or directory
	 * @param separator Directory separator for replacement
	 * @param withEndedSeparator Whether to attach the end separator of the directory
	 * @return String Result path, has replaced by new separator
	 */
	public static String replaceSeparator(String path, String separator, boolean withEndedSeparator) {
		if (path == null || path.length() == 0) return "";
		String ret;
		if ("\\".equals(separator)) {
			ret = path.replaceAll("/", "\\\\");
		} else if ("/".equals(separator)) {
			ret = path.replaceAll("\\\\", "/");
		} else {
			ret = path;
		}
		if (withEndedSeparator) {
			if (!ret.endsWith(separator)) {
				ret += separator;
			}
		} else {
			if (ret.endsWith(separator)) {
				ret = ret.substring(0, ret.length() - 1);
			}
		}
		return ret;
	}

	/**
	 * Determine if the file path exists
	 * @param path file path or directory
	 * @return boolean Whether the path exists
	 */
	public static boolean exists(String path) {
		try {
			File f = new File(path);
			return f.exists();
		} catch (Exception e) {
			System.err.println("File exists error: " + path);
			e.printStackTrace();
			return false;
		}
	}

	// --------------------------- Public methods for file ----------------------------

	/**
	 * Create a folder
	 * @param dir Folder directory
	 * @return boolean Whether created successfully
	 */
	public static boolean createFolder(String dir) {
		if (dir == null || dir.length() == 0) return false;
		try {
			File f = new File(dir);
			if (!f.exists()) {
				return f.mkdirs();
			}
			return true;
		} catch (Exception e) {
			System.err.println("Create folder error: " + dir);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Create a new file or write new content to an existing file
	 * @param filePathAndName Absolute path and file name of text file
	 * @param fileContent Text content
	 * @param encoding Text encoding
	 * @return boolean Whether written successfully
	 */
	public static boolean createFile(String filePathAndName, String fileContent, String encoding) {
		PrintWriter myFile = null;
		try {
			File f = new File(filePathAndName);
			if (!f.exists()) {
				f.createNewFile();
			}
			myFile = new PrintWriter(f, encoding);
			myFile.print(fileContent);
			return true;
		} catch (Exception e) {
			System.err.println("Create/write file error: " + filePathAndName);
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (myFile != null) {
					myFile.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Read text file content
	 * @param filePathAndName Absolute path and file name of text file
	 * @param encoding Text encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically)
	 * @param fromPackage Whether read file from package
	 * @return String Text content
	 * @throws IOException If there is a read error, an exception is thrown
	 */
	public static String readText(String filePathAndName, String encoding, boolean fromPackage) throws IOException {
		StringBuilder sb = new StringBuilder();
		String st = "";
		InputStream fs = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			if (fromPackage) {
				fs = FileHelper.class.getClassLoader().getResourceAsStream(filePathAndName);
			} else {
				fs = new FileInputStream(replaceSeparator(filePathAndName, Separator, false));
			}
			isr = getUnicodeReader(fs, encoding);
			br = new BufferedReader(isr);
			String data = "";
			while ((data = br.readLine()) != null) {
				sb.append(data + "\r\n");
			}
			st = sb.toString();
		} catch (IOException es) {
			System.err.println("Read text file error: " + filePathAndName);
			es.printStackTrace();
			st = "";
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
			}
			try {
				if (isr != null) {
					isr.close();
				}
			} catch (Exception e) {
			}
			try {
				if (fs != null) {
					fs.close();
				}
			} catch (Exception e) {
			}
		}
		return st;
	}

	/**
	 * Read property profile
	 * @param props Properties object waiting to be written
	 * @param filePathAndName Absolute path and file name of text file
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether read file from package
	 * @return boolean Whether read successfully
	 */
	public static boolean loadProperties(Properties props, String filePathAndName, String encoding, boolean fromPackage) {
		if (!fromPackage && !exists(filePathAndName)) return false;
		InputStream fis = null;
		BufferedInputStream bis = null;
		InputStreamReader isr = null;
		// Print message
		System.out.println("[" + ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS") + "] Load properties file: " + filePathAndName);
		try {
			if (fromPackage) {
				fis = FileHelper.class.getClassLoader().getResourceAsStream(filePathAndName);
			} else {
				fis = new FileInputStream(replaceSeparator(filePathAndName, Separator, false));
			}
			isr = getUnicodeReader(fis, encoding);
			props.load(isr);
			return true;
		} catch (Exception e) {
			System.err.println("Load properties file error: " + filePathAndName);
			e.printStackTrace();
			return false;
		} finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Gets the input stream reader of the specified encoding
	 * @param is Input stream object
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @return InputStreamReader Specifies the encoding input stream reader
	 * @throws IOException
	 */
	public static InputStreamReader getUnicodeReader(InputStream is, String encoding) throws IOException {
		final int BOM_SIZE = 4;
		byte bom[] = new byte[BOM_SIZE];
		int n, unread;
		if (is == null) return null;
		PushbackInputStream internalIn = new PushbackInputStream(is, BOM_SIZE);
		n = internalIn.read(bom, 0, bom.length);
		if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = "UTF-32BE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = "UTF-32LE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
			encoding = "UTF-8";
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = "UTF-16BE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = "UTF-16LE";
			unread = n - 2;
		} else {
			if (encoding == null || encoding.equals("")) {
				encoding = Charset.defaultCharset().name();
			}
			unread = n;
		}
		if (unread > 0) internalIn.unread(bom, (n - unread), unread);
		return (new InputStreamReader(internalIn, encoding));
	}

	/**
	 * Delete a folder (all files in the folder will be deleted)
	 * @param dir Folder directory
	 * @param keepDir Whether keep current folder, only delete the files under it
	 * @return boolean Whether the deletion was successful
	 */
	public static boolean deleteFolder(String dir, boolean keepDir) {
		if (dir == null || dir.length() == 0) return false;
		File file = new File(dir);
		if (!file.exists() || !file.isDirectory()) {
			return false;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (dir.endsWith(Separator)) {
				temp = new File(dir + tempList[i]);
			} else {
				temp = new File(dir + Separator + tempList[i]);
			}
			if (temp.isFile()) {
				if (!temp.delete()) return false;
			}
			if (temp.isDirectory()) {
				if (!deleteFolder(dir + Separator + tempList[i], false)) return false;
				if (!deleteFile(dir + Separator + tempList[i])) return false;
			}
		}
		return keepDir ? true : file.delete();
	}

	/**
	 * Delete a file from OS
	 * @param filePathAndName Absolute path and file name of the file
	 * @return boolean Whether the deletion was successful
	 */
	public static boolean deleteFile(String filePathAndName) {
		try {
			File f = new File(filePathAndName);
			return f.delete();
		} catch (Exception e) {
			System.err.println("Delete file error: " + filePathAndName);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Copy a folder and all files from source directory to target directory
	 * @param fromDir Absolute path of source folder
	 * @param toDir Absolute path of target folder
	 * @return boolean Whether copy successfully
	 */
	public static boolean copyFolder(String fromDir, String toDir) {
		if (fromDir == null || fromDir.length() == 0 || toDir == null || toDir.length() == 0) return false;
		if (!fromDir.endsWith(Separator)) fromDir = fromDir + Separator;
		if (!toDir.endsWith(Separator)) toDir = toDir + Separator;
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			new File(toDir).mkdirs();
			File from = new File(fromDir);
			String[] files = from.list();
			File copy = null;
			for (int i = 0; i < files.length; i++) {
				copy = new File(fromDir + files[i]);
				if (copy.isFile()) {
					input = new FileInputStream(copy);
					output = new FileOutputStream(toDir + copy.getName().toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					input.close();
					output.close();
				} else if (copy.isDirectory()) {
					if (!copyFolder(fromDir + files[i], toDir + files[i])) return false;
				}
			}
		} catch (Exception e) {
			System.err.println("Copy folder from \"" + fromDir + "\" to \"" + toDir + "\" error!");
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception ex) {
			}
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception ex) {
			}
		}
		return true;
	}

	/**
	 * Copy a file to another directory
	 * @param fromFile Absolute path and file name of source file
	 * @param toFile Absolute path and file name of target file
	 * @return boolean Whether copy successfully
	 */
	public static boolean copyFile(String fromFile, String toFile) {
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int byteread = 0;
			File from = new File(fromFile);
			if (from.exists()) {
				inStream = new FileInputStream(from);
				fs = new FileOutputStream(toFile);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				return true;
			}
		} catch (Exception e) {
			System.err.println("Copy file from \"" + fromFile + "\" to \"" + toFile + "\" error!");
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (Exception ex) {
			}
			try {
				if (fs != null) {
					fs.close();
				}
			} catch (Exception ex) {
			}
		}
		return false;
	}

	/**
	 * Gets MD5 string of a file (upper case, returns null when failed)
	 * @param filePathAndName Absolute path and file name of the file
	 * @return String MD5 result
	 */
	public static String getFileMD5(String filePathAndName) {
		String value = null;
		FileInputStream in = null;
		FileChannel channel = null;
		MappedByteBuffer byteBuffer = null;
		try {
			File f = new File(filePathAndName);
			in = new FileInputStream(f);
			channel = in.getChannel();
			byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, f.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16).toUpperCase();
		} catch (Exception e) {
			System.err.println("Get file MD5 error: " + filePathAndName);
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (channel != null) {
				try {
					if (byteBuffer != null) {
						Class<?> fcClass = channel.getClass();
						Method m = fcClass.getDeclaredMethod("unmap", MappedByteBuffer.class);
						m.setAccessible(true);
						m.invoke(fcClass, byteBuffer);
						byteBuffer.clear();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						channel.close();
					} catch (Exception e2) {
					}
				}
			}
		}
		return value;
	}

}
