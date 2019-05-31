package com.innovate.filseserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "upload.fileserver.ftp")
public class FtpProperties {

	private String server;
	private String username;
	private String password;
	private String port;
	private String basepath;
	private String encoding;
	private int buffersize;

	public FtpProperties() {
		this.encoding = "UTF-8";
		this.buffersize = 1024 * 2;
		this.basepath = "/";
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

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

	public int getBuffersize() {
		return buffersize;
	}

	public void setBuffersize(int buffersize) {
		this.buffersize = buffersize;
	}
}
