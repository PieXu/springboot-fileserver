package com.innovate.filseserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.easysoft.commons.mybatis.service.impl.BaseServiceImpl;
import com.innovate.filseserver.dao.UploadUserDao;
import com.innovate.filseserver.model.UploadUser;
import com.innovate.filseserver.service.IUploadUserService;
/**
 * 
* Title: UploadUserServiceImpl
* Description: 
* Company: easysoft.ltd 
* @author IvanHsu
* @date 2019年5月28日
 */
@Service
public class UploadUserServiceImpl extends BaseServiceImpl<UploadUser> implements IUploadUserService{

	@Autowired
	private UploadUserDao userDao;
	
	/*
	 * （非 Javadoc）
	* Title: getUserByLoginName
	* Description: 
	* @param name
	* @return
	* @see com.innovate.filseserver.service.IUploadUserService#getUserByLoginName(java.lang.String)
	 */
	@Override
	public UploadUser getUserByLoginName(String name) {
		if(StringUtils.hasText(name)){
			return userDao.getUserByLoginName(name);
		}
		return null;
	}

	/*
	 * （非 Javadoc）
	* Title: getUserByAuthCode
	* Description: 
	* @param authcode
	* @return
	* @see com.innovate.filseserver.service.IUploadUserService#getUserByAuthCode(java.lang.String)
	 */
	@Override
	public UploadUser getUserByAuthCode(String authcode) {
		if(StringUtils.hasText(authcode)){
			return userDao.getUserByAuthCode(authcode);
		}
		return null;
	}

}
