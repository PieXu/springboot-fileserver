package com.innovate.filseserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.innovate.filseserver.model.UploadFile;

/**
 * 附件上传的记录信息
 * @author IvanHsu
 */
public interface UploadFileDao {
	
	/**
	 * 
	 * @param objectId
	 * @return
	 */
	public List<UploadFile> getFilesByObjectId(String objectId);

	/**
	 * 
	 * @param fileId
	 * @param delFlag
	 */
	public void updateFileDeleteFlag(@Param("objectId")String objectId,@Param("fileId")String[] fileId,@Param("delFlag")String delFlag);


}
