package com.yxlisv.ec.api.impl.albb.util;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.ec.api.Constant;
import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.i18n.I18nUtil;
import com.yxlisv.util.net.socket.HttpConnection;
import com.yxlisv.util.string.StringUtil;

/**
 * 阿里巴巴api工具类
 * @author yxl
 */
public class AlibbApiUtil {
	
	//定义一个全局的记录器，通过LoggerFactory获取  
	protected static Logger logger = LoggerFactory.getLogger(AlibbApiUtil.class);

	/**
	 * 查询阿里巴巴api，获取json数据
	 * @param url 链接
	 * @param paramMap 条件
	 * @param fileParams 要上传的文件
	 * @return json对象
	 * @autor yxl
	 */
	public static JSONObject sendPost(String url, Map<String, String> paramMap, Map<String, File> fileParams, int repeatCount) throws Exception{
		
		String log = "正在请求阿里巴巴API: " + url;
		log = log.replaceAll("\n", "");
		logger.info(log);
		
		JSONObject jsonObject = null;
		try{
			String responseJson = null;
			if(fileParams != null) responseJson = HttpConnection.sendPost(url, paramMap, fileParams);
			else responseJson = HttpConnection.sendPost(url, paramMap);
			logger.info(StringUtil.clearBlank(responseJson));
			try{
				responseJson = URLDecoder.decode(responseJson, "utf-8");
			} catch(Exception e){}
			try{
				jsonObject = new JSONObject(responseJson);//转换为json对象
			} catch(Exception e){}
			
			
		} catch (Exception e) {
			//IO异常，post请求异常
			throw new MessageException("请求阿里巴巴服务失败", e.getMessage()  + " - " + url);
		}
		
		
		if(jsonObject != null){
			//如果有错误
			String errorStr = "";
			String errorCode = "";
			try{
				errorCode = jsonObject.getString("error_code");
				errorStr = jsonObject.getString("error_message");
			} catch(Exception e){
				if(jsonObject.toString().contains("error")){
					errorStr = jsonObject.getJSONArray("message").getString(0);
					errorCode = jsonObject.getJSONArray("code").getString(0);
				}
			}
			if(errorCode!=null && !errorCode.equals("")) {
				String alibbError = "error.alibb."+errorCode;
				String i18nErrorStr = I18nUtil.getString(alibbError, "errors");
				if(i18nErrorStr != null && !i18nErrorStr.equals("")) errorStr = i18nErrorStr;
				throw new MessageException(errorStr, "error.alibb." + errorCode);
			}
			
			
			//另外一种错误格式
			if(jsonObject.has("result") && jsonObject.getJSONObject("result").has("success") && !jsonObject.getJSONObject("result").getBoolean("success")){
				if(jsonObject.has("code")) {
					String code = jsonObject.get("code") + "";
					code = code.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
					//一些错误码可以重新尝试
					//09008:uploadImageerror
					//090008:deleteimageerror
					String repeatErrorCode = "#09008#";
					if(repeatErrorCode.contains("#"+code+"#") && repeatCount<5){//上传图片失败
						repeatCount++;
						return sendPost(url, paramMap, fileParams, repeatCount);
					}
					String alibbError = "error.alibb."+code;
					String i18nErrorStr = I18nUtil.getString(alibbError, "errors");
					if(i18nErrorStr != null && !i18nErrorStr.equals("")) alibbError = i18nErrorStr;
					else if(jsonObject.has("message")){
						alibbError = jsonObject.get("message") + "";
						alibbError = alibbError.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
					}
					throw new MessageException(alibbError);
				}
			}
		}
		
		return jsonObject;
	}

	
	/**
	 * 查询阿里巴巴api，获取json数据
	 * @param url 链接
	 * @param uploadfile 要上传的文件
	 * @return json对象
	 * @autor yxl
	 */
	public static JSONObject sendPost(String url) throws Exception{
		
		return sendPost(url, null, null, 0);
	}
	
	/**
	 * 查询阿里巴巴api，获取json数据
	 * @param method api方法
	 * @param paramMap 查询条件
	 * @param fileParams 要上传的文件
	 * @return json对象
	 * @throws Exception 
	 * @autor yxl
	 */
	public static JSONObject getJson(String method, Map paramMap, Map<String, File> fileParams) throws Exception {
		Map checkParamMap = new HashMap();
		checkParamMap.putAll(paramMap);
		Set<Map.Entry> paramMapSet = checkParamMap.entrySet();
		for(Map.Entry entry : paramMapSet){
			String key = entry.getKey().toString();
			if(entry.getValue()==null) paramMap.remove(key);
			else if(entry.getValue().toString().trim().length()<1) paramMap.remove(key);
		}
		//请求签名
		String aopSignature = CommonUtil.signatureWithParamsAndUrlPath(Constant.albbApiUrl2 + method + "/" + Constant.albbAppKey, paramMap, Constant.albbAppSecret);//请求签名 _aop_signature
		paramMap.put("_aop_signature", aopSignature);
		
		//构造URL
		String url = Constant.albbApiUrl + Constant.albbApiUrl2 + method + "/" + Constant.albbAppKey;
		
		
		return sendPost(url, paramMap, fileParams, 0);
	}
	
	/**
	 * 查询阿里巴巴api，获取json数据
	 * @param method api方法
	 * @param paramMap 查询条件
	 * @return json对象
	 * @throws Exception 
	 * @autor yxl
	 */
	public static JSONObject getJson(String method, Map paramMap) throws Exception {
		
		return getJson(method, paramMap, null);
	}
}
