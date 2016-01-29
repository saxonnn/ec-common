package com.yxlisv.ec.api;

import java.util.List;
import java.util.Map;

/**
 * 图片的api服务
 * @author yxl
 */
public interface IPicApiService {
	
	/** 
	 * 上传图片到图片空间
	 * @param albumid 相册ID
	 * @param name 图片名称
	 * @param imagePath 图片路径
	 * @param accessToken 授权令牌
	 * @param userId 用户ID
	 * @return Map{id，path}
	 * @throws Exception 
	 */
	public Map upload(String albumid, String name, String imagePath, String accessToken, String userId) throws Exception;

	/** 
	 * 上传图片到图片空间
	 * @param albumid 相册ID
	 *  @param name 图片名称
	 * @param imageList 图片List集合
	 * @param accessToken 授权令牌
	 * @param userId 用户ID
	 * @return Map{旧图片地址，新地址......}
	 * @throws Exception 
	 */
	public Map upload(String albumid, String name, List<String> imageList, String accessToken, String userId) throws Exception;
	
	/** 
	 * 创建相册
	 * @param name 相册名称
	 * @param authority 相册访问权限。取值范围:0-不公开；1-公开；2-密码访问
	 * @param description 相册描述
	 * @param password 相册访问密码
	 * @param accessToken 授权令牌
	 * @param userId 用户ID
	 * @return 相册ID
	 * @throws Exception 
	 */
	public String createAlbum(String name, String authority, String description, String password, String accessToken, String userId) throws Exception;
	
	/** 
	 * 判断是否有相册
	 * @param ablumId 相册ID
	 * @param accessToken 授权令牌
	 * @return 是否存在相册
	 * @throws Exception 
	 */
	public boolean hasAlbum(String ablumId, String accessToken) throws Exception;

	/**
	 * 删除服务器上的图片
	 * @param imgIdList 图片id集合
	 * @param accessToken  授权令牌
	 */
	public void delete(List imgIdList, String accessToken) throws Exception;
	
	
	/**
	 * 删除服务器上的图片
	 * @param imgId 图片id
	 * @param accessToken  授权令牌
	 */
	public void delete(String imgId, String accessToken) throws Exception;
	
	/**
	 * 批量上传商品图片
	 * @param albumid 相册ID
	 * @param name 图片名称
	 * @param goodsId 商品id
	 * @param imgPathList 图片路径集合，第一张图为商品主图
	 * @param accessToken  授权令牌
	 * @return List[Map{id，path},...]
	 */
	public List uploadGoodsImage(String albumid, String name, String goodsId, List<String> imgPathList, String accessToken) throws Exception;

	
	/**
	 * 上传商品图片
	 * @param albumid 相册ID
	 *  @param name 图片名称
	 * @param goods 商品
	 * @param imgPath 图片路径
	 * @param isMain 是否为主图
	 * @param accessToken  授权令牌
	 * @return Map{id，path}
	 */
	public Map uploadGoodsImage(String albumid, String name, Map goods, String imgPath, boolean isMain, String accessToken) throws Exception;
	
	/**
	 * 替换商品图片
	 * @param albumid 相册ID
	 *  @param name 图片名称
	 * @param goods 商品
	 * @param imgId 图片ID
	 * @param oldImgUrl 旧图片路径
	 * @param newImgUrl 新图片路径
	 * @param isMain 是否为主图
	 * @param accessToken  授权令牌
	 * @return Map{id，path}
	 */
	public Map replaceGoodsImage(String albumid, String name, Map goods, String id, String oldImgUrl, String newImgUrl, boolean isMain, String accessToken) throws Exception;
	
	/**
	 * 替换商品图片
	 * @param albumid 相册ID
	 * @param name 图片名称
	 * @param goods 商品
	 * @param imgId 图片ID
	 * @param oldImgUrl 旧图片路径
	 * @param imgPath 新图片路径(本地图片)
	 * @param isMain 是否为主图
	 * @param accessToken  授权令牌
	 * @return Map{id，path}
	 */
	public Map replaceGoodsImage2(String albumid, String name, Map goods, String imgId, String oldImgUrl, String imgPath, boolean isMain, String accessToken) throws Exception;
	
	/**
	 * 替换所有商品图片
	 * @param albumid 相册ID
	 * @param name 图片名称
	 * @param goodsId 商品Id
	 * @param oldImgIdList 旧图片ID集合
	 * @param oldImgUrl 旧图片地址集合
	 * @param newImgUrlList 新图片地址集合
	 * @param accessToken  授权令牌
	 */
	public void replaceAllGoodsImage(String albumid, String name, String goodsId, List<String> oldImgIdList, List<String> newImgUrlList, String accessToken) throws Exception;

	/**
	 * 删除商品图片
	 * @param goodsId 商品id
	 * @param imgId 图片id
	 * @param url 图片url
	 * @param accessToken  授权令牌
	 */
	public boolean deleteItemImg(String goodsId, String imgId, String accessToken) throws Exception;

	/**
	 * 更新sku图片
	 * @param goodsId 商品ID
	 * @param newSkuStr 新的sku字符串
	 * @param accessToken  授权令牌
	 */
	public void updateSkuImg(String goodsId, String newSkuStr, String accessToken) throws Exception;
}