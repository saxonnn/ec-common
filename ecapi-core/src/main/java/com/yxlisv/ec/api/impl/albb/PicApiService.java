package com.yxlisv.ec.api.impl.albb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yxlisv.ec.api.Constant;
import com.yxlisv.ec.api.IPicApiService;
import com.yxlisv.ec.api.impl.albb.util.AlibbApiUtil;
import com.yxlisv.service.AbstractBaseService;
import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.file.FilePathUtil;
import com.yxlisv.util.file.FileUtil;
import com.yxlisv.util.math.NumberUtil;
import com.yxlisv.util.string.JsonUtil;

/**
 * 阿里巴巴图片空间的api服务
 * @author yxl
 */
@Service("albbPicApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class PicApiService extends AbstractBaseService implements IPicApiService {
	
	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=ibank.image.upload&v=1	<br/>
	 * ibank.image.upload  上传单张图片
	 */
	@Override
	public Map upload(String albumId, String name, String imagePath, String accessToken, String userId) throws Exception {
		Map resultMap = new HashMap();
		//条件
		Map paramMap = new HashMap();
		paramMap.put("access_token", accessToken);
		paramMap.put("albumId", albumId);
		paramMap.put("name", name);
		
		try{
			File file = new File(imagePath);
			if(file.exists()){
				Map fileMap = new HashMap();
				fileMap.put("imageBytes", file);
				
				//请求数据
				JSONObject responseJsonObject = AlibbApiUtil.getJson("ibank.image.upload", paramMap, fileMap);
				
				//筛选数据
				Map picMap = JsonUtil.jsonToMap(responseJsonObject.getJSONObject("result").getJSONArray("toReturn").get(0));
				resultMap.put("id", picMap.get("id"));
				resultMap.put("path", Constant.albbPicUrl + picMap.get("url"));
			}
		} catch(Exception e){
			logger.error("上传图片失败", e);
			throw new MessageException(e.getMessage());
		}
		return resultMap;
	}

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=ibank.image.upload&v=1	<br/>
	 * ibank.image.upload  上传单张图片
	 */
	@Override
	public Map upload(String albumId, String name, List<String> imageList, String accessToken, String userId) throws Exception {
		
		Map resultMap = new HashMap();
		for(String imageUrl : imageList){
			if(imageUrl.indexOf("aliimg") == -1){//如果该图片不属于阿里巴巴，就上传
				//条件
				Map paramMap = new HashMap();
				paramMap.put("access_token", accessToken);
				paramMap.put("albumId", albumId);
				paramMap.put("name", name);
				
				String filePath = FilePathUtil.getWebRoot() + "/WEB-INF/temp/" + NumberUtil.getRandomStr() + FilePathUtil.getSuffix(imageUrl);
				try{
					String path= FilePathUtil.getWebRoot() + "/WEB-INF/temp/";
					new File(path).mkdirs();
					FileUtil.download(imageUrl, filePath);
					File file = new File(filePath);
					if(file.exists()){
						Map fileMap = new HashMap();
						fileMap.put("imageBytes", file);
						
						//请求数据
						JSONObject responseJsonObject = AlibbApiUtil.getJson("ibank.image.upload", paramMap, fileMap);
						
						//筛选数据
						String imgUrl = responseJsonObject.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).getString("url");
						imgUrl = Constant.albbPicUrl + imgUrl;
						resultMap.put(imageUrl, imgUrl);
						file.delete();
					}
				} catch(Exception e){
					throw new MessageException(e.getMessage());
				}
			}
		}
		return resultMap;
	}

	
	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=ibank.album.create&v=1	<br/>
	 * ibank.album.create  本接口实现创建相册功能 
	 */
	@Override
	public String createAlbum(String name, String authority, String description, String password, String accessToken, String userId) throws Exception {
		
		//条件
		Map paramMap = new HashMap();
		paramMap.put("name", name);
		paramMap.put("access_token", accessToken);
		if(authority == null) paramMap.put("authority", "1");
		else paramMap.put("authority", authority);
		if(description != null) paramMap.put("description", description);
		if(password != null && password.trim().length()>0) paramMap.put("password", password);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("ibank.album.create", paramMap);
		
		//筛选数据
		return responseJsonObject.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0).getString("albumId");
	}


	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=ibank.album.get&v=1
	 * ibank.album.get 本接口实现根据相册id获取相册信息 
	 */
	@Override
	public boolean hasAlbum(String albumId, String accessToken) throws Exception {
		if(albumId==null) return false;
		//条件
		Map paramMap = new HashMap();
		paramMap.put("access_token", accessToken);
		paramMap.put("albumId", albumId);
		
		//请求数据
		try {
			JSONObject responseJsonObject = AlibbApiUtil.getJson("ibank.album.get", paramMap);
			if(responseJsonObject.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0)!=null) return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	
	@Override
	public void delete(List imgIdList, String accessToken) throws Exception {
		delete(imgIdList, accessToken, 0);
	}
	
	/**
	 * 删除服务器上的图片
	 * @throws Exception 
	 */
	private void delete(List imgIdList, String accessToken, int count) throws Exception{
		int max = 100;//一次最多删除多少个
		String imgIds = "";
		int end = (count+1)*max;
		if(end>imgIdList.size()) end = imgIdList.size();
		for(int i=count*max; i<end; i++){
			imgIds += ";" + imgIdList.get(i);
		}
		if(imgIds.length()<1) return;
		if(imgIds.startsWith(";")) imgIds = imgIds.substring(1);
		count++;
		delete(imgIds, accessToken);
		if(end<imgIdList.size()) delete(imgIdList, accessToken, count);
	}

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=ibank.image.deleteByIds&v=1
	 * ibank.image.deleteByIds -- version: 1 
	 * @throws Exception 
	 */
	@Override
	public void delete(String imgId, String accessToken) throws Exception {
		if(imgId==null) return;
		if(imgId.trim().length()<1) return;
		//条件
		Map paramMap = new HashMap();
		paramMap.put("imageIds", imgId);
		paramMap.put("access_token", accessToken);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("ibank.image.deleteByIds", paramMap);
	}

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=offer.modify.increment&v=1
	 * offer.modify.increment -- version: 1 
	 * 增量修改offer（该接口只支持价格和标题的增量修改！请慎用！） 
	 * imageUriList 图片地址
	 * ["http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg"]
	 */
	@Override
	public Map uploadGoodsImage(String albumId, String name, Map goods, String imgPath, boolean isMain, String accessToken) throws Exception {
		List<Map> goodsImgList = (List<Map>) goods.get("itemImg");
		List imgUrlList = new ArrayList();
		for(Map goodsImg : goodsImgList){
			imgUrlList.add(goodsImg.get("url").toString());
		}
		Map newImg = upload(albumId, name, imgPath, accessToken, "");
		if(isMain) imgUrlList.add(0, newImg.get("path"));
		else imgUrlList.add(newImg.get("path"));
		
		String imgUrlJson = imgUrlList.toString();
		imgUrlJson = imgUrlJson.replace("[", "[\"");
		imgUrlJson = imgUrlJson.replace("]", "\"]");
		imgUrlJson = imgUrlJson.replace(", ", "\",\"");
		
		//增量更新产品中的图片信息
		Map offerMap = new HashMap();
		offerMap.put("offerId", goods.get("id").toString());
		offerMap.put("imageUriList", imgUrlJson);
		
		String offer = new JSONObject(offerMap).toString();
		Map paramMap = new HashMap();
		paramMap.put("offer", offer);
		paramMap.put("access_token", accessToken);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("offer.modify.increment", paramMap);
		return newImg;
	}
	
	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=offer.modify.increment&v=1
	 * offer.modify.increment -- version: 1 
	 * 增量修改offer（该接口只支持价格和标题的增量修改！请慎用！） 
	 * imageUriList 图片地址
	 * ["http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg"]
	 */
	@Override
	public Map replaceGoodsImage(String albumId, String name, Map goods, String id, String oldImgUrl, String newImgUrl, boolean isMain, String accessToken) throws Exception {
		delete(id, accessToken);
		List<Map> goodsImgList = (List<Map>) goods.get("itemImg");
		List imgUrlList = new ArrayList();
		for(Map goodsImg : goodsImgList){
			imgUrlList.add(goodsImg.get("url").toString());
		}
		
		String imgUrlJson = imgUrlList.toString();
		imgUrlJson = imgUrlJson.replace("[", "[\"");
		imgUrlJson = imgUrlJson.replace("]", "\"]");
		imgUrlJson = imgUrlJson.replace(", ", "\",\"");
		imgUrlJson = imgUrlJson.replace(oldImgUrl, newImgUrl);
		
		//增量更新产品中的图片信息
		Map offerMap = new HashMap();
		offerMap.put("offerId", goods.get("id").toString());
		offerMap.put("imageUriList", imgUrlJson);
		
		String offer = new JSONObject(offerMap).toString();
		Map paramMap = new HashMap();
		paramMap.put("offer", offer);
		paramMap.put("access_token", accessToken);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("offer.modify.increment", paramMap);
		Map resultMap = new HashMap();
		resultMap.put("id", "");
		resultMap.put("path", newImgUrl);
		return resultMap;
	}
	
	
	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=offer.modify.increment&v=1
	 * offer.modify.increment -- version: 1 
	 * 增量修改offer（该接口只支持价格和标题的增量修改！请慎用！） 
	 * imageUriList 图片地址
	 * ["http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg"]
	 */
	@Override
	public Map replaceGoodsImage2(String albumId, String name, Map goods, String id, String oldImgUrl, String imgPath, boolean isMain, String accessToken) throws Exception {
		Map newImageMap = upload(albumId, name, imgPath, accessToken, null);
		replaceGoodsImage(albumId, name, goods, id, oldImgUrl, newImageMap.get("path").toString(), isMain, accessToken);
		return newImageMap;
	}
	
	
	@Override
	public void replaceAllGoodsImage(String albumid, String name, String goodsId, List<String> oldImgIdList, List<String> newImgUrlList, String accessToken) throws Exception {
		
		String imgUrlJson = newImgUrlList.toString();
		imgUrlJson = imgUrlJson.replace("[", "[\"");
		imgUrlJson = imgUrlJson.replace("]", "\"]");
		imgUrlJson = imgUrlJson.replace(", ", "\",\"");
		
		//增量更新产品中的图片信息
		Map offerMap = new HashMap();
		offerMap.put("offerId", goodsId);
		offerMap.put("imageUriList", imgUrlJson);
		
		String offer = new JSONObject(offerMap).toString();
		Map paramMap = new HashMap();
		paramMap.put("offer", offer);
		paramMap.put("access_token", accessToken);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("offer.modify.increment", paramMap);
		
		//尝试删除旧图片
		delete(oldImgIdList, accessToken);
	}

	@Override
	public boolean deleteItemImg(String goodsId, String imgId, String accessToken) throws Exception {
		delete(imgId);
		return true;
	}

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=offer.modify.increment&v=1
	 * offer.modify.increment -- version: 1 
	 * 增量修改offer（该接口只支持价格和标题的增量修改！请慎用！） 
	 * imageUriList 图片地址
	 * ["http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg"]
	 */
	@Override
	public List uploadGoodsImage(String albumId, String name, String goodsId, List<String> imgPathList, String accessToken) throws Exception {
		List imgMapList = new ArrayList();
		List imgUrlList = new ArrayList();
		//上传图片
		for(String imgPath : imgPathList){
			Map imgMap = upload(albumId, name, imgPath, accessToken, "");
			imgUrlList.add(imgMap.get("path").toString());
			imgMapList.add(imgMap);
		}
		String imgUrlJson = imgUrlList.toString();
		imgUrlJson = imgUrlJson.replace("[", "[\"");
		imgUrlJson = imgUrlJson.replace("]", "\"]");
		imgUrlJson = imgUrlJson.replace(", ", "\",\"");
		
		//增量更新产品中的图片信息
		Map offerMap = new HashMap();
		offerMap.put("offerId", goodsId);
		offerMap.put("imageUriList", imgUrlJson);
		
		String offer = new JSONObject(offerMap).toString();
		Map paramMap = new HashMap();
		paramMap.put("offer", offer);
		paramMap.put("access_token", accessToken);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("offer.modify.increment", paramMap);
				
		return imgMapList;
	}

	
	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=offer.modify.increment&v=1
	 * offer.modify.increment -- version: 1 
	 * 增量修改offer（该接口只支持价格和标题的增量修改！请慎用！） 
	 * imageUriList 图片地址
	 * ["http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg","http://img.china.alibaba.com/img/ibank/2011/736/195/418591637_1146240514.jpg"]
	 * @throws Exception 
	 */
	@Override
	public void updateSkuImg(String goodsId, String newSkuStr, String accessToken) throws Exception {
		//构造新的offer
		Map offerMap = new HashMap();
		offerMap.put("offerId", goodsId);
		//newSkuStr = newSkuStr.replaceAll("'", "").replaceAll(Constant.albbPicUrl, "");
		offerMap.put("skuPics", newSkuStr);
		
		String offer = new JSONObject(offerMap).toString();
		Map paramMap = new HashMap();
		paramMap.put("offer", offer);
		paramMap.put("access_token", accessToken);
		
		//请求数据
		JSONObject responseJsonObject = AlibbApiUtil.getJson("offer.modify.increment", paramMap);
	}
}