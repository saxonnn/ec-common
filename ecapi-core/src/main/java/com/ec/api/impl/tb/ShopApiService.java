package com.ec.api.impl.tb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ec.api.IShopApiService;
import com.ec.api.impl.tb.util.TbApiUtil;
import com.yxl.service.AbstractBaseService;
import com.yxl.util.string.JsonUtil;

/**
 * 淘宝店铺的api服务
 * @author yxl
 */
@Service("tbShopApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class ShopApiService extends AbstractBaseService implements IShopApiService {

	/**
	 * http://api.taobao.com/apidoc/api.htm?spm=0.0.0.0.cwhxkv&path=cid:9-apiId:65	<br/>
	 * taobao.sellercats.list.get 获取前台展示的店铺内卖家自定义商品类目
	 */
	@Override
	public List<Map> getCategory(String nick, String accessToken) throws Exception {
		this.logger.info("正在店铺的类目信息...");
		
		//查询条件
		Map paramMap = new HashMap();
		paramMap.put("nick", nick);
		
		//请求数据
		JSONObject responseJsonObject = TbApiUtil.getJson("taobao.sellercats.list.get", null, paramMap, accessToken);
		
		//筛选数据
		responseJsonObject = responseJsonObject.getJSONObject("sellercats_list_get_response");
		if(responseJsonObject.has("seller_cats")){//如果有返回数据
			JSONArray jsonArray = responseJsonObject.getJSONObject("seller_cats").getJSONArray("seller_cat");
			return JsonUtil.jsonArrayToList(jsonArray.toString());
		}
		return null;
	}

	@Override
	public List<Map> formatCategoryForSelect(List<Map> categoryList) {
		
		if(categoryList == null) return null;
		//把顶层节点先挑选出来,parent_cid = 0
		List<Map> srcList = categoryList;//原始节点
		for(Map map : categoryList){
			if(map.get("parent_cid").toString().equals("0")) {
				map.put("deep", 0);
				srcList = formatCategoryForSelect(srcList, map);
			}
		}
		
		return srcList;
	}
	
	/**
	 * 格式化商品类目给select标签使用
	 * @param srcList 源数据
	 * @param currentMap 当前对象
	 * @autor yxl
	 */
	private List<Map> formatCategoryForSelect(List<Map> srcList, Map currentMap) {
		
		int deep = (Integer) currentMap.get("deep");//当前节点的深度
		String cid = currentMap.get("cid").toString();//当前节点的ID
		
		//查找子节点
		List<Map> childList = new ArrayList();
		List<Map> newSrcList = new ArrayList();//新的源节点
		for(Map childMap : srcList){
			if(childMap.get("parent_cid").toString().equals(cid)) {
				childMap.put("deep", deep + 1);
				childList.add(childMap);
			} else newSrcList.add(childMap);
		}
		
		if(newSrcList.indexOf(currentMap) != -1 && childList.size()>0)
			newSrcList.addAll(newSrcList.indexOf(currentMap) + 1, childList);//把子节点添加到源节点
		
		//去查找所有的子节点的下级节点
		for(Map childMap : childList){
			newSrcList = formatCategoryForSelect(newSrcList, childMap);
		}
		
		return newSrcList;
	}
	
	
	public static void main(String[] args) {
		
		List<Map> srcList = new ArrayList();
		
		Map map = new HashMap();
		map.put("parent_cid", 0);
		map.put("name", "服装");
		map.put("cid", 1);
		srcList.add(map);
		
		map = new HashMap();
		map.put("parent_cid", 6);
		map.put("name", "蓝瑟");
		map.put("cid", 9);
		srcList.add(map);
		
		map = new HashMap();
		map.put("parent_cid", 6);
		map.put("name", "翼豪陆神");
		map.put("cid", 10);
		srcList.add(map);
		
		map = new HashMap();
		map.put("parent_cid", 1);
		map.put("name", "女装上衣");
		map.put("cid", 2);
		srcList.add(map);
		
		map = new HashMap();
		map.put("parent_cid", 1);
		map.put("name", "裙子");
		map.put("cid", 3);
		srcList.add(map);
		
		map = new HashMap();
		map.put("parent_cid", 0);
		map.put("name", "汽车");
		map.put("cid", 4);
		srcList.add(map);
		
		map = new HashMap();
		map.put("parent_cid", 4);
		map.put("name", "大众");
		map.put("cid", 5);
		srcList.add(map);
		
		map = new HashMap();
		map.put("parent_cid", 4);
		map.put("name", "三菱");
		map.put("cid", 6);
		srcList.add(map);
		
		map = new HashMap();
		map.put("parent_cid", 8);
		map.put("name", "透明小披肩");
		map.put("cid", 11);
		srcList.add(map);
		
		
		map = new HashMap();
		map.put("parent_cid", 2);
		map.put("name", "吊带");
		map.put("cid", 7);
		srcList.add(map);
		
		map = new HashMap();
		map.put("parent_cid", 2);
		map.put("name", "小披肩");
		map.put("cid", 8);
		srcList.add(map);
		
		
		ShopApiService shopApiService = new ShopApiService();
		srcList = shopApiService.formatCategoryForSelect(srcList);
		for(Map mapObj : srcList){
			for(int i=1; i<=(Integer)mapObj.get("deep"); i++) System.out.print("  ");
			System.out.println(mapObj.get("name") + " : " + mapObj.get("deep"));
		}
	}

}
