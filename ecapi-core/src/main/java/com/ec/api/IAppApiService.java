package com.ec.api;

import java.util.Map;

import com.yxl.util.Page;

/**
 * APP的api服务
 * @author yxl
 */
public interface IAppApiService {

	/** 
	 * 获取app订单
	 * @param page 分页对象
	 * @param srMap 查询条件map
	 * @param orderBy 排序
	 * @return page.result中存放List，List中存放Map，Map中存放：{productName：产品名称，orderStartTime：下单时间，orderEndTime：到期时间，status：订单状态，money：订单金额，statusStr：订单详细状态，userId：用户ID}
	 * @throws Exception 
	 */
	public void getOrderList(Page page, Map<String, String[]> srMap, String orderBy) throws Exception;
}