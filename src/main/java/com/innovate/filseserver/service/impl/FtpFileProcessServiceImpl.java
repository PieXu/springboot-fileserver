package com.innovate.filseserver.service.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.easysoft.commons.cons.Constants;
import com.easysoft.commons.helper.CommonsHelper;
import com.innovate.filseserver.dao.FilesDao;
import com.innovate.filseserver.dao.UploadFileDao;
import com.innovate.filseserver.model.UploadFile;
import com.innovate.filseserver.service.IFileProcessService;
import com.innovate.filseserver.utils.FtpUploadUtils;

/**
 * 文件处理处理方法
 * @author IvanHsu
 *
 */
public class FtpFileProcessServiceImpl implements IFileProcessService{

	@Autowired
	private UploadFileDao fileDao;
	@Autowired
	private FilesDao dao;
	private FtpUploadUtils ftpUtils = FtpUploadUtils.getInstance();
	
	@Override
	public String uploadFile(MultipartFile fileItem,String objectId, String grantUser,String operator,String remoteIp) throws Exception
	{
		String storePath = ftpUtils.getStorePath();
		//解决同一个文件名重复被覆盖问题
		String fileId = CommonsHelper.getUUID();
		// 1. 获取文件的实际内容
		String name = fileItem.getOriginalFilename();
		String subfix = name.substring(name.lastIndexOf("."));
		String ext = name.substring(name.lastIndexOf(".")+1);
		// 2. 保存文件,加扩展名保存， 用于图片文件服务器的读取
		boolean bol = ftpUtils.storeFile(fileId + subfix , storePath, fileItem);
		// 3. 保存成功后，创建表记录
		if(bol){
			//创建文件对象保存
			UploadFile file = new UploadFile();
			file.setId(fileId);
			file.setName(name);
			file.setPath(storePath);
			file.setCreateTime(new Date());
			file.setUpdateTime(new Date());
			file.setSize(fileItem.getSize());
			file.setGrantUser(grantUser);//测试用
			file.setExt(ext);
			file.setType(fileItem.getContentType());
			file.setUploadUser(operator);
			file.setRemoteIp(remoteIp);
			file.setObjectId(objectId);
			file.setFullPath(storePath+File.separator+fileId+"."+ext);
			dao.insert(file);
		}
		return fileId;
	}

	/**
	 * 单个文件下载
	 */
	@Override
	public void downloadSingleFile(String fileId, HttpServletResponse response) throws Exception {
		if(StringUtils.isNotBlank(fileId))
		{
			response.setContentType("application/octet-stream;charset=UTF-8");
			UploadFile uploadFile = dao.get(new UploadFile(fileId));
			if(null!=uploadFile)
			{
				response.addHeader("Content-Length", String.valueOf(uploadFile.getSize()));
				response.setHeader("Content-Disposition", "attachment;filename=" + uploadFile.getName());
				ftpUtils.downloadFile(uploadFile.getName(),uploadFile.getPath(), response.getOutputStream());
			}
		}
	}

	/**
	 * 单个业务对象
	 * 相关的附件下载
	 */
	@Override
	public void downloadFileZip(String objectId,HttpServletResponse response) throws Exception {
		if(StringUtils.isNotBlank(objectId)){
			List<UploadFile> fileList = fileDao.getFilesByObjectId(objectId);
			if(CollectionUtils.isNotEmpty(fileList))
			{
				byte[] buffer = new byte[1024];
				//创建临时文件
		        File zipFile = ftpUtils.downloadCompressFiles(fileList,response);
		        int len;
		        FileInputStream zipInput =new FileInputStream(zipFile);
		        OutputStream out = response.getOutputStream();
		        response.setContentType("application/octet-stream");
		        response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("附件文件压缩包", "UTF-8") + ".zip");
		        while ((len=zipInput.read(buffer))!= -1){
		            out.write(buffer,0,len);
		        }
		        zipInput.close();
		        out.flush();
		        out.close();
		        zipFile.delete();
			}
		}
	}
	
	/**
	 * 删除文件
	 */
	@Override
	public void deleteFile(String[] fileIds, boolean isLogicDel) throws Exception {
		if(!ArrayUtils.isEmpty(fileIds)){
			if(isLogicDel){
				deleteFile(fileIds);
			}else{
				deleteDiskFile(fileIds);
			}
		}
	}

	/**
	 * 逻辑删除，只修改数据库的记录为删除状态 （-1）
	 * 正常状态为 0
	 */
	private void deleteFile(String[] fileIds) throws Exception {
		if(!ArrayUtils.isEmpty(fileIds)){
			dao.changeDelFlag(new UploadFile() , fileIds, Constants.STATUS_DELETE);
		}
	}

	/**
	 * 数据库记录和文件系统中同时删除
	 * 不可恢复
	 */
	private void deleteDiskFile(String[] fileIds) throws Exception {
		if(null!=fileIds && fileIds.length>0){
			for(String fileId : fileIds)
			{
				UploadFile file = dao.get(new UploadFile(fileId));
				if(ftpUtils.deleteFile(file.getPath(), file.getName()))
					dao.delete(file);
			}
		}
	}
}
