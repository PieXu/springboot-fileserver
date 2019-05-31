package com.innovate.filseserver.service.impl;


import java.io.BufferedInputStream;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.easysoft.commons.cons.Constants;
import com.easysoft.commons.helper.CommonsHelper;
import com.innovate.filseserver.dao.FilesDao;
import com.innovate.filseserver.dao.UploadFileDao;
import com.innovate.filseserver.model.UploadFile;
import com.innovate.filseserver.service.IFileProcessService;
import com.innovate.filseserver.utils.HttpUploadUtils;

/**
 * http上传的处理方法
 * @author IvanHsu
 *
 */
public class HttpFileProcessServiceImpl implements IFileProcessService{

//	@Autowired
	private UploadFileDao fileDao;
	@Autowired
	private FilesDao dao;
	
	/**
	 * 保存文件
	 */
	@Override
	public String uploadFile(MultipartFile fileItem,String objectId, String grantUser,String operator,String remoteIp) throws Exception {
		// 根据路径创建目录
		String path = HttpUploadUtils.getFullPath();
		String storePath = HttpUploadUtils.getUploadPath(path);
		HttpUploadUtils.mkDir(storePath);
		String fileId = CommonsHelper.getUUID();
		// 2. 获取文件的实际内容
		InputStream is = fileItem.getInputStream();
		String orginName = fileItem.getOriginalFilename();
		String name = FilenameUtils.getName(orginName);
		String subfix = orginName.substring(orginName.lastIndexOf("."));
		String ext =  FilenameUtils.getExtension(orginName);
		// 3. 保存文件,加扩展名保存， 用于图片文件服务器的读取
		FileUtils.copyInputStreamToFile(is, new File(storePath + File.separator + fileId + subfix));
		//创建文件对象保存
		UploadFile file = new UploadFile();
		file.setId(fileId);
		file.setName(name);
		file.setCreateTime(new Date());
		file.setUpdateTime(new Date());
		file.setSize(fileItem.getSize());
		file.setPath(path);
		file.setFullPath(path+ File.separator + fileId + subfix);
		file.setExt(ext);
		file.setObjectId(objectId);
		file.setRemoteIp(remoteIp);
		file.setGrantUser(grantUser);//测试用
		file.setType(fileItem.getContentType());
		file.setUploadUser(operator);
		dao.insert(file);
		return fileId;
	}

	/**
	 * 下载文件
	 */
	@Override
	public void downloadSingleFile(String fileId, HttpServletResponse response) throws Exception {
		if (StringUtils.isEmpty(fileId)) {
			return;
		}
		UploadFile uploadFile = dao.get(new UploadFile(fileId));
		response.setContentType("application/octet-stream;charset=UTF-8");
		response.addHeader("Content-Length", String.valueOf(uploadFile.getSize()));
		response.setHeader("Content-Disposition", "attachment;filename=" + uploadFile.getName());
		InputStream fis = null;
		String filePath = uploadFile.getFullPath();
		File file = new File(filePath);
		if (file.exists()) {
			try {
				fis = new BufferedInputStream(new FileInputStream(filePath));
				FileCopyUtils.copy(fis, response.getOutputStream());
				if (null != fis) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != fis) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 下载业务对象关联的所有的文件 
	 * 打包下载 ZIP
	 */
	@Override
	public void downloadFileZip(String objectId, HttpServletResponse response) throws Exception {
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
		        	 InputStream bis = new BufferedInputStream(new FileInputStream(file.getFullPath()));
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
		if (null != fileIds && fileIds.length > 0) {
			for (String fileId : fileIds) {
				UploadFile file = dao.get(new UploadFile(fileId));
				if (HttpUploadUtils.deleteFile(file.getFullPath())) {
					dao.delete(file);
				}
			}
		}
	}
}
