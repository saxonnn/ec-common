package com.yxlisv.ec.api.impl.tb;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yxlisv.ec.api.Constant;
import com.yxlisv.ec.api.IAuthorizeService;
import com.taobao.api.internal.util.WebUtils;
import com.yxlisv.service.AbstractBaseService;
import com.yxlisv.util.string.JsonUtil;

/**
 * 淘宝授权
 * @author yxl
 */
@Service("tbAuthorizeService")
@Transactional(propagation=Propagation.REQUIRED)
public class AuthorizeService extends AbstractBaseService implements IAuthorizeService{
	
	@Override
	public Map authorize(String code) throws Exception {
		this.logger.info("淘宝授权...");
		if(code==null || code.trim().equals("")) {
			this.logger.error("code 为空，授权失败！");
			return null;
		}
		//用上一步获取的Code和应用密钥（AppSecret）通过Https Post方式换取Token
		Map params = new HashMap();
		params.put("client_id", Constant.tbAppKey);//appkey的值
		params.put("client_secret", Constant.tbAppSecret);//appsecret
		params.put("grant_type", "authorization_code");
		params.put("code", code);//授权码
		params.put("redirect_uri", Constant.appAddr + "/taobao/token");//怎么没有给我跳转

		//获取访问令牌
		String tokenStr = "";
		tokenStr = WebUtils.doPost(Constant.tbOAuthTokenUrl, params, Constant.tbConnectTimeout, Constant.tbReadTimeout);
		tokenStr = URLDecoder.decode(tokenStr, "utf-8");
		Map tokenMap = JsonUtil.jsonToMap(tokenStr);
		this.logger.info("获取访问令牌成功：" + tokenMap);
		
		return tokenMap;
	}
}
