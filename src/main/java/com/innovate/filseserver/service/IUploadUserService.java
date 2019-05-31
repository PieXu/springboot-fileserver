package com.innovate.filseserver.service;

import com.easysoft.commons.mybatis.service.IBaseService;
import com.innovate.filseserver.model.UploadUser;

/**
 * 
* Title: IUploadUserService
* Description: 
* Company: easysoft.ltd 
* @author IvanHsu
* @date 2019年5月28日
 */
public interface IUploadUserService extends IBaseService<UploadUser>{

	public UploadUser getUserByLoginName(String name);

	public UploadUser getUserByAuthCode(String authcode);

}
