package com.ec.api;

import java.util.List;
import java.util.Map;

/**
 * 店铺的api服务
 * @author yxl
 */
public interface IShopApiService {

	/**
	 * 获取卖家店铺的商品类目
	 * @param nick 昵称
	 * @param accessToken 授权令牌
	 * @throws Exception
	 * @autor yxl
	 */
	public List<Map> getCategory(String nick, String accessToken) throws Exception;
	
	
	/**
	 * 格式化商品类目给select标签使用
	 * @autor yxl
	 */
	public List<Map> formatCategoryForSelect(List<Map> categoryList);
}
