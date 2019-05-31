package com.innovate.filseserver.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.innovate.filseserver.model.UploadFile;
import com.innovate.filseserver.model.UploadUser;
import com.innovate.filseserver.service.IFileProcessService;
import com.innovate.filseserver.service.IFileService;
import com.innovate.filseserver.service.IUploadUserService;

/**
 * 文件上传
 * @author IvanHsu
 */
@RestController
public class UploadController {
	private Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	@Autowired
	private IFileProcessService processService;
	@Autowired
	private IFileService fileService;
	@Autowired
	private IUploadUserService userService;

	/**
	* Title: auth
	* Description: 上传之前的 验证 以及 页面删除文件的处理
	* @param request
	* @param response
	* @param authcode
	* @return
	 */
	@RequestMapping(value = { "/file_server/preUpload" })
	public JSONObject auth(HttpServletRequest request,HttpServletResponse response,
			String authcode,String fileIds,String objectId){
		JSONObject json = new JSONObject();
		if(StringUtils.isNotBlank(authcode)){
			try {
				UploadUser user = userService.getUserByAuthCode(authcode);
				if(null!=user){
					try {
						String[] ids = StringUtils.isNotBlank(fileIds) ? fileIds.split(",") : null;
						fileService.deleteFileExceptIds(ids, objectId);
						json.put("errorCode", "0");
						json.put("grantUser", user.getId());
						json.put("errorMsg", "验证用户成功");
					} catch (Exception e) {
						json.put("errorCode", "104");
						json.put("errorMsg", "上传验证失败，请联系管理员");
					}
				}else{
					json.put("errorCode", "101");
					json.put("errorMsg", " authcode授权码验证不通过，用户不存在");
				}
			} catch (Exception e) {
				json.put("errorCode", "103");
				json.put("errorMsg", " authcode授权码验证异常:"+e.getMessage());
			}
		}else{
			json.put("errorCode", "100");
			json.put("errorMsg", " authcode 不能为空，请设置授权码");
		}
		return json;
	}
	
	/**
	 * 
	* Title: uploadFile
	* Description: 
	* @param request
	* @param files
	* @param response
	* @return
	* @throws ServletException
	* @throws IOException
	 */
	@RequestMapping(value = { "/file_server/upload" })
	public JSONObject uploadFile(HttpServletRequest request,@RequestParam("file")MultipartFile file,
			HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		String objectId = request.getParameter("objectId");
		String grantUser = request.getParameter("grantUser");//可以传id过来，为了防止泄露Id还是传zuthcode在查询一下
		String operator = request.getParameter("operator");
		if(null!= file){
			if(StringUtils.isNotBlank(grantUser)){
				try {
					processService.uploadFile(file,objectId,grantUser,operator,getLoginIpAddr(request));
					json.put("errorCode", "0");
					json.put("errorMsg", "问先处理成功");
				} catch (Exception e) {
					json.put("errorCode", "102");
					json.put("errorMsg", "文件保存异常："+e.getMessage());
					logger.error("[{}]上传文件失败{},原因：{}", operator,getLoginIpAddr(request),e.getMessage());
				}
			
			}else{
				json.put("errorCode", "100");
				json.put("errorMsg", " 授权用户为空，请检查设置...");
			}
		}
		return json;
	}
	
	/**
	 * 下载文件
	 * @param response
	 * @param request
	 */
	@RequestMapping({ "/file_server/downloadSingle" })
	public void downloadFile(HttpServletResponse response, HttpServletRequest request) {
		// 要下在的文件的id集合
		String fileId = request.getParameter("fileId");
		if (StringUtils.isNotEmpty(fileId)) {
			try {
				processService.downloadSingleFile(fileId, response);
			} catch (Exception e) {
				logger.error("文件下载处理异常：{}", e.getMessage());
			}
		} else {
			logger.error("文件下载失败,请选择要下载的文件！");
		}
	}

	/**
	 * 关联对象的所有文件的压缩包下载
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping({ "/file_server/downloadZip" })
	public void downloadRefObjFile(HttpServletResponse response, HttpServletRequest request) {
		// 要下在的文件的id集合
		String objectId = request.getParameter("objectId");
		if (StringUtils.isNotEmpty(objectId)) {
			try {
				processService.downloadFileZip(objectId, response);
			} catch (Exception e) {
				logger.error("文件下载处理异常：{}", e.getMessage());
			}
		} else {
			logger.error("文件下载失败,请选择要下载的文件！");
		}
	}

	/**
	 * 单个文件的信息
	 * @param response
	 * @param request
	 */
	@RequestMapping({ "/file_server/getFileById" })
	public String getFileById(HttpServletResponse response, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String fileId = request.getParameter("fileId");
		try {
			if (StringUtils.isNotBlank(fileId)) {
				UploadFile uploadFile = fileService.getFile(fileId);
				json.put("success", true);
				json.put("message", "附件信息获取成功");
				json.put("data", JSON.toJSONString(uploadFile));
			} else {
				json.put("success", false);
				json.put("message", "文件fileId不能为空");
			}
		} catch (Exception e) {
			json.put("success", false);
			json.put("message", "服务处理异常，请查看日志");
			logger.error("id[{}]对应附件信息获取异常:{}", fileId, e.getMessage());
		}
		return json.toJSONString();
	}

	/**
	 * 文件的信息
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping({ "/file_server/getFileByObjectId" })
	public String getFileByObjectId(HttpServletResponse response, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String objectId = request.getParameter("objectId");
		try {
			if (StringUtils.isNotBlank(objectId)) {
				List<UploadFile> uploadFileList = fileService.getFilesByObjectId(objectId);
				json.put("success", true);
				json.put("message", "附件信息获取成功");
				json.put("data", uploadFileList);
			} else {
				json.put("success", false);
				json.put("message", "文件objectId不能为空");
			}
		} catch (Exception e) {
			json.put("success", false);
			json.put("message", "服务处理异常，请查看日志");
			logger.error("id[{}]对应附件信息获取异常:{}", objectId, e.getMessage());
		}
		return json.toJSONString();
	}

	/**
	 * 删除附件信息
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping({ "/file_server/delete" })
	public String deleteFile(HttpServletResponse response, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String[] fileIds = request.getParameterValues("fileId");
		String logic = request.getParameter("logic");
		try {
			if (ArrayUtils.isNotEmpty(fileIds)) {
				boolean isLogicDel = StringUtils.isNotBlank(logic) && "false".equalsIgnoreCase(logic) ? false : true;
				processService.deleteFile(fileIds, isLogicDel);
				json.put("success", true);
				json.put("message", "删除成功");
			}
		} catch (Exception e) {
			json.put("success", false);
			json.put("message", "服务处理异常，请查看日志");
			logger.error("id[{}]对应附件信息删除异常:{}", fileIds, e.getMessage());
		}
		return json.toJSONString();
	}
	
	/**
	 * 获取真实的登录的ip
	 * @param request
	 * @return
	 */
	private static String getLoginIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

}
