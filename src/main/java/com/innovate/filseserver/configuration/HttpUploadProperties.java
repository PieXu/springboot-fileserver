package com.innovate.filseserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HTTP上传的配置属性定义 
 * @author IvanHsu
 */
@ConfigurationProperties(prefix = "upload.fileserver.http")
public class HttpUploadProperties {

	private String basepath;
	private String encoding;
	public String getBasepath() {
		return basepath;
	}
	public void setBasepath(String basepath) {
		this.basepath = basepath;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}
