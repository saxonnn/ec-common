package com.ec.api.impl.tb;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ec.api.IUserApiService;
import com.ec.api.impl.tb.util.TbApiUtil;
import com.yxl.service.AbstractBaseService;
import com.yxl.util.string.JsonUtil;

/**
 * 淘宝用户API服务
 * @author yxl
 */
@Service("tbUserApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class UserApiService extends AbstractBaseService implements IUserApiService {

	/**
	 * http://api.taobao.com/apidoc/api.htm?spm=0.0.0.0.3GbuyB&path=cid:1-apiId:21349	<br/>
	 * taobao.user.seller.get 查询卖家用户信息 
	 */
	@Override
	public Map getSeller(String userId, String accessToken) throws Exception {
		
		this.logger.info("正在获取淘宝卖家信息...");
		//返回参数
		String fields = "user_id,nick,sex,type,item_img_num,item_img_size,prop_img_num,prop_img_size,promoted_type,status,consumer_protection,avatar,liangpin,has_shop,is_golden_seller,vip_info";
		//请求数据
		JSONObject responseJsonObject = TbApiUtil.getJson("taobao.user.seller.get", fields, accessToken);
		//筛选数据
		String userJson = responseJsonObject.getJSONObject("user_seller_get_response").getString("user");
				
		return JsonUtil.jsonToMap(userJson);
	}
}
