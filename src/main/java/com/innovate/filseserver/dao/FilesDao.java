package com.innovate.filseserver.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.easysoft.commons.mybatis.dao.IBaseDao;
import com.innovate.filseserver.model.UploadFile;
/**
 * 
* Title: FilesDao
* Description: 
* Company: easysoft.ltd 
* @author IvanHsu
* @date 2019年5月28日
 */
public interface FilesDao extends IBaseDao<UploadFile>{

	@Select("SELECT t.grant_user as userId, count(t.grant_user) as count FROM t_upload_file t group by t.grant_user")
	public List<Map<String, Object>> getGroupData();

}
