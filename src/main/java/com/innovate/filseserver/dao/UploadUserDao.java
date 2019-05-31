package com.innovate.filseserver.dao;

import org.apache.ibatis.annotations.Select;

import com.easysoft.commons.cons.Constants;
import com.easysoft.commons.mybatis.dao.IBaseDao;
import com.innovate.filseserver.model.UploadUser;

/**
 * 
* Title: UploadUserDao
* Description: 
* Company: easysoft.ltd 
* @author IvanHsu
* @date 2019年5月28日
 */
public interface UploadUserDao extends IBaseDao<UploadUser>{

	/**
	 * 
	* Title: getUserByLoginName
	* Description: 
	* @param name
	* @return
	 */
	@Select("select * from t_upload_user where login_name = #{name} and del_flag = '"+Constants.STATUS_NORMAL+"'")
	public UploadUser getUserByLoginName(String name);

	/**
	 * 
	* Title: getUserByAuthCode
	* Description: 
	* @param authcode
	* @return
	 */
	@Select("select * from t_upload_user where auth_code = #{authcode} and del_flag = '"+Constants.STATUS_NORMAL+"'")
	public UploadUser getUserByAuthCode(String authcode);

}
