package com.yxlisv.ec.api.impl.tb;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yxlisv.ec.api.IAppApiService;
import com.yxlisv.service.AbstractBaseService;
import com.yxlisv.util.data.Page;

/**
 * 淘宝App API服务
 * @author yxl
 */
@Service("tbAppApiService")
@Transactional(propagation=Propagation.REQUIRED)
public class AppApiService extends AbstractBaseService implements IAppApiService {

	@Override
	public void getOrderList(Page page, Map<String, String[]> srMap, String orderBy) throws Exception {
		
	}
}
