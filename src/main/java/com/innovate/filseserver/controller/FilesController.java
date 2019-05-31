package com.innovate.filseserver.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.easysoft.commons.cons.Constants;
import com.github.pagehelper.Page;
import com.innovate.filseserver.model.UploadFile;
import com.innovate.filseserver.service.IFileService;

/**
 * 文件查询管理列表
 * 
 * @author IvanHsu
 */
@RestController
public class FilesController {

	// 日志
	private Logger logger = LoggerFactory.getLogger(FilesController.class);

	@Autowired
	private IFileService fileService;

	/**
	 * Title: listFiles Description: 文件的列表查询
	 * @param reqeust
	 * @param response
	 * { "code": 0, "msg": "", "count": 1000, "data": [{}, {}] }
	 * @return
	 */
	@RequestMapping(value={"files/list"},produces = "application/json")
	public JSONObject listFiles(HttpServletRequest reqeust, HttpServletResponse response, 
			Integer page, Integer limit,UploadFile file) {
		JSONObject json = new JSONObject();
		String isSuper = (String) reqeust.getSession().getAttribute("isSuper");
		//如果不是超级用户只能看到自己的附件信息
		if(!isSuper.equals(Constants.EMUN_Y)){
			String currentuserId = (String) reqeust.getSession().getAttribute("_userId");
			file.setGrantUser(currentuserId);
		}
		Page<UploadFile> filePage = fileService.getPage(file, page, limit, "create_time desc");
		json.put("code", 0);
		json.put("msg", "查询成功");
		json.put("count",filePage.getTotal());
		json.put("data", filePage.getResult());
		return json;
	}
	
	/**
	 * 
	* Title: delete
	* Description: 删除
	* @param reqeust
	* @param ids
	* @return
	 */
	@RequestMapping("files/delete")
	public JSONObject delete(HttpServletRequest reqeust,String ids)
	{
		JSONObject json = new JSONObject();
		try {
			fileService.delByIds(new UploadFile(), ids);
			json.put("success", true);
		} catch (Exception e) {
			json.put("success", false);
			logger.error(e.getMessage());
		}
		return json;
	}

}
