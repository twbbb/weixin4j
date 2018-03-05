package com.foxinmy.weixin4j.api;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.foxinmy.weixin4j.http.weixin.WeixinRequestExecutor;

/**
 * API基础
 *
 * @className BaseApi
 * @author jinyu(foxinmy@gmail.com)
 * @date 2014年9月26日
 * @since JDK 1.6
 * @see <a href="http://mp.weixin.qq.com/wiki/index.php">微信公众平台API文档</a>
 * @see <a href="http://qydev.weixin.qq.com/wiki/index.php">微信企业号API文档</a>
 */
public abstract class BaseApi {

	protected final WeixinRequestExecutor weixinExecutor;
	protected List<WeixinRequestExecutor> weixinExecutorList = new ArrayList(); 

	private final  Pattern uriPattern = Pattern.compile("(\\{[^\\}]*\\})");

	public BaseApi() {
		this.weixinExecutor = new WeixinRequestExecutor();
		String template_executor_number =  weixinBundle().getString("template_executor_number");
		int num = 10;
		if (template_executor_number != null)
		{
			Matcher m = null;
			Pattern p = Pattern.compile("^[0-9]+$");
			m = p.matcher(template_executor_number);
			if(m.matches())
			{
				num = Integer.parseInt(template_executor_number);
			}
		}		
		for(int i=0;i<num;i++)
		{
			weixinExecutorList.add(new WeixinRequestExecutor());
		}
	}
	private volatile int weixinExecutorIndex = 0; 
	public synchronized WeixinRequestExecutor getWeixinRequestExecutor()
	{
		weixinExecutorIndex++;
		if(weixinExecutorIndex >= weixinExecutorList.size())
		{
			weixinExecutorIndex = 0;
		}
		return weixinExecutorList.get(weixinExecutorIndex);
	}

	protected abstract ResourceBundle weixinBundle();

	protected String getRequestUri(String key) {
		String url = weixinBundle().getString(key);
		Matcher m = uriPattern.matcher(url);
		StringBuffer sb = new StringBuffer();
		String sub = null;
		while (m.find()) {
			sub = m.group();
			m.appendReplacement(sb,
					getRequestUri(sub.substring(1, sub.length() - 1)));
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
