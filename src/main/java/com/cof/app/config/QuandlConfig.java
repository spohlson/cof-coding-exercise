package com.cof.app.config;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.cof.app.exception.DataNotFoundException;
import com.cof.app.model.QuandlPricingDataColumn;
import com.cof.app.model.QuandlRouteTemplate;

@Configuration
@ConfigurationProperties(prefix = "quandl")
public class QuandlConfig {

	@NotEmpty
	private String apiKey;

	@NotEmpty
	private String baseApi;

	@NotEmpty
	private String dateFormat;

	@NotEmpty
	private Map<QuandlRouteTemplate, String> routeTemplatesMap;

	@NotEmpty
	private List<QuandlPricingDataColumn> pricingDataColumnOrder;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getBaseApi() {
		return baseApi;
	}

	public void setBaseApi(String baseApi) {
		this.baseApi = baseApi;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Map<QuandlRouteTemplate, String> getRouteTemplatesMap() {
		return routeTemplatesMap;
	}

	public void setRouteTemplatesMap(Map<QuandlRouteTemplate, String> routeTemplatesMap) {
		this.routeTemplatesMap = routeTemplatesMap;
	}

	public List<QuandlPricingDataColumn> getPricingDataColumnOrder() {
		return pricingDataColumnOrder;
	}

	public void setPricingDataColumnOrder(List<QuandlPricingDataColumn> pricingDataColumnOrder) {
		this.pricingDataColumnOrder = pricingDataColumnOrder;
	}

	public int getPricingDataColumnOrderSize() {
		return pricingDataColumnOrder.size();
	}

	public QuandlPricingDataColumn getQuandlPricingDataColumnByIndex(int index) {
		return pricingDataColumnOrder.get(index);
	}

	public int getPricingDataColumnIndex(QuandlPricingDataColumn column) {
		for (int i = 0; i < pricingDataColumnOrder.size(); i++) {

			if (column.equals(pricingDataColumnOrder.get(i))) {
				return i;
			}
		}
		throw new DataNotFoundException(
				column.toString() + " enum not specified in application.yml");
	}

	public String getApiForRoute(QuandlRouteTemplate route) {
		String template = routeTemplatesMap.get(route);

		if (StringUtils.isEmpty(template)) {
			return null;
		}
		return getBaseApi() + template;
	}

}
