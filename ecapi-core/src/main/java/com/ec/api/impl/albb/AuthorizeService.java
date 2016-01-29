package com.ec.api.impl.albb;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ec.api.Constant;
import com.ec.api.IAuthorizeService;
import com.ec.api.impl.albb.util.AlibbApiUtil;
import com.yxl.service.AbstractBaseService;
import com.yxl.util.string.JsonUtil;

/**
 * 阿里巴巴授权
 * @author yxl
 */
@Service("albbAuthorizeService")
@Transactional(propagation=Propagation.REQUIRED)
public class AuthorizeService extends AbstractBaseService implements IAuthorizeService{
	
	@Override
	public Map authorize(String code) throws Exception {
		this.logger.info("阿里巴巴授权...");
		if(code==null || code.trim().equals("")) {
			this.logger.error("code 为空，授权失败！");
			return null;
		}
		//获取访问令牌
		JSONObject tokenJson = AlibbApiUtil.sendPost(Constant.albbOAuthTokenUrl + "&code=" + code);
		Map tokenMap = JsonUtil.jsonToMap(tokenJson.toString());
		this.logger.info("获取访问令牌成功：" + tokenMap);
		
		return tokenMap;
	}
}
