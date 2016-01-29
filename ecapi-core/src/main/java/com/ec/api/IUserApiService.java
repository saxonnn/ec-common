package com.ec.api;

import java.util.Map;

/**
 * 用户的api服务
 * @author yxl
 */
public interface IUserApiService {

	/** 
	 * 获取卖家
	 * @param accessToken 授权令牌
	 * @throws Exception 
	 */
	public Map getSeller(String userId, String accessToken) throws Exception;
}