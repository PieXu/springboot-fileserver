package com.innovate.filseserver.configuration;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.innovate.filseserver.service.IFileProcessService;
import com.innovate.filseserver.service.impl.FastDFSFileProcessServiceImpl;
import com.innovate.filseserver.service.impl.FtpFileProcessServiceImpl;
import com.innovate.filseserver.service.impl.HttpFileProcessServiceImpl;

/**
 * 配置文件不同的配置返回不同的调用实例
 * @author IvanHsu
 */
@Configuration
@Component
public class FileUploadConfiguration {
	// 日誌
	private static Logger logger = LoggerFactory.getLogger(FileUploadConfiguration.class);

	@Value("${upload.fileserver.way}")
	private String way;

	@Bean
	public IFileProcessService initFileService() {
		logger.info("File Server Inital...");
		IFileProcessService fileService;
		try {
			fileService = new FileUtilServiceFactory().build();
			if (null == fileService) {
				logger.error("File Server 服务创建失败！[NULL]");
			}
			return fileService;
		} catch (Exception e) {
			logger.error("File Server初始化失败!", e);
		}
		return null;
	}

	protected class FileUtilServiceFactory {
		protected IFileProcessService build() {
			if(StringUtils.isNotBlank(way)){
				if("ftp".equalsIgnoreCase(way)){
					logger.info("File Server采用FTP方式处理文件上传下载.");
					return new FtpFileProcessServiceImpl();
				}else if("fastdfs".equalsIgnoreCase(way)){
					logger.info("File Server采用FastDFS方式处理文件上传下载.");
					return new FastDFSFileProcessServiceImpl();
				}else if("http".equalsIgnoreCase(way)){
					logger.info("File Server采用HTTP方式处理文件上传下载.");
					return new HttpFileProcessServiceImpl();
				}else{
					logger.info("File Server当前配置暂不支持，请修改 upload.fileserver 对应的配置信息");
					return null;
				}
			}else{
				logger.info("未配置，采用默认HTTP方式");
				return new HttpFileProcessServiceImpl();
			}
		}
	}
}
