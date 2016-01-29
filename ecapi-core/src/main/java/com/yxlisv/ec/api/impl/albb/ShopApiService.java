package com.yxlisv.ec.api.impl.albb;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yxlisv.ec.api.IShopApiService;
import com.yxlisv.service.AbstractBaseService;

/**
 * 阿里巴巴店铺的api服务
 * @author yxl
 */
@Service("albbShopApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class ShopApiService extends AbstractBaseService implements IShopApiService {

	/**
	 * http://open.1688.com/doc/api/cn/api.htm?ns=cn.alibaba.open&n=category.getSelfCatlist&v=1	<br/>
	 * category.getSelfCatlist 获取指定会员（供应商）的自定义商品分类信息 
	 */
	@Override
	public List<Map> getCategory(String memberId, String accessToken) throws Exception {
		
		return null;
	}

	@Override
	public List<Map> formatCategoryForSelect(List<Map> categoryList) {
		return null;
	}
}
