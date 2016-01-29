package com.yxlisv.ec.api;

import java.io.IOException;
import java.util.Map;

/**
 * 授权
 * @author yxl
 */
public interface IAuthorizeService {

	/** 
	 * 授权
	 * @return 授权后产生的令牌
	 * @throws IOException 
	 */
	public Map authorize(String code) throws Exception;
}