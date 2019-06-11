package com.innovate.filseserver.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.innovate.filseserver.configuration.FtpProperties;
import com.innovate.filseserver.model.UploadFile;

/**
 * Ftp连接断开工具类
 * @author IvanHsu
 */
@Component
@EnableConfigurationProperties({ FtpProperties.class })
public class FtpUploadUtils {
	private static Logger logger = LoggerFactory.getLogger(FtpUploadUtils.class);

	private static FTPClient ftpClient = new FTPClient();
	/**
	 * 属性配置文件的加载
	 */
	private static FtpProperties properties;

	@SuppressWarnings("static-access")
	public FtpUploadUtils(FtpProperties properties) {
		this.properties = properties;
	}

	private static class SingletonClassInstance {
		private static final FtpUploadUtils instance = new FtpUploadUtils(properties);
	}

	public static FtpUploadUtils getInstance() {
		return SingletonClassInstance.instance;
	}

	/**
	 * 保存文件
	 * 
	 * @param filename
	 * @param input
	 * @return
	 */
	public boolean storeFile(String filename, String folder, MultipartFile fileItem) {
		synchronized (this) {
			boolean result = false;
			try {
				if (connectServer()) {
					// 如果不存在则创建目录
					ftpClient.sendCommand("XMKD " + folder + "\r\n");
					// 转移工作目录至指定目录下
					if (ftpClient.changeWorkingDirectory(folder)) {
						ftpClient.enterLocalPassiveMode();
						ftpClient.setBufferSize(properties.getBuffersize());
						ftpClient.setControlEncoding(properties.getEncoding());
						ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
						result = ftpClient.storeFile(new String(filename.getBytes("UTF-8"), "ISO-8859-1"),
								fileItem.getInputStream());
						logger.info("文件 {} 上传服务器，返回结果：{}", filename, result);
					}
				}
			} catch (IOException e) {
				logger.error("文件 {} 上传服务器异常：{}", filename, e.getMessage());
			} finally {
				if (ftpClient.isConnected()) {
					disConnect();
				}
			}
			return result;
		}
	}

	/**
	 * 文件下载
	 * 
	 * @param filename
	 * @param filepath
	 * @param os
	 * @return
	 */
	public boolean downloadFile(String filename, String folder, OutputStream os) {
		boolean result = false;
		try {
			if (connectServer()) {
				ftpClient.enterLocalPassiveMode();
				if (ftpClient.changeWorkingDirectory(folder)) {
					FTPFile[] ftpFiles = ftpClient.listFiles();
					for (FTPFile file : ftpFiles) {
						if (filename.equalsIgnoreCase(file.getName())) {
							ftpClient.retrieveFile(filename, os);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("下载文件失败" + e.getMessage());
			e.printStackTrace();
		} finally {
			disConnect();
			try {
				os.close();
			} catch (IOException e) {
				logger.error("下载文件失败" + e.getMessage());
			}
		}
		return result;
	}

	/**
	 * 下载压缩文件ZIP
	 * 
	 * @param fileList
	 * @param response
	 * @throws Exception
	 */
	public File downloadCompressFiles(List<UploadFile> fileList, HttpServletResponse response) throws Exception {
		if (CollectionUtils.isNotEmpty(fileList)) {
			byte[] buf = new byte[1024];
			// 创建临时文件
			File zipFile = new File("zip_temp_" + UUID.randomUUID());
			ZipOutputStream zipOS = new ZipOutputStream(new FileOutputStream(zipFile));
			if (connectServer()) {
				for (int i = 0; i < fileList.size(); i++) {
					ZipEntry entry = new ZipEntry(fileList.get(i).getName());
					zipOS.putNextEntry(entry);
					InputStream bis = getInputStream(fileList.get(i));
					if (bis != null) {
						int readLen = -1;
						while ((readLen = bis.read(buf, 0, 1024)) != -1) {
							zipOS.write(buf, 0, readLen);
						}
						zipOS.closeEntry();
						// 调用ftp.retrieveFileStream这个接口后，一定要手动close掉返回的InputStream，
						// 然后再调用completePendingCommand方法,若不是按照这个顺序，则会导致后面对FTPClient的操作都失败
						bis.close();
						ftpClient.completePendingCommand();
					}
				}
			}
			zipOS.close();
			disConnect();
			return zipFile;
		}
		return null;
	}

	/**
	 * 删除文件
	 * 
	 * @param pathname
	 *            FTP服务器保存目录
	 * @param filename
	 *            要删除的文件名称
	 * @return
	 */
	public boolean deleteFile(String folder, String filename) {
		boolean flag = false;
		try {
			if (connectServer()) {
				if (ftpClient.changeWorkingDirectory(folder)) {
					flag = ftpClient.deleteFile(filename);
					logger.info("{} 文件执行了删除操作，结果为：,", filename, flag);
				}
			}
		} catch (Exception e) {
			logger.info("{} 该文件删除失败!", filename, e);
		} finally {
			disConnect();
		}
		return flag;
	}

	/**
	 * 获取文件流
	 * 
	 * @param uploadFile
	 * @return
	 */
	private InputStream getInputStream(UploadFile uploadFile) {
		InputStream is = null;
		try {
			if (connectServer()) {
				ftpClient.enterLocalPassiveMode();
				ftpClient.changeWorkingDirectory(uploadFile.getPath());
				FTPFile[] ftpFiles = ftpClient.listFiles();
				for (FTPFile file : ftpFiles) {
					if (uploadFile.getName().equalsIgnoreCase(file.getName())) {
						is = ftpClient.retrieveFileStream(file.getName());
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.info("下载文件失败" + e.getMessage());
		} finally {
			disConnect();
		}
		return is;
	}

	/**
	 * 文件的路径
	 * 
	 * @param filepath
	 * @return
	 */
	public String getStorePath() {
		/*
		 * 按照时间规则 常见目录路径
		 */
		String path = DateUtils.formatDate(new Date(), new StringBuilder().append("yyyy").append(File.separator)
				.append("MM").append(File.separator).append("dd").toString());
		return properties.getBasepath() + File.separator + path;
	}

	/**
	 * 连接服务器,并切换到对应的操作目录下
	 */
	private boolean connectServer() {
		if (ftpClient.isConnected()) {
			return true;
		}
		try {
			ftpClient.connect(properties.getServer(), Integer.parseInt(properties.getPort()));
			return ftpClient.login(properties.getUsername(), properties.getPassword());
		} catch (Exception e) {
			logger.error("FTP服务器[{}]登录异常：{}", properties.getServer(), e);
		}
		return false;
	}

	/**
	 * 断开服务器
	 */
	private void disConnect() {
		try {
			if (ftpClient != null) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("FTP 断开连接异常..", e);
		}
	}

}
