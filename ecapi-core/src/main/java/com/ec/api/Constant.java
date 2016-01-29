package com.ec.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ec.api.impl.albb.util.CommonUtil;
import com.yxl.util.math.NumberUtil;
import com.yxl.util.resource.PropertiesUtil;

/**
 * 电子商务常量类
 * @author yxl
 */
public abstract class Constant {
	
	final private static Properties properties = PropertiesUtil.readProperties("ecapi.properties", Constant.class);//配置文件
	public static String appAddr;//应用测试地址
	public static String albumName;//相册名称
	static{
		appAddr = PropertiesUtil.get("appAddr", properties);
		albumName = PropertiesUtil.get("albumName", properties);
	}
	
	//淘宝的应用配置
	public static String tbAppKey;//appkey的值
	public static String tbAppSecret ;//淘宝应用密钥
	public static String tbOAuthUrl;//淘宝应用授权地址
	public static String tbOAuthTokenUrl ;//淘宝应用授权token获取地址
	public static String tbApiUrl;//淘宝API调用地址 http://gw.api.taobao.com/router/rest
	public static int tbConnectTimeout = 30000;//请求连接超时时间(毫秒)
	public static int tbReadTimeout = 10000;//读取数据超时时间(毫秒)
	static{
		tbAppKey = PropertiesUtil.get("tbAppKey", properties);
		tbAppSecret = PropertiesUtil.get("tbAppSecret", properties);
		tbOAuthUrl = PropertiesUtil.get("tbOAuthUrl", properties);
		tbOAuthTokenUrl = PropertiesUtil.get("tbOAuthTokenUrl", properties);
		tbApiUrl = PropertiesUtil.get("tbApiUrl", properties);
		tbConnectTimeout = NumberUtil.parseInt(PropertiesUtil.get("tbConnectTimeout", properties));
		tbReadTimeout = NumberUtil.parseInt(PropertiesUtil.get("tbReadTimeout", properties));
	}
	
	//阿里巴巴的应用配置
	public static String albbAppKey;//appkey的值
	public static String albbAppSecret;//阿里巴巴应用密钥
	public static String albbOAuthUrl;//阿里巴巴应用授权地址
	public static String albbOAuthTokenUrl;//阿里巴巴应用授权token获取地址，还需要拼接一个参数code，code为授权完成后返回的一次性令牌
	public static String albbApiUrl;//阿里巴巴API调用地址，还需要附加2个参数：_aop_signature 请求签名， access_token，访问用户隐私数据时的权限标识
	public static String albbApiUrl2;//阿里巴巴API调用地址第二部分 protocol/version/namespace/
	public static String albbPicUrl;//阿里巴巴API调用地址第二部分 protocol/version/namespace/  http://i00.c.aliimg.com/
	public static int albbConnectTimeout = 30000;//请求连接超时时间(毫秒)
	public static int albbReadTimeout = 10000;//读取数据超时时间(毫秒)
	static{
		albbAppKey = PropertiesUtil.get("albbAppKey", properties);
		albbAppSecret = PropertiesUtil.get("albbAppSecret", properties);
		albbOAuthUrl = PropertiesUtil.get("albbOAuthUrl", properties) + CommonUtil.getOAuthSignature();
		albbOAuthTokenUrl = PropertiesUtil.get("albbOAuthTokenUrl", properties);
		albbApiUrl = PropertiesUtil.get("albbApiUrl", properties);
		albbApiUrl2 = PropertiesUtil.get("albbApiUrl2", properties);
		albbPicUrl = PropertiesUtil.get("albbPicUrl", properties);
		albbConnectTimeout = NumberUtil.parseInt(PropertiesUtil.get("albbConnectTimeout", properties));
		albbReadTimeout = NumberUtil.parseInt(PropertiesUtil.get("albbReadTimeout", properties));
	}
	
	//拍拍的应用配置
	public static String paipaiAppKey = "700146600";//appkey的值
	public static String paipaiAppSecret = "Ivj2WJFx6uR2SkeF";//拍拍应用密钥
	public static String paipaiOAuthUrl = "http://fuwu.paipai.com/my/app/authorizeGetAccessToken.xhtml?responseType=access_token&appOAuthID=" + paipaiAppKey;//拍拍应用授权地址，这JB授权地址真难找：http://open.buy.qq.com/bin/view/Main/useroauthinfo
	public static String paipaiApiUrl = "http://api.paipai.com";//拍拍API调用地址
	
	//京东的应用配置
	public static String jdAppKey = "8BE0424FC1A306A658D83E1641E2FC5B";//京东Appkey
	public static String jdAppSecret = "fdf0e818181c4eff80ea71afc0dea4bc";//京东应用密钥
	public static String jdOAuthUrl = "https://auth.360buy.com/oauth/authorize?response_type=code&client_id=" + jdAppKey + "&redirect_uri=" + appAddr + "/ec/api/authorize/jd";//京东应用授权地址
	public static String jdOAuthTokenUrl = "https://auth.360buy.com/oauth/token?grant_type=authorization_code&client_id=" + jdAppKey + "&client_secret=" + jdAppSecret + "&scope=read&redirect_uri=" + appAddr + "/ec/api/authorize/jd";//京东应用授权token获取地址，还需要拼接code参数
	public static String jdApiUrl = "http://gw.api.360buy.com/routerjson";//京东应用授权地址
	
	
	//视觉营销的应用配置
	public static String vmOAuthTokenUrl = "http://127.0.0.1:333/netVmMarket/ec/author/token";//视觉营销应用授权token获取地址
	public static String vmApiUrl = "http://127.0.0.1:333/netVmMarket/ec";//视觉营销API调用地址
	
	
	/** 商品的页面地址 */
	public static Map<String, String> goodsUrl = new HashMap();

	static{
		goodsUrl.put("tb", "http://item.taobao.com/item.htm?id=");//淘宝
		goodsUrl.put("albb", "http://detail.1688.com/offer/");//阿里巴巴
		goodsUrl.put("paipai", "http://auction1.paipai.com/");//拍拍
		goodsUrl.put("jd", "http://item.jd.com/");//京东
		goodsUrl.put("vm", "/netVmMarket/b2cTreasure/view/");//视觉营销
	}
	
	
	/** 描述页宽度 */
	public static Map<String, String> descWidth = new HashMap();
	static{
		descWidth.put("tb", "750");//淘宝
		descWidth.put("tm", "790");//天猫
		descWidth.put("albb", "790");//阿里巴巴
		descWidth.put("paipai", "750");//拍拍
		descWidth.put("jd", "990");//京东
	}
	
	//正在测试
	public static final boolean testing = false;
	static{
		if(testing){//如果是测试环境，把所有地址换成沙箱地址
			//淘宝
			tbAppKey = "1023176463";
			tbAppSecret = "sandboxfc4e706d10854d6300306ff71";
			tbOAuthUrl = "https://oauth.tbsandbox.com/authorize?response_type=code&client_id=" + tbAppKey + "&redirect_uri=" + appAddr + "/ec/api/authorize/tb";//淘宝应用授权地址
			tbOAuthTokenUrl = "https://oauth.tbsandbox.com/token";
			tbApiUrl = "https://gw.api.tbsandbox.com/router/rest";
			goodsUrl.put("tb", "http://item.tbsandbox.com/item.htm?id=");
			
			//阿里巴巴
			//albbAppKey = "1007047";
			//albbAppSecret = "y8y~8VQYaI";
			
			//京东 沙箱帐号: sandbox_yxl 901226
			jdAppKey = "A3811D7816E42F854ADA498137C17627";
			jdAppSecret = "a1afe9d727244b6cbcfbf27a8ddf2b23";
			jdOAuthUrl = "http://auth.sandbox.360buy.com/oauth/authorize?response_type=code&client_id=" + jdAppKey + "&redirect_uri=" + appAddr + "/ec/api/authorize/jd";//京东应用授权地址
			jdOAuthTokenUrl = "http://auth.sandbox.360buy.com/oauth/token?grant_type=authorization_code&client_id=" + jdAppKey + "&client_secret=" + jdAppSecret + "&scope=read&redirect_uri=" + appAddr + "/ec/api/authorize/jd";//京东应用授权token获取地址，还需要拼接code参数
			jdApiUrl = "http://gw.api.sandbox.360buy.com/routerjson";//京东应用授权地址
		}
	}
}