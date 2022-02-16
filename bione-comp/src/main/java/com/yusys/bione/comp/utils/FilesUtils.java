package com.yusys.bione.comp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理工具类
 * 
 */
public class FilesUtils {

	public static final int FLUSH_BUFFER_SIZE = 10240; // buffer size=10K

	private static final Map<String, byte[]> mFileTypes = new HashMap<String, byte[]>(24);
	
	static {
		mFileTypes.put("avi", new byte[]{0x41, 0x56, 0x49, 0x20});
		mFileTypes.put("bmp", new byte[]{0x42, 0x4D});
		mFileTypes.put("doc", new byte[]{(byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0});
		mFileTypes.put("docx", new byte[]{0x50, 0x4B, 0x03, 0x04});
		mFileTypes.put("gif", new byte[]{0x47, 0x49, 0x46, 0x38});
		mFileTypes.put("gz", new byte[]{0x1F, (byte)0x8B, (byte)0x08});
		mFileTypes.put("jpg", new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF});
		mFileTypes.put("pdf", new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E});
		mFileTypes.put("png", new byte[]{(byte)0x89, 0x50, 0x4E, 0x47});
		mFileTypes.put("psd", new byte[]{0x38, 0x42, 0x50, 0x53});
		mFileTypes.put("rar", new byte[]{0x52, 0x61, 0x72, 0x21});
		mFileTypes.put("rm", new byte[]{0x2E, 0x52, 0x4D, 0x46});
		mFileTypes.put("tif", new byte[]{0x49, 0x49, 0x2A, 0x00});
		mFileTypes.put("xls", new byte[]{(byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0});//excel2003版本文件
		mFileTypes.put("xlsx", new byte[]{0x50, 0x4B, 0x03, 0x04});//excel2007以上版本文件
		mFileTypes.put("zip", new byte[]{0x50, 0x4B, 0x03, 0x04});
	}

	/**
	 * 获取文件类型描述
	 */
	public static String getFileType(java.io.File file) {
		if (file == null) {
			return "";
		}
		if (file.isDirectory()) {
			return "文件夹";
		}
		String ext = getFileExt(file.getName());
		return FileType.getFileType(ext);
	}

	/**
	 * 取文件扩展名
	 */
	public static String getFileExt(String clientFileName) {
		if (clientFileName == null || clientFileName.equals("")) {
			return "";
		}
		String[] arrFile = StringUtils.split(clientFileName, '.');
		return arrFile[arrFile.length - 1];
	}

	/**
	 * 获取短文件名
	 */
	public static String getShortFileName(String fileName) {
		if (fileName == null || fileName.equals("")) {
			return "";
		}
		int index = indexOfLastSeparator(fileName);
		return fileName.substring(index + 1);
	}

	/**
	 * 获取简单文件名, 不包括扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getSimpleFileNameExceptExt(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return "";
		}
		fileName = getShortFileName(fileName);
		return fileName.substring(0, fileName.lastIndexOf("\\x2E"));
	}

	public static int indexOfLastSeparator(String filename) {
		if (filename == null) {
			return -1;
		} else {
			int lastUnixPos = filename.lastIndexOf('/');
			int lastWindowsPos = filename.lastIndexOf('\\');
			return Math.max(lastUnixPos, lastWindowsPos);
		}
	}

	/**
	 * 获取文件的大小
	 */
	public static String getFileSize(long filesize) {
		double size = filesize * 1.0d;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(0);

		double sizeGB = size / 1024 / 1024 / 1024;
		if (sizeGB >= 1) {
			return nf.format(sizeGB) + " GB";
		}

		double sizeMB = size / 1024 / 1024;
		if (sizeMB >= 1) {
			return nf.format(sizeMB) + " MB";
		}

		double sizeKB = size / 1024;
		if (sizeKB >= 1) {
			return nf.format(sizeKB) + " KB";
		}
		return filesize + " bytes";
	}

	/**
	 * 获取文件的大小
	 */
	public static String getFileSize(File file) {
		return getFileSize(file.length());
	}

	/**
	 * 拷贝文件
	 * 
	 * @param srcFile
	 *            源文件
	 * @param destFile
	 *            目标文件
	 * @throws IOException
	 *             IO异常
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException {
		copyFile(srcFile, destFile, true);
	}

	/**
	 * 拷贝文件
	 * 
	 * @param srcFile
	 *            源文件
	 * @param destFile
	 *            目标文件
	 * @param preserveFileDate
	 *            是否保持时间戳
	 * @throws IOException
	 *             IO异常
	 */
	public static void copyFile(File srcFile, File destFile,
			boolean preserveFileDate) throws IOException {
		if (srcFile == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destFile == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (!srcFile.exists()) {
			throw new IOException("Source '" + srcFile + "' does not exist");
		}
		if (srcFile.isDirectory()) {
			throw new IOException("Source '" + srcFile
					+ "' exists but is a directory");
		}
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
			throw new IOException("Source '" + srcFile + "' and destination '"
					+ destFile + "' are the same");
		}
		if (destFile.getParentFile() != null
				&& !destFile.getParentFile().exists()
				&& !destFile.getParentFile().mkdirs()) {
			throw new IOException("Destination '" + destFile
					+ "' directory cannot be created");
		}
		if (destFile.exists() && !destFile.canWrite()) {
			throw new IOException("Destination '" + destFile
					+ "' exists but is read-only");
		} else {
			doCopyFile(srcFile, destFile, preserveFileDate);
		}
	}

	// 拷贝文件
	private static void doCopyFile(File srcFile, File destFile,
			boolean preserveFileDate) throws IOException {
		if (destFile.exists() && destFile.isDirectory()) {
			throw new IOException("Destination '" + destFile
					+ "' exists but is a directory");
		}
		FileInputStream input = new FileInputStream(srcFile);
		try {
			FileOutputStream output = new FileOutputStream(destFile);
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
		} finally {
			IOUtils.closeQuietly(input);
		}
		if (srcFile.length() != destFile.length()) {
			throw new IOException("Failed to copy full contents from '"
					+ srcFile + "' to '" + destFile + "'");
		}
		if (preserveFileDate) {
			destFile.setLastModified(srcFile.lastModified());
		}
	}

	/**
	 * 默认方式清理指定目录下的文件：文件时间超过24小时者删除
	 * 
	 * @param directory
	 */
	public static void defaultClean(File directory) {
		if (!directory.exists()) {
			return;
		}
		long limit = 24 * 60 * 60 * 1000L; // 最大时限24小时
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			long time = System.currentTimeMillis() - f.lastModified() - limit;
			if (time >= 0) {
				if (f.isDirectory()) {
					defaultClean(f);
				}
				f.delete();
			}
		}
	}

	/**
	 * 删除目录下的超过时限的文件（不含目录）,并且文件的后缀为fileExt中的一个
	 * 
	 * @param directory
	 * @param andSubFolder
	 *            是否删除子目录下的文件
	 * @param fileExt
	 *            如"txt,exe,xls"
	 */
	public static void cleanDir(File directory, boolean andSubFolder,
			String fileExt) {
		long limit = 24 * 60 * 60 * 1000L; // 最大时限24小时
		String fileext = fileExt.toLowerCase();
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			long time = System.currentTimeMillis() - f.lastModified() - limit;
			if (time >= 0) {
				if (f.isDirectory() && andSubFolder) {
					cleanDir(f, andSubFolder, fileext);
				}
				String ext = getFileExt(f.getName()).toLowerCase();
				if (f.isFile() && !"".equals(ext) && fileext.indexOf(ext) >= 0) {
					f.delete();
				}
			}
		}
	}

	/**
	 * 判断目录及其子目录下是否有文件
	 * 
	 * @param directory
	 */
	public static boolean containFiles(File directory) {
		if (directory == null || !directory.exists()) {
			return false;
		}
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				return true;
			}
			if (f.isDirectory()) {
				return containFiles(f);
			}
		}
		return false;
	}

	/**
	 * 删除目录下的所有文件或目录
	 * 
	 * @param directory
	 *            需要删除的目录
	 */
	public static void deleteFiles(File directory) {
		File[] files = directory.listFiles();
		for (int i = 0; files != null && i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				deleteFiles(f);
			}
			f.delete();
		}
		directory.delete();
	}

	/**
	 * 删除目录下的所有文件
	 * 
	 * @param directory
	 *            需要删除文件所属的目录
	 */
	public static void cleanFiles(File directory) {
		File[] files = directory.listFiles();
		for (int i = 0; files != null && i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				deleteFiles(f);
			}
			f.delete();
		}
	}

	/**
	 * 删除绝对路径文件
	 * 
	 * @param directory
	 *            需要删除文件所属的目录
	 */
	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
		}
	}

	/**
	 * 删除目录下所有文件（不含目录）,并且文件的后缀为fileExt中的一个
	 * 
	 * @param directory
	 *            需要删除的目录
	 * @param alsoSubFolder
	 *            是否删除子目录下的文件
	 * @param fileExt
	 *            文件后缀，用逗号分割，如"txt,exe,xls"
	 */
	public static void deleteFiles(File directory, boolean alsoSubFolder,
			String fileExt) {
		String fileext = fileExt.toLowerCase();
		File[] files = directory.listFiles();
		for (int i = 0; files != null && i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && alsoSubFolder) {
				deleteFiles(f, alsoSubFolder, fileext);
			}
			String ext = getFileExt(f.getName()).toLowerCase();
			if (f.isFile() && !"".equals(ext) && fileext.indexOf(ext) >= 0) {
				f.delete();
			}
		}
	}

	/**
	 * 创建一个指定编码的文件Writer
	 * 
	 * @param out
	 *            输出
	 * @param enc
	 *            编码
	 * @return Writer
	 * @throws UnsupportedEncodingException
	 *             不支持的编码
	 */
	public static Writer makeWriter(OutputStream out, String enc)
			throws UnsupportedEncodingException {
		if ("UTF-8".equals(enc)) {
			enc = "UTF8";
		}
		Writer writer = new BufferedWriter(new OutputStreamWriter(
				new BufferedOutputStream(out), enc));
		return writer;
	}

	/**
	 * 创建一个指定编码的文件Reader
	 * 
	 * @param in
	 *            输入
	 * @param enc
	 *            编码
	 * @return Reader
	 * @throws UnsupportedEncodingException
	 *             不支持的编码
	 */
	public static Reader makeReader(InputStream in, String enc)
			throws UnsupportedEncodingException {
		if ("UTF-8".equals(enc)) {
			enc = "UTF8";
		}
		Reader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(in), enc));
		return reader;
	}

	/**
	 * 创建整个目录
	 * 
	 * @param path
	 *            目录路径
	 * @return 目录存在否
	 */
	public static boolean createDir(String path) {
		if (FilepathValidateUtils.validateFilepath(path)) {
			File file = new File(path);
			return file.exists() ? true : file.mkdirs();
		}
		return false;
	}

	/**
	 * 
	 * @param inputFileName
	 *            输入一个文件夹
	 * @param zipFileName
	 *            输出一个压缩文件夹，打包后文件名字
	 * @throws Exception
	 */
	public static void zip(String inputFileName, String zipFileName) throws Exception {
		if (FilepathValidateUtils.validateFilepath(inputFileName)) {
			zip(zipFileName, new File(inputFileName));
		}
	}

	private static void zip(String zipFileName, File inputFile) throws Exception {
		if (FilepathValidateUtils.validateFilepath(zipFileName)) {
			ZipOutputStream out = null;
			try {
				out = new ZipOutputStream(new FileOutputStream(zipFileName));
				String base = "";
				if (inputFile.isFile()) {
					base = inputFile.getName();
				}
				zip(out, inputFile, base, new File(zipFileName));
			} finally {
				IOUtils.closeQuietly(out);
			}
		}
	}

	public static void zip(ZipOutputStream out, File f, String base, File zipFile) throws IOException {
		if (f.isDirectory()) { // 判断是否为目录
			File[] fl = f.listFiles();
			out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName(), zipFile);
			}
		} else { // 压缩目录中的所有文件
			if (f.equals(zipFile)) {
				return;
			}
			out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
			InputStream in = null;
			try {
				in = new FileInputStream(f);
				IOUtils.copy(in, out);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	}

	// 解压指定zip文件
	public static List<File> unzip(File inputFile, String outputPath) throws IOException {
		ZipFile zipFile = null;
		List<File> files = new ArrayList<File>();
		if (FilepathValidateUtils.validateFilepath(outputPath)) {
			try {
				zipFile = new ZipFile(inputFile, "GB18030");
				for (Enumeration<?> entries = zipFile.getEntries(); entries.hasMoreElements();) {
					
					ZipEntry entry = (ZipEntry) entries.nextElement();
					File outputPathDir = new File(outputPath);
					if (! outputPathDir.exists()) {
						outputPathDir.mkdirs();
					}
					String entryName = entry.getName();
					if(entryName.contains("/")){
						entryName = entryName.substring(entry.getName().lastIndexOf("/")+1);
					}
					File file = new File(outputPath, entryName);
					
					if (entry.isDirectory()) {
						file.mkdirs();
					} else {
						InputStream in = null;
						OutputStream out = null;
						try {
							in = zipFile.getInputStream(entry);
							out = new FileOutputStream(file);
							IOUtils.copy(in, out);
							files.add(file);
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							IOUtils.closeQuietly(in);
							IOUtils.closeQuietly(out);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (zipFile != null) {
					zipFile.close();
				}
			}
		}
		return files;
	}

	/**
	 * 检查文件内容是否符合文件扩展名所标志的文件类型
	 */
	public static boolean isRealTypeFile(File f) {
		if (f == null) {
			return false;
		}
		int pos = f.getName().lastIndexOf('.');
		if (pos < 0) {
			return false;
		}
		String extName = f.getName().substring(pos + 1).toLowerCase();
		byte[] tags = mFileTypes.get(extName);
		if (tags == null) {
			return true;
		}
		InputStream in = null;
		try {
			in = new FileInputStream(f);
			byte[] b = new byte[tags.length];
			int len = in.read(b);
			if (len < b.length) {
				return false;
			}
			return ArrayUtils.isEquals(b, tags);
		} catch (IOException e) {
		} finally {
			IOUtils.closeQuietly(in);
		}
		return false;
	}
	
    public static void deleteFileAndParent(File file){
    	File [] cfiles = file.getParentFile().listFiles();
    	for(int i = 0; i < cfiles.length; i++){
    		cfiles[i].delete();
    	}
    	file.getParentFile().delete();
    }
    
    /**
     * 文件写入工具类
     * 2020 lcy 【后台管理】不正确的资源释放 代码优化
     * @param filePathName
     * @param file1
     */
    public static void writeMultipartFile(String filePathName, MultipartFile file) {
        FileOutputStream out = null;
		try {
			out = new FileOutputStream(filePathName);
			out.write(file.getBytes());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
    }
    
    /**
     * 检查 上传下载文件 物理路径
     * @param filePathName
     * @param file
     * @return
     */
    public static boolean checkFilePathAndName(String filePathName) {
    	boolean isPass = true;
    	
    	return isPass;
    }
}
