package com.yxlisv.ec.authorize;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yxlisv.ec.EcBaseControl;
import com.yxlisv.ec.api.Constant;
import com.yxlisv.ec.api.IAuthorizeService;
import com.yxlisv.ec.api.IUserApiService;
import com.yxlisv.control.AbstractBaseControl;
import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.spring.annotation.ApplicationContextUtil;

/**
 * 用户授权接口
 * @author yxl
 */
@Controller
@RequestMapping("/ec/api")
public class AuthorizeControl extends EcBaseControl{
	
	/**
	 * 淘宝授权
	 * @param code 外部api用户授权后返回的用户授权获取授权码Code
	 * @param error 错误码
	 * @param error_description 错误描述信息
	 * @author yxl
	 * @throws Exception 
	 * @throws JSONException 
	 */
	@RequestMapping("/authorize/tb")
	public String authorizeTb(String code, String error, String error_description) throws Exception {
		logger.info("淘宝用户授权...");
		this.getSession().setAttribute("appPlatformKey", "tb");//设置应用平台key
		this.getSession().setAttribute("appKey", Constant.tbAppKey);//AppKey
		
		/** 用户的api服务 */
		IUserApiService userApiService = (IUserApiService) ApplicationContextUtil.getBean(this.getAppPlatformKey() + "UserApiService");
		
		/** 授权的api服务 */
		IAuthorizeService authorizeService = (IAuthorizeService) ApplicationContextUtil.getBean(this.getAppPlatformKey() + "AuthorizeService");
		
		if(code == null) throw new MessageException("获取授权码失败：" + error_description);
		//同一个会话中不允许使用同一个授权码，非法的code
		if((this.getSession().getAttribute("authorize_code") != null && this.getSession().getAttribute("authorize_code").toString().equals(code))){
			return this.redirect(Constant.tbOAuthUrl);
		}
		this.getSession().setAttribute("authorize_code", code);
		
		//如果发生错误
		if(error!=null) throw new MessageException("授权失败！错误信息：" + error + "("+ URLDecoder.decode(error_description, "utf-8") +")");

		this.logger.info("获取授权码成功：" + code);
		Map tokenMap = authorizeService.authorize(code);
		if(tokenMap.get("access_token")==null) {
			this.getSession().invalidate();
			return this.redirect(Constant.tbOAuthUrl);
		}
		this.getSession().setAttribute("tokenMap", tokenMap);
		
		//加载用户信息
		Map userSellerMap = userApiService.getSeller(null, tokenMap.get("access_token") + "");
		AbstractBaseControl.getRequest().getSession().setAttribute("userSellerMap", userSellerMap);
		
		return this.redirect("/login/" + this.getAppPlatformKey());
	}
	
	
	/**
	 * 阿里巴巴授权
	 * @param code 外部api用户授权后返回的用户授权获取授权码Code
	 * @param error 错误码
	 * @param error_description 错误描述信息
	 * @author yxl
	 * @throws Exception 
	 * @throws JSONException 
	 */
	@RequestMapping("/authorize/albb")
	public String authorizeAlbb(String code, String error, String error_description, HttpServletRequest request) throws Exception {
		
		logger.info("阿里巴巴用户授权...");
		request.getSession().setAttribute("appPlatformKey", "albb");//设置应用平台key
		request.getSession().setAttribute("appKey", Constant.albbAppKey);//AppKey
		
		/** 用户的api服务 */
		IUserApiService userApiService = (IUserApiService) ApplicationContextUtil.getBean(this.getAppPlatformKey() + "UserApiService");
		
		/** 授权的api服务 */
		IAuthorizeService authorizeService = (IAuthorizeService) ApplicationContextUtil.getBean(this.getAppPlatformKey() + "AuthorizeService");
		
		if(code == null) throw new MessageException("获取授权码失败");
		//同一个会话中不允许使用同一个授权码，非法的code
		if((request.getSession().getAttribute("authorize_code") != null && request.getSession().getAttribute("authorize_code").toString().equals(code))){
			return this.redirect(Constant.albbOAuthUrl);
		}
		request.getSession().setAttribute("authorize_code", code);
		
		//如果发生错误
		if(error!=null) throw new MessageException("授权失败！错误信息：" + error + "("+ URLDecoder.decode(error_description, "utf-8") +")");

		this.logger.info("获取授权码成功：" + code);
		Map tokenMap = authorizeService.authorize(code);
		if(tokenMap.get("access_token")==null) {
			request.getSession().invalidate();
			return this.redirect(Constant.albbOAuthUrl);
		}
		request.getSession().setAttribute("tokenMap", tokenMap);
		
		//加载用户信息
		Map userSellerMap = userApiService.getSeller(tokenMap.get("memberId").toString(), tokenMap.get("access_token") + "");
		request.getSession().setAttribute("userSellerMap", userSellerMap);
		
		return this.redirect("/login/" + this.getAppPlatformKey());
	}
	
	
	
	/**
	 * 拍拍授权
	 * @param access_token 授权令牌
	 * @param error 错误码
	 * @param error_description 错误描述信息
	 * @author yxl
	 * @throws Exception 
	 * @throws JSONException 
	 */
	@RequestMapping("/authorize/paipai")
	public String authorizePaipai(String access_token, String useruin, String sign) throws Exception {
		
		logger.info("拍拍用户授权...");
		this.getSession().setAttribute("appPlatformKey", "paipai");//设置应用平台key
		this.getSession().setAttribute("appKey", Constant.paipaiAppKey);//AppKey
		
		/** 用户的api服务 */
		IUserApiService userApiService = (IUserApiService) ApplicationContextUtil.getBean(this.getAppPlatformKey() + "UserApiService");
		
		if(access_token == null) throw new MessageException("获取授权码失败");
		
		Map tokenMap = new HashMap();
		tokenMap.put("access_token", access_token);
		this.getSession().setAttribute("tokenMap", tokenMap);
		
		//加载用户信息
		Map userSellerMap = userApiService.getSeller(useruin, access_token);
		AbstractBaseControl.getRequest().getSession().setAttribute("userSellerMap", userSellerMap);
		
		return this.redirect("/login/" + this.getAppPlatformKey());
	}
	
	
	
	/**
	 * 京东授权
	 * @param code 外部api用户授权后返回的用户授权获取授权码Code
	 * @param error 错误码
	 * @param error_description 错误描述信息
	 * @author yxl
	 * @throws Exception 
	 * @throws JSONException 
	 */
	@RequestMapping("/authorize/jd")
	public String authorizeJd(String code, String error, String error_description) throws Exception {
		
		logger.info("京东用户授权...");
		this.getSession().setAttribute("appPlatformKey", "jd");//设置应用平台key
		this.getSession().setAttribute("appKey", Constant.jdAppKey);//AppKey
		
		/** 用户的api服务 */
		IUserApiService userApiService = (IUserApiService) ApplicationContextUtil.getBean(this.getAppPlatformKey() + "UserApiService");
		
		/** 授权的api服务 */
		IAuthorizeService authorizeService = (IAuthorizeService) ApplicationContextUtil.getBean(this.getAppPlatformKey() + "AuthorizeService");
		
		//同一个会话中不允许使用同一个授权码
		if(this.getSession().getAttribute("authorize_code") != null && this.getSession().getAttribute("authorize_code").toString().equals(code)){
			return this.redirect(Constant.jdOAuthUrl);
		}
		
		//如果发生错误
		if(error!=null) throw new MessageException("授权失败！错误信息：" + error + "("+ URLDecoder.decode(error_description, "utf-8") +")");

		this.logger.info("获取授权码成功：" + code);
		Map tokenMap = authorizeService.authorize(code);
		this.getSession().setAttribute("tokenMap", tokenMap);
		
		//加载用户信息
		Map userSellerMap = userApiService.getSeller(null, tokenMap.get("access_token") + "");
		AbstractBaseControl.getRequest().getSession().setAttribute("userSellerMap", userSellerMap);
		
		return this.redirect("/login/" + this.getAppPlatformKey());
	}
}