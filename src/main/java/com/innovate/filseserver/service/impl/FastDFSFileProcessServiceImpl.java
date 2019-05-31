package com.innovate.filseserver.service.impl;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.easysoft.commons.cons.Constants;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.innovate.filseserver.dao.FilesDao;
import com.innovate.filseserver.dao.UploadFileDao;
import com.innovate.filseserver.model.UploadFile;
import com.innovate.filseserver.service.IFileProcessService;

/**
 * FastDFS上传的处理方法
 * @author Founder
 *
 */
public class FastDFSFileProcessServiceImpl implements IFileProcessService{

	private Logger logger = LoggerFactory.getLogger(FastDFSFileProcessServiceImpl.class);
	@Autowired
    private FastFileStorageClient storageClient;
	
//	@Autowired
	private UploadFileDao fileDao;
	@Autowired
	private FilesDao dao;
	
	@Override
	public String uploadFile(MultipartFile fileItem,String objectId, String grantUser,String operator,String remoteIp) throws Exception {
		//解决同一个文件名重复被覆盖问题
		String fileId = UUID.randomUUID().toString().replaceAll("-", "");
		// 2. 保存文件,加扩展名保存， 用于图片文件服务器的读取
		StorePath storePath = storageClient.uploadFile(fileItem.getInputStream(),fileItem.getSize(), 
				FilenameUtils.getExtension(fileItem.getName()),null);
		// 3. 保存成功后，创建表记录
		if(null!=storePath){
			//创建文件对象保存
			UploadFile file = new UploadFile();
			file.setId(fileId);
			file.setName(FilenameUtils.getName(fileItem.getName()));
			file.setCreateTime(new Date());
			file.setUpdateTime(new Date());
			file.setSize(fileItem.getSize());
			file.setPath(storePath.getPath());
			file.setFullPath(storePath.getFullPath());
			file.setExt(FilenameUtils.getExtension(fileItem.getName()).toLowerCase());
			file.setType(fileItem.getContentType());
			file.setRemoteIp(remoteIp);
			file.setObjectId(objectId);
			dao.insert(file);
		}
		return fileId;
	}

	/**
	 * 单个文件的下载
	 */
	@Override
	public void downloadSingleFile(String fileId, HttpServletResponse response) throws Exception 
	{
		if(StringUtils.isNotBlank(fileId)){
			UploadFile uploadFile = dao.get(new UploadFile(fileId));
			if(null!=uploadFile){
		        byte[] bytes = storageClient.downloadFile(uploadFile.getGrantUser(), uploadFile.getPath(), new DownloadByteArray());
		        if( null != bytes && bytes.length > 0)
		        {
		        	response.addHeader("Content-Length", String.valueOf(bytes.length));
		        	response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(uploadFile.getName(), "UTF-8"));
		            response.setCharacterEncoding("UTF-8");
		            ServletOutputStream outputStream = null;
		            try {
		                outputStream = response.getOutputStream();
		                outputStream.write(bytes);
		            }catch (Exception e) {
		            	logger.error("{}下载文件异常：{}",uploadFile.getName(),e.getMessage());
					}finally{
						try {
			                outputStream.flush();
			                outputStream.close();
			            } catch (IOException e) {
			            	logger.error("{}下载文件异常：{}",uploadFile.getName(),e.getMessage());
			            }
					}
		        }
			}
		}
	}

	/**
	 * 多个文件打包下载
	 */
	@Override
	public void downloadFileZip(String objectId, HttpServletResponse response) throws Exception 
	{
		if(StringUtils.isNotBlank(objectId))
		{
			List<UploadFile> fileList = fileDao.getFilesByObjectId(objectId);
			if(!fileList.isEmpty()){
				//创建临时文件
				File zipFile = new File("zip_temp_"+UUID.randomUUID());
		        ZipOutputStream zipOS = new ZipOutputStream(new FileOutputStream(zipFile));
		        byte[] buffer = new byte[1024];
				for(UploadFile file : fileList)
				{
					 ZipEntry entry = new ZipEntry(file.getName());
		        	 zipOS.putNextEntry(entry);
		        	 //获取每个文件的字节输入流写到文件中
		        	 InputStream bis = getInputStream(file);
		             if (bis != null) {
		                 int readLen = -1;
		                 while ((readLen = bis.read(buffer, 0, 1024)) != -1) {
		                	 zipOS.write(buffer, 0, readLen);
		                 }
		                 zipOS.closeEntry();
		                 bis.close();
		             }
				}
				//关闭ZIP输出
				zipOS.finish();
				zipOS.close();
				//划到response中写出压缩的文件
				int len;
		        FileInputStream zipInput =new FileInputStream(zipFile);
		        ServletOutputStream out = response.getOutputStream();
		        response.setContentType("application/octet-stream");
		        response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("附件文件压缩包", "UTF-8") + ".zip");
		        while ((len=zipInput.read(buffer))!= -1){
		            out.write(buffer,0,len);
		        }
		        zipInput.close();
		        out.flush();
		        out.close();
		        //不保留过程文件，删除, 并发会消耗服务器性能等
		        zipFile.delete();
			}
		}
	}

	/**
	 * 获取输入流
	 * @param file
	 * @return
	 */
	private InputStream getInputStream(UploadFile file) 
	{
		InputStream is = null;
		try {
			byte[] bytes = storageClient.downloadFile(file.getGrantUser(), file.getPath(), new DownloadByteArray());
			if(null!=bytes && bytes.length >0){
				is = new ByteArrayInputStream(bytes);
			}
		} catch (Exception e) {
			logger.info("下载文件失败" + e.getMessage());
		} 
		return is;
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
			dao.changeDelFlag(new UploadFile(), fileIds, Constants.STATUS_DELETE);
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
				try {
					// 删除 文件目录
					//参数：String groupName, String path
					storageClient.deleteFile(file.getGrantUser(), file.getPath());
					// 删除数据库
					dao.delete(file);
				} catch (Exception e) {
					logger.error("{} 删除文件，服务器异常：{}",file.getName(),e.getMessage());
				}
			}
		}
	}
}
