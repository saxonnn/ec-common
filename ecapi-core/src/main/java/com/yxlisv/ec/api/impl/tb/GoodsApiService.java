package com.yxlisv.ec.api.impl.tb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yxlisv.ec.api.Constant;
import com.yxlisv.ec.api.IGoodsApiService;
import com.yxlisv.ec.api.impl.tb.util.TbApiUtil;
import com.yxlisv.service.AbstractBaseService;
import com.yxlisv.util.data.Page;
import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.map.MapUtil;
import com.yxlisv.util.string.JsonUtil;

/**
 * 淘宝商品的api服务
 * @author yxl
 */
@Service("tbGoodsApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class GoodsApiService extends AbstractBaseService implements IGoodsApiService {

	/**
	 * http://api.taobao.com/apidoc/api.htm?spm=0.0.0.0.6CBYa1&path=cid:4-apiId:18	<br/>
	 * taobao.items.onsale.get 获取当前会话用户出售中的商品列表
	 */
	@Override
	public void getOnsaleList(Page page, Map<String, String[]> srMap, String orderBy, String accessToken) throws Exception {
		this.logger.info("正在获取出售中的淘宝商品信息...");
		//返回参数
		String fields = "approve_status,num_iid,title,nick,type,cid,pic_url,num,props,valid_thru,list_time,price,has_discount,has_invoice,has_warranty,has_showcase,modified,delist_time,postage_id,seller_cids,outer_id";
		
		//查询条件
		Map paramMap = MapUtil.parse(srMap);
		paramMap = MapUtil.trim(paramMap);
		paramMap.put("page_no", page.getPn()+"");
		paramMap.put("page_size", page.getPageSize()+"");
		
		//请求数据
		JSONObject responseJsonObject = TbApiUtil.getJson("taobao.items.onsale.get", fields, paramMap, accessToken);
		
		//筛选数据
		responseJsonObject = responseJsonObject.getJSONObject("items_onsale_get_response");
		page.setTotalRows(responseJsonObject.getInt("total_results"));//总行数
		
		//json结果集
		if(page.getTotalRows() > 0 && responseJsonObject.has("items")){
			JSONArray jsonArray = responseJsonObject.getJSONObject("items").getJSONArray("item");
			
			//格式化数据
			List<Map> newList = new ArrayList();
			List<Map> list = JsonUtil.jsonArrayToList(jsonArray.toString());
			for(Map apiMap : list){
				Map map = new HashMap();
				map.put("id", apiMap.get("num_iid"));
				map.put("title", apiMap.get("title"));
				map.put("img", apiMap.get("pic_url"));
				map.put("smallImg", apiMap.get("pic_url"));
				map.put("price", apiMap.get("price"));
				
				map.put("url", Constant.goodsUrl.get("tb") + map.get("id"));
				newList.add(map);
			}
			
			page.setResult(newList);
		}
	}
	
	/**
	 * http://api.taobao.com/apidoc/api.htm?spm=0.0.0.0.Fvk2IS&path=cid:4-apiId:162	<br/>
	 * taobao.items.inventory.get 得到当前会话用户库存中的商品列表 
	 */
	@Override
	public void getInventoryList(Page page, Map<String, String[]> srMap, String orderBy, String accessToken) throws Exception {
		this.logger.info("正在获取仓库中的淘宝商品信息...");
		//返回参数
		String fields = "approve_status,num_iid,title,nick,type,cid,pic_url,num,props,valid_thru,list_time,price,has_discount,has_invoice,has_warranty,has_showcase,modified,delist_time,postage_id,seller_cids,outer_id";
		
		//查询条件
		Map paramMap = MapUtil.parse(srMap);
		paramMap = MapUtil.trim(paramMap);
		paramMap.put("page_no", page.getPn()+"");
		paramMap.put("page_size", page.getPageSize()+"");
		
		//请求数据
		JSONObject responseJsonObject = TbApiUtil.getJson("taobao.items.inventory.get", fields, paramMap, accessToken);
		
		//筛选数据
		responseJsonObject = responseJsonObject.getJSONObject("items_inventory_get_response");
		page.setTotalRows(responseJsonObject.getInt("total_results"));//总行数
		
		//json结果集
		if(page.getTotalRows() > 0){
			JSONArray jsonArray = responseJsonObject.getJSONObject("items").getJSONArray("item");
			page.setResult(JsonUtil.jsonArrayToList(jsonArray.toString()));
		}
	}

	/**
	 * http://api.taobao.com/apidoc/api.htm?spm=0.0.0.0.uyvtFN&path=cid:4-apiId:20	<br/>
	 * taobao.item.get 得到单个商品信息(包含属性：)
	 */
	@Override
	public List getItemImageList(String goodsId, String accessToken) throws Exception {
		this.logger.info("正在获取商品"+ goodsId +"列表图片...");
		//返回参数
		String fields = "item_img";
		
		Map paramMap = new HashMap();
		paramMap.put("num_iid", goodsId);
		
		//请求数据
		JSONObject responseJsonObject = TbApiUtil.getJson("taobao.item.get", fields, paramMap, accessToken);
		
		//筛选数据
		responseJsonObject = responseJsonObject.getJSONObject("item_get_response");
		
		//json结果集
		if(responseJsonObject.length() > 0){
			JSONArray jsonArray = responseJsonObject.getJSONObject("item").getJSONObject("item_imgs").getJSONArray("item_img");
			return JsonUtil.jsonArrayToList(jsonArray.toString());
		}
		return null;
	}

	/**
	 * http://api.taobao.com/apidoc/api.htm?spm=0.0.0.0.0lijwb&path=cid:4-apiId:315	<br/>
	 * taobao.items.list.get 批量获取商品信息
	 * @throws Exception 
	 */
	@Override
	public List getItemDescList(String[] numIdArray, String accessToken) throws Exception {
		this.logger.info("正在批量获取商品..." );
		//返回参数
		String fields = "num_iid,title,desc,detail_url,item_img.url ";//num_iid,title,desc,url,imageList
		
		String num_iids = "";//商品数字id列表，多个num_iid用逗号隔开，一次不超过20个
		for(String numId : numIdArray){
			if(numId!=null && numId.length()>0){
				Map paramMap = new HashMap();
				if(num_iids.length()>0) num_iids += ",";
				num_iids += numId;
			}
		}
		
		
		Map paramMap = new HashMap();
		paramMap.put("num_iids", num_iids);
		
		//请求数据
		JSONObject responseJsonObject = TbApiUtil.getJson("taobao.items.list.get", fields, paramMap, accessToken);
		
		//筛选数据
		responseJsonObject = responseJsonObject.getJSONObject("items_list_get_response");
		
		//json结果集
		if(responseJsonObject.length() > 0){
			JSONArray jsonArray = responseJsonObject.getJSONObject("items").getJSONArray("item");
			//格式化数据
			List<Map> newList = new ArrayList();
			List<Map> list = JsonUtil.jsonArrayToList(jsonArray.toString());
			for(Map apiMap : list){
				Map map = new HashMap();
				map.put("num_iid", apiMap.get("num_iid"));
				map.put("title", apiMap.get("title"));
				map.put("desc", apiMap.get("desc"));
				map.put("url", apiMap.get("detail_url"));
				Map imgMaps = (Map) apiMap.get("item_imgs");
				
				if(imgMaps!=null){
					List imgList = (ArrayList) imgMaps.get("item_img");
					if(imgList!=null && imgList.size()>0){
						Map imgMap = (Map) imgList.get(0);
						map.put("img", imgMap.get("url"));
					}
				}else
					map.put("img", "");
				
				newList.add(map);
			}
			return newList;
		}
		return null;
	}

	/**
	 * http://api.taobao.com/apidoc/api.htm?spm=0.0.0.0.5sHnZy&path=cid:4-apiId:21	<br/>
	 * taobao.item.update 更新商品信息
	 * @throws JSONException 
	 */
	@Override
	public String updateDesc(String id, String desc, String accessToken, String userId) throws Exception {
		this.logger.info("正在更新商品描述:" + id);
		//返回参数
		String fields = "num_iid,modified";
		
		Map paramMap = new HashMap();
		paramMap.put("num_iid", id);
		paramMap.put("desc", desc);
		
		JSONObject responseJsonObject = null;
		//请求数据
		try{
			responseJsonObject = TbApiUtil.getJson("taobao.item.update", fields, paramMap, accessToken);
		} catch(Exception e){
			if(e.getMessage().endsWith("###error.taobao.41")) throw new MessageException("该商品的描述内容过多，超过了淘宝的限制，请删减");
			throw new MessageException(e.getMessage());
		}
		return null;
	}

	/**
	 * http://open.taobao.com/apidoc/api.htm?path=scopeId:287-apiId:20 <br/>
	 * taobao.item.get 得到单个商品信息
	 * @return {id：商品ID，title：商品标题，img：主图链接，smallImg：小图链接，price：商品价格，url：商品链接, desc: 描述，itemImg：列表图片集合{url：地址}}
	 * @throws Exception 
	 * @throws JSONException 
	 */
	@Override
	public Map getGoods(String goodsId, String accessToken) throws Exception {
		this.logger.info("正在获取商品信息:" + goodsId);
		//返回参数
		String fields = "num_iid,title,pic_url,price,desc,detail_url,item_img";//num_iid,title,desc,url,imageList
		
		Map paramMap = new HashMap();
		paramMap.put("num_iid", goodsId);
		
		JSONObject responseJsonObject = null;
		//请求数据
		responseJsonObject = TbApiUtil.getJson("taobao.item.get", fields, paramMap, accessToken);
		Map apiMap = JsonUtil.jsonToMap(responseJsonObject.getJSONObject("item_get_response").getString("item"));
		Map map = new HashMap();
		map.put("id", apiMap.get("num_iid"));
		map.put("title", apiMap.get("title"));
		map.put("img", apiMap.get("pic_url"));
		map.put("smallImg", apiMap.get("pic_url"));
		map.put("price", apiMap.get("price"));
		map.put("url", apiMap.get("detail_url"));
		map.put("desc", apiMap.get("desc"));
		Map itemImgs = (Map) apiMap.get("item_imgs");
		List itemimgList = null;
		if(itemImgs!=null && itemImgs.get("item_img")!=null) itemimgList = (List) itemImgs.get("item_img");
		map.put("itemImg", itemimgList);
		return map;
	}
}