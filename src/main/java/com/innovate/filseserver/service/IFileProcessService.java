package com.innovate.filseserver.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理
 * 业务方法定义
 * @author Founder
 *
 */
public abstract interface IFileProcessService {

	/**
	 * 文件上传
	 * @param item 文件对象
	 * @param objectId 关联的业务对象
	 * @return
	 */
	public String uploadFile(MultipartFile fileItem,String objectId, String grantUser,String operator,String remoteIp) throws Exception;
	
	/**
	 * 单个文件下载
	 * @param fileIds
	 * @param response
	 */
	public void downloadSingleFile(String fileId, HttpServletResponse response) throws Exception;
	
	/**
	 * 文件合集打包下载
	 * @param objectId
	 * @param response
	 */
	public void downloadFileZip(String objectId ,HttpServletResponse response) throws Exception;

	/**
	 * 删除文件
	 * @param fileIds 删除的文件的ids集合
	 * @param isLogicDel 是否是逻辑删除
	 * 					 true：只删除数据库的记录，文件系统不删除
	 * 					 false：除数据库的记录，删除文件系统，不可恢复
	 */
	public void deleteFile(String[] fileIds,boolean isLogicDel) throws Exception;
	
}
