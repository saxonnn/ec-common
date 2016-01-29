package com.yxlisv.ec.api;

import java.util.List;
import java.util.Map;

import com.yxlisv.util.Page;

/**
 * 商品的api服务
 * @author yxl
 */
public interface IGoodsApiService {

	/** 
	 * 获取出售中的商品
	 * @param page 分页对象
	 * @param srMap 查询条件map
	 * @param orderBy 排序
	 * @param accessToken 授权令牌
	 * @return page.result中存放List，List中存放Map，Map中存放：{id：商品ID，title：商品标题，img：主图链接，smallImg：小图链接，price：商品价格，url：商品链接}
	 * @throws Exception 
	 */
	public void getOnsaleList(Page page, Map<String, String[]> srMap, String orderBy, String accessToken) throws Exception;
	
	/** 
	 * 获取仓库中的商品
	 * @param page 分页对象
	 * @param srMap 查询条件map
	 * @param orderBy 排序，eg：list_time:desc；排序方式。格式为column:asc/desc ，column可选值:list_time(上架时间),delist_time(下架时间),num(商品数量)，modified(最近修改时间);默认上架时间降序(即最新上架排在前面)。如按照上架时间降序排序方式为list_time:desc
	 * @param accessToken 授权令牌
	 * @throws Exception 
	 */
	public void getInventoryList(Page page, Map<String, String[]> srMap, String orderBy, String accessToken) throws Exception;

	/**
	 * 获取商品图片列表(包括主图)
	 * @param goodsId 商品ID
	 * @param accessToken 授权令牌
	 * @autor yxl
	 */
	public List getItemImageList(String goodsId, String accessToken) throws Exception;

	/**
	 * 批量获取商品
	 * @param numIdArray 商品id集合
	 * @param accessToken 授权令牌
	 * @return 商品集合，但是只包含描述相关的字段{num_iid,title,desc}
	 * @autor yxl
	 */
	public List getItemDescList(String[] numIdArray, String accessToken) throws Exception;

	/**
	 * 更新商品描述
	 * @param id 商品id
	 * @param desc 描述
	 * @param accessToken 授权令牌
	 * @param userId 用户ID
	 * @autor yxl
	 */
	public String updateDesc(String id, String desc, String accessToken, String userId) throws Exception;

	/**
	 * 获取一个商品
	 * @param id 商品id
	 * @param accessToken 授权令牌
	 * @return 商品，包含的字段{id：商品ID，title：商品标题，img：主图链接，smallImg：小图链接，price：商品价格，url：商品链接, desc: 描述，itemImg：列表图片集合{url：地址}}
	 * @autor yxl
	 */
	public Map getGoods(String goodsId, String accessToken) throws Exception;
	
}