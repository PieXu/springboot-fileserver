/**
 * @name:IFileService.java
 * @package:com.xu.sys.service
 * @time: 2017年7月10日 上午10:37:13
 * @author IvanHsu 
 */
package com.innovate.filseserver.service;

import java.util.List;
import java.util.Map;

import com.easysoft.commons.mybatis.service.IBaseService;
import com.innovate.filseserver.model.UploadFile;

/**
 * @desc:文件处理
 * @author IvanHsu 
 */
public interface IFileService extends IBaseService<UploadFile>{

	/**
	* Title: getGroupData
	* Description: 
	* @return
	 */
	public List<Map<String, Object>> getGroupData();
	
	/**
	 * 主键查找
	 * @param fileId
	 * @return
	 */
	public abstract UploadFile getFile(String fileId);
	
	/**
	 * 业务对象关联查找
	 * @param paramString
	 * @return
	 */
	public abstract List<UploadFile> getFilesByObjectId(String objectId);

	/**
	 * 删除fileIds以为的所有objectId 对应的文件
	* Title: deleteFileExceptIds
	* Description: 
	* @param fileIds
	* @param objectId
	* @throws Exception
	 */
	public void deleteFileExceptIds(String[] fileIds,String objectId) throws Exception ;



}
