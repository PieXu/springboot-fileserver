package com.innovate.filseserver.utils;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.innovate.filseserver.configuration.HttpUploadProperties;

/**
 * 工具类定义
 * @author IvanHsu
 */
@Component
@EnableConfigurationProperties({ HttpUploadProperties.class })
public class HttpUploadUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpUploadUtils.class);
	/**
	 * 属性配置文件的加载
	 */
	private static HttpUploadProperties properties;
	
	@SuppressWarnings("static-access")
	public HttpUploadUtils(HttpUploadProperties properties) {
		this.properties = properties;
	}
	
	/**
     * 删除单个文件
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) 
    {
    	boolean delete = false;
    	if(StringUtils.isNotBlank(fileName)){
    		try {
    			File file = new File(fileName);	
    			if (file.exists() && file.isFile()) {
    				file.delete();
    				delete = true;
    			}
			} catch (Exception e) {
			  logger.error("删除单个文件{}失败,异常信息{}",fileName,e.getMessage());
			}
    	}
    	return delete;
    }
	
	
	/**
	 * 文件存储路径
	 * @return
	 */
	public static String getFullPath()
	{
		return DateUtils.formatDate(new Date(),new StringBuilder().append("yyyy").append(File.separator)
				.append("MM").append(File.separator).append("dd").toString());
	}
    
	/**
	 * 文件存储路径
	 * @return
	 */
	public static String getUploadPath(String fullPath)
	{
		return properties.getBasepath() + File.separator + getFullPath();
	}

	/**
	 * 检查并创建目录
	 * @param savePath
	 */
	public static void mkDir(String path) {
		File fd = null;
		try {
			fd = new File(path);
			if (!fd.exists()) {
				fd.mkdirs();
			}
		} catch (Exception e) {
			logger.error("上传文件创建路径失败",e);
		} finally {
			fd = null;
		}
	}
}
