package com.yxlisv.ec.api.impl.albb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yxlisv.ec.api.Constant;
import com.yxlisv.ec.api.IAppApiService;
import com.yxlisv.ec.api.impl.albb.util.AlibbApiUtil;
import com.yxlisv.service.AbstractBaseService;
import com.yxlisv.util.data.Page;
import com.yxlisv.util.date.DateUtil;
import com.yxlisv.util.map.MapUtil;
import com.yxlisv.util.string.JsonUtil;

/**
 * 阿里巴巴App API服务
 * @author yxl
 */
@Service("albbAppApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class AppApiService extends AbstractBaseService implements IAppApiService {

	/**
	 * ISV获取自己名下的应用最近一个月的订购的订单信息列表
	 * app.order.get -- version: 1 
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=app.order.get&v=1
	 */
	@Override
	public void getOrderList(Page page, Map<String, String[]> srMap, String orderBy) throws Exception {
		//查询条件
		Map paramMap = MapUtil.parse(srMap);
		paramMap = MapUtil.trim(paramMap);
		paramMap.put("startIndex", page.getPn()+"");
		paramMap.put("pageSize", page.getPageSize()+"");
		
		long gmtCreateLong = System.currentTimeMillis();
		String gmtCreate = "";
		if(paramMap.get("orderStartTime")!=null) {
			gmtCreateLong = DateUtil.toLong(paramMap.get("orderStartTime"));
		} else gmtCreateLong += 1000*60*60*24;
		gmtCreate += DateUtil.toYear(gmtCreateLong);
		gmtCreate += DateUtil.toMonth(gmtCreateLong);
		gmtCreate += DateUtil.toDay(gmtCreateLong);
		gmtCreate += "000000000+0800";
		paramMap.put("gmtCreate", gmtCreate);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("app.order.get", paramMap);
		
		page.setTotalRows(1000);//总行数
		
		//json结果集
		if(page.getTotalRows() > 0){
			JSONArray jsonArray = responseJsonObject.getJSONArray("returnValue");
			
			//格式化数据
			List<Map> newList = new ArrayList();
			List<Map> list = JsonUtil.jsonArrayToList(jsonArray.toString());
			for(Map apiMap : list){
				//Map中存放：{productName：产品名称，orderStartTime：下单时间，orderEndTime：到期时间，status：订单状态，money：订单金额，statusStr：订单详细状态，userId：用户ID}
				Map map = new HashMap();
				map.put("userId", apiMap.get("memberId"));
				map.put("productName", apiMap.get("productName"));
				map.put("orderStartTime", apiMap.get("gmtCreate"));
				map.put("orderEndTime", apiMap.get("gmtServiceEnd"));
				map.put("status", apiMap.get("bizStatus"));
				//阿里巴巴 B:服务前，S:服务中，P：挂起，E：关闭，C:作废
				//APP     1：已支付，2：未支付，3：取消，4：异常
				String status = map.get("status").toString();
				if(status.equals("S")) status = "1";
				else if(status.equals("B")) status = "2";
				else if(status.equals("E") || status.equals("C")) status = "3";
				else status = "4";
				map.put("status", status);
				
				map.put("money", apiMap.get("executePrice"));
				map.put("statusStr", apiMap.get("bizStatusExt"));
				
				//格式化时间
				map.put("orderStartTime", DateUtil.toLong(map.get("orderStartTime")));
				map.put("orderEndTime", DateUtil.toLong(map.get("orderEndTime")));
				
				newList.add(map);
			}
			page.setResult(newList);
		}
	}
}
