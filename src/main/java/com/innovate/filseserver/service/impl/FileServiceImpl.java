package com.innovate.filseserver.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easysoft.commons.cons.Constants;
import com.easysoft.commons.mybatis.service.impl.BaseServiceImpl;
import com.innovate.filseserver.dao.FilesDao;
import com.innovate.filseserver.dao.UploadFileDao;
import com.innovate.filseserver.model.UploadFile;
import com.innovate.filseserver.service.IFileService;

/**
 * 文件处理
 * @author IvanHsu
 */
@Service("com.innovate.filseserver.service.impl.FileServiceImpl")
public class FileServiceImpl extends BaseServiceImpl<UploadFile> implements IFileService {

	@Autowired
	private FilesDao filesDao;
	@Autowired
	private UploadFileDao uploadDao;

	/*
	 * （非 Javadoc）
	* Title: getGroupData
	* Description: 
	* @return
	* @see com.innovate.filseserver.service.IFileService#getGroupData()
	 */
	@Override
	public List<Map<String, Object>> getGroupData() {
		return filesDao.getGroupData();
	}

	/*
	 * （非 Javadoc）
	* Title: getFile
	* Description: 
	* @param fileId
	* @return
	* @see com.innovate.filseserver.service.IFileService#getFile(java.lang.String)
	 */
	@Override
	public UploadFile getFile(String fileId) {
		return filesDao.get(new UploadFile(fileId));
	}

	/*
	 * （非 Javadoc）
	* Title: getFilesByObjectId
	* Description: 
	* @param objectId
	* @return
	* @see com.innovate.filseserver.service.IFileService#getFilesByObjectId(java.lang.String)
	 */
	@Override
	public List<UploadFile> getFilesByObjectId(String objectId) {
		return uploadDao.getFilesByObjectId(objectId);
	}

	/*
	 * （非 Javadoc）
	* Title: deleteFileExceptIds
	* Description: 
	* @param fileIds
	* @param objectId
	* @throws Exception
	* @see com.innovate.filseserver.service.IFileService#deleteFileExceptIds(java.lang.String[], java.lang.String)
	 */
	@Override
	public void deleteFileExceptIds(String[] fileIds, String objectId) throws Exception {
		uploadDao.updateFileDeleteFlag(objectId,fileIds,Constants.STATUS_DELETE);
	}

}
