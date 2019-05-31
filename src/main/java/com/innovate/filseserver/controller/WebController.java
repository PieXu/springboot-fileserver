package com.innovate.filseserver.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.easysoft.commons.cons.Constants;
import com.easysoft.commons.helper.CommonsHelper;
import com.innovate.filseserver.model.ResultObject;
import com.innovate.filseserver.model.UploadUser;
import com.innovate.filseserver.service.IFileService;
import com.innovate.filseserver.service.IUploadUserService;
import com.innovate.filseserver.utils.RandomValidateCodeUtil;


@Controller
public class WebController {
	private static final Logger logger = LoggerFactory.getLogger(WebController.class);

	@Autowired
	private IUploadUserService userService;
	@Autowired
	private IFileService fileService;
	
	/**
	 * 
	 * Title: login Description: 登录页面
	 * @param request
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("login")
	public ResultObject login(HttpServletRequest request, Model model) 
	{
		ResultObject result = new ResultObject();
		try {
			String name = request.getParameter("username");
			String password = request.getParameter("password");
			String code = request.getParameter("code");
			HttpSession session = request.getSession();
			String sessionCode = (String) session.getAttribute(RandomValidateCodeUtil.RANDOMCODEKEY);
			if(sessionCode.equals(code)){
				UploadUser user = userService.getUserByLoginName(name);
				if(null!=user){
					String realPwd = user.getPassword();
					String salt = user.getSalt();
					String encoderPassword = CommonsHelper.encoderPassword(salt, password);
					if(UploadUser.STATUS.NORMAL.toString().equalsIgnoreCase(user.getStatus())){
						if(encoderPassword.equals(realPwd)){
							session.setAttribute(Constants.SESSION_USER_KEY, user);
							session.setAttribute("username", user.getName());
							session.setAttribute("isSuper", user.getIsSuper());
							session.setAttribute("_userId", user.getId());
							result.setResult(ResultObject.OPERATE_RESULT.success.toString());
						}else{
							result.setResult(ResultObject.OPERATE_RESULT.fail.toString());
							result.setMessage("密码不正确");	
						}
					}else{
						result.setResult(ResultObject.OPERATE_RESULT.fail.toString());
						result.setMessage("当前用户已冻结，请联系管理员");
					}
				}else{
					result.setResult(ResultObject.OPERATE_RESULT.fail.toString());
					result.setMessage("当前用户不存在");
				}
			}else{
				result.setResult(ResultObject.OPERATE_RESULT.fail.toString());
				result.setMessage("验证码不正确");
			}
			
		} catch (Exception e) {
			result.setResult(ResultObject.OPERATE_RESULT.fail.toString());
			result.setMessage("登录异常，请联系管理员");
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 
	* Title: getVerify
	* Description: 验证码图片
	* @param request
	* @param response
	 */
	@RequestMapping(value = "/getVerify")
	public void getVerify(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("image/jpeg");// 设置相应类型,告诉浏览器输出的内容为图片
			response.setHeader("Pragma", "No-cache");// 设置响应头信息，告诉浏览器不要缓存此内容
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expire", 0);
			RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();
			randomValidateCode.getRandcode(request, response);// 输出验证码图片方法
		} catch (Exception e) {
			logger.error("获取验证码失败>>>> ", e);
		}
	}
	
	/**
	 * 
	* Title: changPass
	* Description: 修改密码
	* @param request
	* @param model
	* @return
	 */
	@ResponseBody
	@RequestMapping(value={"changPass"})
	public ResultObject changPass(HttpServletRequest request,Model model)
	{
		ResultObject result = new ResultObject();
		try {
			String id = request.getParameter("userId");
			if(!StringUtils.isEmpty(id)){
				// 设置加密密码
				UploadUser user = userService.get(new UploadUser(id));
				String newPass = request.getParameter("newpassword");
				user.setPassword(CommonsHelper.encoderPassword(user.getSalt(), newPass));
				userService.update(user);
				result.setResult(ResultObject.OPERATE_RESULT.success.toString());
				result.setMessage("密码修改成功！");
			}else{
				result.setResult(ResultObject.OPERATE_RESULT.fail.toString());
				result.setMessage("参数ID 为空 ");
			}
		} catch (Exception e) {
			result.setResult(ResultObject.OPERATE_RESULT.fail.toString());
			result.setMessage("操作失败");
		}
		return result;
	}

	/**
	 * 
	 * Title: main Description: 菜单跳转
	 * 
	 * @param request
	 * @param model
	 * @param path
	 * @return
	 */
	@RequestMapping("{path}")
	public String main(HttpServletRequest request, Model model, @PathVariable("path") String path) {
		model.addAttribute("path", path);
		return path;
	}
	
	/**
	 * 
	* Title: logout
	* Description: 退出
	* @param request
	* @return
	 */
	@RequestMapping("logout")
	public String logout(HttpServletRequest request)
	{
		request.getSession().removeAttribute(Constants.SESSION_USER_KEY);
		return "redirect:/";
	}
	
	/**
	 * 
	* Title: getData
	* Description: 首页图标分析数据数据查询
	* @param request
	* @param model
	* @return
	 */
	@RequestMapping("getData")
	@ResponseBody
	public JSONObject getData(HttpServletRequest request,Model model)
	{
		JSONObject jsonData = new JSONObject();
		List<Object> legendData = new ArrayList<Object>();
		Map<String,Object> selectedMap = new HashMap<String,Object>();
		List<UploadUser> userList = userService.getList(new UploadUser());
		List<Map<String,Object>> seriesData = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> tempSeriesData = fileService.getGroupData();
		int size = null == userList ? 0 : userList.size();
		if(size>0){
			Map<String,String> tempMap = new HashMap<String,String>();
			for(int i=0;i<size;i++){
				legendData.add(userList.get(i).getName());
				selectedMap.put(userList.get(i).getName(), true);
				tempMap.put(userList.get(i).getId(), userList.get(i).getName());
			}
			for(Map<String,Object> map : tempSeriesData){
				String uId = tempMap.get(map.get("userId"));
				if(StringUtils.isNotBlank(uId)){
					Map<String,Object> dataMap = new HashMap<String,Object>();
					dataMap.put("name", tempMap.get(map.get("userId")));
					dataMap.put("value", map.get("count"));
					seriesData.add(dataMap);
				}
			}
		}
		jsonData.put("legendData", legendData); 
		jsonData.put("selected", selectedMap); 
		jsonData.put("seriesData", seriesData); 
		return jsonData;
	}
	
}
