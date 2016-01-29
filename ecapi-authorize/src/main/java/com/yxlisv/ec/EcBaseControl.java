package com.yxlisv.ec;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.yxlisv.ec.api.Constant;
import com.yxlisv.control.AbstractBaseControl;

/**
 * 电商control层的父类
 * @author yxl
 */
public class EcBaseControl extends AbstractBaseControl{
	
	/**
	 * 获取应用平台key
	 */
	public String getAppPlatformKey() {
		return this.getFromSession("appPlatformKey").toString();
	}

	/**
	 * 获取授权的令牌
	 * @throws JSONException
	 */
	public String getAccessToken() {
		Map tokenMap = (Map) this.getFromSession("tokenMap");
		return tokenMap.get("access_token") + "";
	}
	
	/**
	 * 获取授权令牌Map
	 * @autor yxl
	 */
	public Map getTokenMap(){
		return (Map) this.getFromSession("tokenMap");
	}
	
	/**
	 * 获取卖家信息Map
	 * @autor yxl
	 */
	public Map getUserSellerMap(){
		return (Map) this.getFromSession("userSellerMap");
	}
	
	/**
	 * 获取用户昵称
	 * @autor yxl
	 */
	public String getNick(){
		Map userSellerMap = (Map) this.getFromSession("userSellerMap");
		return userSellerMap.get("nick") + "";
	}
	
	/**
	 * 获取用户id
	 * @autor yxl
	 */
	public String getUserApiId(){
		String userId = "";
		Map userSellerMap = (Map) this.getFromSession("userSellerMap");
		if(userSellerMap.get("memberId") != null) userId =  userSellerMap.get("memberId").toString();
		else if(userSellerMap.get("uid") != null) userId =  userSellerMap.get("uid").toString();
		else if(userSellerMap.get("uin") != null) userId =  userSellerMap.get("uin").toString();
		else if(userSellerMap.get("user_id") != null) userId =  userSellerMap.get("user_id").toString();
		
		return userId;
	}
	
	
	/**
	 * 获取授权地址
	 * @autor yxl
	 */
	public static Map getAuthorizePath(){
		//授权地址
		Map map = new HashMap();
		map.put("tb", Constant.tbOAuthUrl);
		map.put("albb", Constant.albbOAuthUrl);
		map.put("paipai", Constant.paipaiOAuthUrl);
		map.put("jd", Constant.jdOAuthUrl);
		return map;
	}
}