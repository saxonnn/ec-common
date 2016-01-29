package com.ec.api.impl.albb;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ec.api.IUserApiService;
import com.ec.api.impl.albb.util.AlibbApiUtil;
import com.yxl.service.AbstractBaseService;
import com.yxl.util.string.JsonUtil;

/**
 * 阿里巴巴用户API服务
 * @author yxl
 */
@Service("albbUserApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class UserApiService extends AbstractBaseService implements IUserApiService {

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=member.get&v=1	<br/>
	 * member.get 获取单个阿里巴阿中国网站会员信息。非会员本人只返回非隐私数据 
	 */
	@Override
	public Map getSeller(String userId, String accessToken) throws Exception {
		
		this.logger.info("正在获取阿里巴巴卖家信息...");
		
		//查询条件
		Map paramMap = new HashMap();
		paramMap.put("memberId", userId);
		paramMap.put("access_token", accessToken);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("member.get", paramMap);
		//筛选数据
		String userJson = responseJsonObject.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).toString();
		
		Map userMap = JsonUtil.jsonToMap(userJson);
		//统一各种平台的字段
		userMap.put("user_id", userMap.get("memberId"));
		userMap.put("uid", userMap.get("loginId"));
		userMap.put("nick", userMap.get("sellerName"));
		userMap.put("type", "ALBB");
		if(userMap.get("sex").toString().contains("女")) userMap.put("sex", "f");
		else userMap.put("sex", "m");
		
		return userMap;
	}
}
