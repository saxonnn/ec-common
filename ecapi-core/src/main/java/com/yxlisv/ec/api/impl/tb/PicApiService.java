package com.yxlisv.ec.api.impl.tb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yxlisv.ec.api.IPicApiService;
import com.yxlisv.ec.api.impl.tb.util.TbApiUtil;
import com.taobao.api.FileItem;
import com.yxlisv.service.AbstractBaseService;
import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.exception.SimpleMessageException;
import com.yxlisv.util.file.FilePathUtil;
import com.yxlisv.util.file.FileUtil;
import com.yxlisv.util.math.NumberUtil;
import com.yxlisv.util.string.JsonUtil;

/**
 * 淘宝图片空间的api服务
 * @author yxl
 */
@Service("tbPicApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class PicApiService extends AbstractBaseService implements IPicApiService {
	
	/**
	 * http://api.taobao.com/apidoc/api.htm?path=cid:10122-apiId:140	<br/>
	 * taobao.picture.upload 上传单张图片
	 */
	@Override
	public Map upload(String albumid, String name, String imagePath, String accessToken, String userId) throws Exception {

		byte[] imageData = FileUtil.readByte(imagePath);
		Map resultMap = new HashMap();
		//开始上传图片
		if(imageData != null){
			//条件
			Map paramMap = new HashMap();
			paramMap.put("picture_category_id", albumid);//图片分类ID，设置具体某个分类ID或设置0上传到默认分类，只能传入一个分类 
			paramMap.put("title", name);
			
			//文件
			String fileName = name + ".png";
			paramMap.put("image_input_title", fileName);// 	包括后缀名的图片标题,不能为空，如Bule.jpg,有些卖家希望图片上传后取图片文件的默认名 
			
			FileItem fileItem = new FileItem(fileName, imageData);
			Map fileMap = new HashMap();
			fileMap.put("img", fileItem);
			
			//请求数据
			JSONObject responseJsonObject = TbApiUtil.getJson("taobao.picture.upload", "picture_path", paramMap, fileMap, accessToken);
			
			//筛选数据
			responseJsonObject = responseJsonObject.getJSONObject("picture_upload_response");
			if(responseJsonObject.has("picture")){//如果有返回数据
				Map picMap = JsonUtil.jsonToMap(responseJsonObject.getString("picture"));
				resultMap.put("id", picMap.get("picture_id"));
				resultMap.put("path", picMap.get("picture_path"));
			}
		}
		
		return resultMap;
	}

	/**
	 * http://api.taobao.com/apidoc/api.htm?path=cid:10122-apiId:140	<br/>
	 * taobao.picture.upload 上传单张图片
	 */
	@Override
	public Map upload(String albumid, String name, List<String> imageList, String accessToken, String userId) throws Exception {
		
		Map resultMap = new HashMap();
		for(String imageUrl : imageList){
			if(imageUrl.indexOf("taobao") == -1){//如果该图片不属于淘宝，就上传
				byte[] imageData = null;
				try{
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					BufferedImage bi = ImageIO.read(new URL(imageUrl));
					ImageIO.write(bi, "png", bos);
					imageData = bos.toByteArray();
					bos.close();
				} catch(Exception e){
					logger.error(e.getMessage());
				}
				
				//开始上传图片
				if(imageData != null){
					//条件
					Map paramMap = new HashMap();
					paramMap.put("picture_category_id", albumid);//图片分类ID，设置具体某个分类ID或设置0上传到默认分类，只能传入一个分类 
					
					//文件
					String fileName = name + ".png";
					paramMap.put("image_input_title", fileName);// 	包括后缀名的图片标题,不能为空，如Bule.jpg,有些卖家希望图片上传后取图片文件的默认名 
					
					FileItem fileItem = new FileItem(fileName, imageData);
					Map fileMap = new HashMap();
					fileMap.put("img", fileItem);
					
					//请求数据
					JSONObject responseJsonObject = TbApiUtil.getJson("taobao.picture.upload", "picture_path", paramMap, fileMap, accessToken);
					
					//筛选数据
					responseJsonObject = responseJsonObject.getJSONObject("picture_upload_response");
					if(responseJsonObject.has("picture")){//如果有返回数据
						String newImageUrl = responseJsonObject.getJSONObject("picture").getString("picture_path");
						resultMap.put(imageUrl, newImageUrl);
					}
				}
			}
		}
		return resultMap;
	}

	
	/**
	 * http://api.taobao.com/apidoc/api.htm?spm=0.0.0.0.Bt12Nz&path=cid:10122-apiId:227	<br/>
	 * taobao.picture.category.add 新增图片分类信息
	 */
	@Override
	public String createAlbum(String name, String authority, String description, String password, String accessToken, String userId) throws Exception {
		
		//条件
		Map paramMap = new HashMap();
		paramMap.put("parent_id", "0");//图片分类的父分类,一级分类的parent_id为0,二级分类的则为其父分类的picture_category_id 
		paramMap.put("picture_category_name", name);//图片分类名称，最大长度20字符，中英文都算一字符，不能为空 
		
		//请求数据
		JSONObject responseJsonObject = TbApiUtil.getJson("taobao.picture.category.add", "picture_category_id", paramMap, accessToken);
		
		
		//筛选数据
		responseJsonObject = responseJsonObject.getJSONObject("picture_category_add_response");
		if(responseJsonObject.has("picture_category")){//如果有返回数据
			return responseJsonObject.getJSONObject("picture_category").getString("picture_category_id");
		}
		
		return null;
	}


	/**
	 * http://api.taobao.com/apidoc/api.htm?path=cid:10122-apiId:137
	 * taobao.picture.category.get 获取图片分类信息
	 */
	@Override
	public boolean hasAlbum(String ablumId, String accessToken) throws Exception {
		
		//条件
		Map paramMap = new HashMap();
		paramMap.put("picture_category_id", ablumId);//图片分类id 
		
		try {
			//请求数据
			JSONObject responseJsonObject = TbApiUtil.getJson("taobao.picture.category.get", "picture_category_id", paramMap, accessToken);
			String responseString = responseJsonObject.toString();
			if(responseString.contains("\"picture_category_id\":"+ ablumId + ",")) return true;
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
			imgIds += "," + imgIdList.get(i);
		}
		if(imgIds.length()<1) return;
		if(imgIds.startsWith(",")) imgIds = imgIds.substring(1);
		count++;
		delete(imgIds, accessToken);
		if(end<imgIdList.size()) delete(imgIdList, accessToken, count);
	}

	/**
	 * http://open.taobao.com/apidoc/api.htm?path=cid:10122-apiId:139
	 * taobao.picture.delete 删除图片空间图片
	 * @throws Exception 
	 */
	@Override
	public void delete(String imgId, String accessToken) throws Exception {
		//条件
		Map paramMap = new HashMap();
		paramMap.put("picture_ids", imgId);
		
		//请求数据
		try{
			TbApiUtil.getJson("taobao.picture.delete", null, paramMap, accessToken);
		} catch(Exception e){
			logger.error(e.getMessage());
		}
	}


	/**
	 * http://open.taobao.com/apidoc/api.htm?path=cid:4-apiId:24	<br/>
	 * taobao.item.img.delete 删除商品图片
	 * @throws Exception 
	 */
	@Override
	public boolean deleteItemImg(String goodsId, String imgId, String accessToken) throws Exception {
		if(NumberUtil.parseDouble(imgId)<=0) return false;
		//条件
		Map paramMap = new HashMap();
		paramMap.put("num_iid", goodsId);
		paramMap.put("id", imgId);
		
		//请求数据
		JSONObject responseJsonObject = TbApiUtil.getJson("taobao.item.img.delete", null, paramMap, accessToken);
		
		return true;
	}

	@Override
	public List uploadGoodsImage(String albumid, String name, String goodsId, List<String> imgPathList, String accessToken) throws Exception {
		List resultList = new ArrayList();
		int i=1;
		for(String imgPath : imgPathList){
			boolean isMain = false;
			if(i==1) isMain = true;
			Map goods = new HashMap();
			goods.put("id", goodsId);
			Map map = uploadGoodsImage(albumid, name, goods, imgPath, isMain, accessToken);
			resultList.add(map);
			i++;
		}
		return resultList;
	}
	
	/**
	 * http://open.taobao.com/apidoc/api.htm?path=cid:4-apiId:23	<br/>
	 * taobao.item.img.upload 添加商品图片
	 * @throws Exception 
	 */
	@Override
	public Map uploadGoodsImage(String albumid, String name, Map goods, String imgPath, boolean isMain, String accessToken) throws Exception {
		Map resultMap = new HashMap();
		//条件
		Map paramMap = new HashMap();
		paramMap.put("num_iid", goods.get("id"));
		if(isMain) paramMap.put("is_major", "true");
		
		Map fileMap = new HashMap();
		fileMap.put("image", new FileItem(imgPath));
		
		//请求数据
		JSONObject responseJsonObject = TbApiUtil.getJson("taobao.item.img.upload", null, paramMap, fileMap, accessToken);
		
		//筛选数据
		responseJsonObject = responseJsonObject.getJSONObject("item_img_upload_response");
		if(responseJsonObject.has("item_img")){//如果有返回数据
			Map picMap = JsonUtil.jsonToMap(responseJsonObject.getString("item_img"));
			resultMap.put("id", picMap.get("id"));
			resultMap.put("path", picMap.get("url"));
		}
		
		return resultMap;
	}

	@Override
	public Map replaceGoodsImage(String albumid, String name, Map goods, String id, String oldImgUrl, String newImgUrl, boolean isMain, String accessToken) throws Exception {
		if(!isMain) deleteItemImg(goods.get("id").toString(), id, accessToken);
		//下载新图片
		String newImgPath = FilePathUtil.getWebRoot() + "/userRes/temp/" + NumberUtil.getRandomStr() + ".jpg";
		try{
			FileUtil.download(newImgUrl, newImgPath);
		} catch(Exception e){
			throw new SimpleMessageException("替换失败，图片不存在");
		}
		Map resultMap = uploadGoodsImage(albumid, name, goods, newImgPath, isMain, accessToken);
		FileUtil.deleteFile(newImgPath);
		return resultMap;
	}
	
	@Override
	public Map replaceGoodsImage2(String albumid, String name, Map goods, String id, String oldImgUrl, String imgPath, boolean isMain, String accessToken) throws Exception {
		if(!isMain) deleteItemImg(goods.get("id").toString(), id, accessToken);
		Map resultMap = uploadGoodsImage(albumid, name, goods, imgPath, isMain, accessToken);
		return resultMap;
	}
	
	
	@Override
	public void replaceAllGoodsImage(String albumid, String name, String goodsId, List<String> oldImgIdList, List<String> newImgUrlList, String accessToken) throws Exception {
		int i=0;
		boolean isMain = true;
		Map goods = new HashMap();
		goods.put("id", goodsId);
		for(String newImgUrl : newImgUrlList){
			if(i==0) isMain = true;
			else isMain = false;
			replaceGoodsImage(albumid, name, goods, oldImgIdList.get(i), null, newImgUrl, isMain, accessToken);
			i++;
		}
	}

	@Override
	public void updateSkuImg(String goodsId, String newSkuStr,
			String accessToken) {
		
	}
}