package com.yxlisv.ec.api.impl.albb;

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
import com.yxlisv.ec.api.impl.albb.util.AlibbApiUtil;
import com.yxlisv.ec.api.impl.tb.util.TbApiUtil;
import com.yxlisv.service.AbstractBaseService;
import com.yxlisv.util.data.Page;
import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.map.MapUtil;
import com.yxlisv.util.string.JsonUtil;

/**
 * 阿里巴巴商品的api服务
 * @author yxl
 */
@Service("albbGoodsApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class GoodsApiService extends AbstractBaseService implements IGoodsApiService {

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=offer.search&v=1	<br/>
	 * offer.search 搜索产品信息 
	 */
	@Override
	public void getOnsaleList(Page page, Map<String, String[]> srMap, String orderBy, String accessToken) throws Exception {
		this.logger.info("正在获取出售中的阿里巴巴商品信息...");
		
		//查询条件
		Map paramMap = MapUtil.parse(srMap);
		paramMap = MapUtil.trim(paramMap);
		paramMap.put("pageNo", page.getPn()+"");
		paramMap.put("pageSize", page.getPageSize()+"");
		paramMap.put("memberId", paramMap.get("userId"));
		if(paramMap.get("name")!=null) paramMap.put("q", paramMap.get("name"));
		paramMap.put("returnFields", "offerId,subject,retailPrice,imageList,unitPrice,priceRanges");//自定义返回字段，字段为offerDetailInfo子集。多个字段以半角','分隔。若此字段为空，则返回offer数组信息为空
		paramMap.remove("name");
		paramMap.remove("userId");
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("offer.search", paramMap);
		
		//筛选数据
		responseJsonObject = responseJsonObject.getJSONObject("result");
		page.setTotalRows(responseJsonObject.getInt("total"));//总行数
		
		//json结果集
		if(page.getTotalRows() > 0){
			JSONArray jsonArray = responseJsonObject.getJSONArray("toReturn");
			
			//格式化数据
			List<Map> newList = new ArrayList();
			List<Map> list = JsonUtil.jsonArrayToList(jsonArray.toString());
			for(Map apiMap : list){
				Map map = new HashMap();
				map.put("id", apiMap.get("offerId"));
				map.put("title", apiMap.get("subject"));
				
				//图片
				List imgList = (ArrayList) apiMap.get("imageList");
				if(imgList!=null && imgList.size()>0){
					Map imgMap = (Map) imgList.get(0);
					map.put("img", Constant.albbPicUrl + imgMap.get("imageURI"));
					map.put("smallImg", Constant.albbPicUrl + imgMap.get("size310x310URL"));
				}
				
				//价格
				List priceList = (ArrayList) apiMap.get("priceRanges");
				if(priceList!=null && priceList.size()>0){
					Map priceMap = (Map) priceList.get(0);
					map.put("price", priceMap.get("price"));
				}
				
				map.put("url", Constant.goodsUrl.get("albb") + map.get("id") + ".html?tracelog=" + Constant.albbAppKey);
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
		this.logger.info("正在获取仓库中的阿里巴巴商品信息...");
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
		return null;
	}

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=offer.get&v=1	<br/>
	 * offer.get 获取单个产品信息 <br/>
	 * 没有找到批量获取产品的方法，先用此代替
	 * @throws Exception 
	 */
	@Override
	public List getItemDescList(String[] numIdArray, String accessToken) throws Exception {
		this.logger.info("正在批量获取商品..." );
		
		List itemList = new ArrayList();
		for(String numId : numIdArray){
			if(numId!=null && numId.length()>0){
				Map goodsMap = getGoods(numId, accessToken);
				itemList.add(goodsMap);
			}
		}
		
		return itemList;
	}

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=offer.modify.increment&v=1	<br/>
	 * offer.modify.increment 增量修改offer  
	 * @throws JSONException 
	 */
	@Override
	public String updateDesc(String id, String desc, String accessToken, String userId) throws Exception {
		this.logger.info("正在更新商品描述:" + id);
		
		//构造新的offer
		Map offerMap = new HashMap();
		offerMap.put("offerId", id);
		offerMap.put("offerDetail", desc);
		
		String offer = new JSONObject(offerMap).toString();
		Map paramMap = new HashMap();
		paramMap.put("offer", offer);
		paramMap.put("access_token", accessToken);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("offer.modify.increment", paramMap);
		return "1";
	}
	
	/**
	 * 添加描述代码（天猫）
	 * @param goods 商品
	 * @param modulesCodeStr 模板代码
	 * @param postion 位置 top/bottom
	 * @throws JSONException 
	 * @autor yxl
	 * 2014-7-21
	 */
	public void delDesc(Map goods, String modulesCodeStr, String postion, String accessToken, String userId) throws JSONException {
		JSONArray descModulesArray = new JSONArray(goods.get("desc_modules").toString());
		List<Map> descModules = JsonUtil.jsonArrayToList(descModulesArray.toString());
		int i=0, length = descModules.size();
		for(Map descModule : descModules){
			String content = descModule.get("content").toString();
			if(postion.equals("top") && i==0){
				content = modulesCodeStr + content;
				descModule.put("content", content);
			} else if(postion.equals("bottom") && i==length-1){
				content += modulesCodeStr;
				descModule.put("content", content);
			}
			i++;
		}
		JSONArray jsonArray = new JSONArray(descModules);
		
		//返回参数
		String fields = "num_iid,modified";
		
		Map paramMap = new HashMap();
		paramMap.put("num_iid", goods.get("num_iid").toString());
		paramMap.put("desc_modules", jsonArray.toString());
		
		JSONObject responseJsonObject = null;
		//请求数据
		try{
			responseJsonObject = TbApiUtil.getJson("taobao.item.update", fields, paramMap, accessToken);
		} catch(Exception e){
			if(e.getMessage().endsWith("###error.taobao.41")) throw new MessageException("该商品的描述内容过多，超过了淘宝的限制，请删减");
			else throw new MessageException(e.getMessage());
		}
	}

	/**
	 * 删除模块代码（天猫）
	 * @param goods
	 * @param id 模块ID
	 * @throws JSONException 
	 * @autor yxl
	 * 2014-7-21
	 */
	public void delDesc(Map goods, String id, String accessToken, String userId) throws JSONException {
		JSONArray descModulesArray = new JSONArray(goods.get("desc_modules").toString());
		List<Map> descModules = JsonUtil.jsonArrayToList(descModulesArray.toString());
		for(Map descModule : descModules){
			String content = descModule.get("content").toString();
			//content = ModuleDomUtil.removeModule(id, content);
			descModule.put("content", content);
		}
		JSONArray jsonArray = new JSONArray(descModules);
		
		//返回参数
		String fields = "num_iid,modified";
		
		Map paramMap = new HashMap();
		paramMap.put("num_iid", goods.get("num_iid").toString());
		paramMap.put("desc_modules", jsonArray.toString());
		
		JSONObject responseJsonObject = null;
		//请求数据
		try{
			responseJsonObject = TbApiUtil.getJson("taobao.item.update", fields, paramMap, accessToken);
		} catch(Exception e){
			if(e.getMessage().endsWith("###error.taobao.41")) throw new MessageException("该商品的描述内容过多，超过了淘宝的限制，请删减");
			else throw new MessageException(e.getMessage());
		}
	}

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=offer.get&v=1	<br/>
	 * offer.get 获取单个产品信息 <br/>
	 * @throws Exception 
	 */
	@Override
	public Map getGoods(String goodsId, String accessToken) {
		this.logger.info("正在获取商品..." );
		//返回参数
		String returnFields = "offerId,subject,offerDetail,details,detailsUrl,imageList,skuPics";//num_iid,title,desc,url,imageList
		Map paramMap = new HashMap();
		paramMap.put("offerId", goodsId);
		paramMap.put("returnFields", returnFields);
		paramMap.put("access_token", accessToken);
		
		logger.info("获取阿里巴巴平台产品信息：" + goodsId);
		Map goodsMap = null;
		try{
			//请求数据
			JSONObject responseJsonObject = AlibbApiUtil.getJson("offer.get", paramMap);
			
			goodsMap = JsonUtil.jsonToMap(responseJsonObject.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).toString());
			//转换统一格式
			goodsMap.put("id", goodsMap.get("offerId"));
			goodsMap.put("title", goodsMap.get("subject"));
			goodsMap.put("desc", goodsMap.get("details"));
			goodsMap.put("url",goodsMap.get("detailsUrl"));
			
			//图片
			List<Map> imgList = (ArrayList) goodsMap.get("imageList");
			if(imgList!=null && imgList.size()>0){
				//主图
				Map mainImgMap = imgList.get(0);
				goodsMap.put("img", mainImgMap.get("imageURI"));
				goodsMap.put("smallImg", mainImgMap.get("size310x310URL"));
				//商品图
				List goodsImgList = new ArrayList();
				for(Map imgMap : imgList){
					Map goodsImgMap = new HashMap();
					goodsImgMap.put("id", "");
					goodsImgMap.put("url", Constant.albbPicUrl + imgMap.get("imageURI"));
					goodsImgList.add(goodsImgMap);
				}
				goodsMap.put("itemImg", goodsImgList);
			}else
				goodsMap.put("img", "");
			
			//sku图片，如颜色关联的图片
			/*if(goodsMap.get("skuPics")!=null){
				String skuPics = goodsMap.get("skuPics").toString();
				skuPics = skuPics.replaceAll("img/", "'"+Constant.albbPicUrl + "img/");
				skuPics = skuPics.replaceAll("},", "'},").replaceAll("}]", "'}]");
				goodsMap.put("skuPics", skuPics);
			}*/
			
			goodsMap.remove("offerId");
			goodsMap.remove("subject");
			goodsMap.remove("details");
			goodsMap.remove("detailsUrl");
			goodsMap.remove("imageList");
		} catch(Exception e){
			logger.error(e.getMessage());
		}
		
		return goodsMap;
	}

}
