package com.yahoo.finance.route.api;


import com.yahoo.finance.bean.FinanceApiRequest;
import com.yahoo.finance.bean.FinanceApiResponse;
import com.yahoo.finance.common.annotation.UrlPath;
import com.yahoo.finance.exceptions.AbstractException;
import com.yahoo.finance.impl.FinanceApiImpl;
import com.yahoo.finance.route.AbstractJsonHandler;
import com.yahoo.finance.util.CommonUtil;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;

/**
 * 
 * @author tausif
 * @param <FinanceApiRequest>>
 * @param <FinanceApiResponse>
 *
 */
@UrlPath(name = "/vertx/yfa/:coloumnHeader",
produces = "application/json", 
method = HttpMethod.POST)
public class FinanceApi extends AbstractJsonHandler<FinanceApiRequest, FinanceApiResponse>{

	@Override
	protected void performTask(FinanceApiRequest request) throws AbstractException {
		
		String coloumnHeader = getRoutingContext().request().getParam("coloumnHeader");
		String stockFile = CommonUtil.isNotNull(request.getStock()) ? CommonUtil.processString(request.getStock()) : CommonUtil.readFile();
		new FinanceApiImpl(getRoutingContext(), stockFile, coloumnHeader).perform();
		
	}
	
	@Override
	protected Object readBufferedRequest(Buffer buffer) throws AbstractException {
		
		FinanceApiRequest financeApiRequest = new FinanceApiRequest();
		financeApiRequest.setStock(CommonUtil.isNotNull(buffer.toString()) ? buffer.toString() : null);
		
		return financeApiRequest;
	}
}
