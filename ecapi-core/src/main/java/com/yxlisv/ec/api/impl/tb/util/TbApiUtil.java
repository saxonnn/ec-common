package com.yxlisv.ec.api.impl.tb.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.ec.api.Constant;
import com.taobao.api.internal.util.WebUtils;
import com.yxlisv.util.date.DateUtil;
import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.string.StringUtil;

/**
 * 淘宝api工具类
 * @author yxl
 */
public class TbApiUtil {
	
	//定义一个全局的记录器，通过LoggerFactory获取  
	protected static Logger logger = LoggerFactory.getLogger(TbApiUtil.class);

	/**
	 * 查询淘宝api，获取json数据
	 * @param method api方法
	 * @param fields api请求返回参数
	 * @param accessToken 授权令牌
	 * @return json对象
	 * @autor yxl
	 */
	public static JSONObject getJson(String method, String fields, String accessToken) throws Exception{
		return TbApiUtil.getJson(method, fields, null, accessToken);
	}
	
	
	/**
	 * 查询淘宝api，获取json数据
	 * @param method api方法
	 * @param fields api请求返回参数
	 * @param paramMap 查询条件
	 * @param accessToken 授权令牌
	 * @return json对象
	 * @autor yxl
	 */
	public static JSONObject getJson(String method, String fields, Map paramMap, String accessToken) throws Exception{
		return TbApiUtil.getJson(method, fields, paramMap, null, accessToken);
	}
	
	/**
	 * 查询淘宝api，获取json数据
	 * @param method api方法
	 * @param fields api请求返回参数
	 * @param paramMap 查询条件
	 * @param accessToken 授权令牌
	 * @return json对象
	 * @autor yxl
	 */
	public static JSONObject getJson(String method, String fields, Map paramMap, Map fileParams, String accessToken) throws Exception{
		
		//构造条件
		TreeMap<String, String> param = new TreeMap<String, String>();
		param.put("method", method);
		param.put("format", "json");
		param.put("session", accessToken);
		param.put("app_key", Constant.tbAppKey);
		param.put("v", "2.0");
		param.put("sign_method", "md5");
		param.put("timestamp", DateUtil.getTime());
		if(fields != null && fields.trim().length()>0) param.put("fields", fields);
		
		if(paramMap != null) param.putAll(paramMap);//添加查询条件
		
		 //生成签名
        String sign = Util.md5Signature(param, Constant.tbAppSecret);
        param.put("sign", sign);
		
		String log = "正在请求淘宝API: method=" + method + ", param=" + param;
		log = log.replaceAll("\n", "");
		logger.info(log);
		
		String responseJson = "";//返回的json字符串
		JSONObject jsonObject = null;
		try {
			responseJson = WebUtils.doPost(Constant.tbApiUrl, param, fileParams, Constant.tbConnectTimeout, Constant.tbReadTimeout);//请求服务器
			logger.info(StringUtil.clearBlank(responseJson));
			try{
				responseJson = URLDecoder.decode(responseJson, "utf-8");
			} catch(Exception e){}
			
			
			jsonObject = new JSONObject(responseJson);//转换为json对象
		} catch (IOException e) {
			//IO异常，post请求异常
			throw new MessageException("请求淘宝服务失败", method);
		} catch (JSONException e) {
			//JSON解析错误
			throw new MessageException("淘宝服务返回数据解析错误", responseJson);
		}
		
		
		//{"error_response":{"code":15,"msg":"Remote service error","sub_code":"isp.top-remote-connection-error"}}
		//是否有错误
		String errorStr = "";
		String errorCode = "";
		try{
			if(jsonObject.has("error_response")){
				JSONObject errorJson = jsonObject.getJSONObject("error_response");
				if(errorJson.has("sub_msg")) errorStr = errorJson.getString("sub_msg");
				else if(errorJson.has("msg")) errorStr = errorJson.getString("msg");
				else errorStr = errorJson.toString();
				
				errorCode = errorJson.getString("code");
			}
		} catch(Exception e){}
		if(errorStr!=null && !errorStr.equals("")) {
			//if(errorCode.equals("530")) {
			//	logger.warn("淘宝请求失败（code:"+ errorCode +"，"+ errorStr +"），正在重新发送请求...");
			//	jsonObject = getJson(method, fields, paramMap, fileParams, accessToken);
			//}
			//else 
				throw new MessageException(errorStr, "error.taobao." + errorCode);
		}
		
		return jsonObject;
	}
}
