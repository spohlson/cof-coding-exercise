package com.cof.app.config;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.cof.app.model.RouteTemplate;

@Configuration
@ConfigurationProperties(prefix = "quandl")
public class QuandlConfig {

	@NotEmpty
	private String apiKey;

	@NotEmpty
	private String baseApi;

	@NotEmpty
	private String defaultStartDate;

	@NotEmpty
	private String defaultEndDate;

	@NotEmpty
	private List<String> defaultTickers;

	@NotEmpty
	private String dateFormat;

	@NotEmpty
	private Map<RouteTemplate, String> routeTemplatesMap;

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

	public String getDefaultStartDate() {
		return defaultStartDate;
	}

	public void setDefaultStartDate(String defaultStartDate) {
		this.defaultStartDate = defaultStartDate;
	}

	public String getDefaultEndDate() {
		return defaultEndDate;
	}

	public void setDefaultEndDate(String defaultEndDate) {
		this.defaultEndDate = defaultEndDate;
	}

	public List<String> getDefaultTickers() {
		return defaultTickers;
	}

	public void setDefaultTickers(List<String> defaultTickers) {
		this.defaultTickers = defaultTickers;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Map<RouteTemplate, String> getRouteTemplatesMap() {
		return routeTemplatesMap;
	}

	public void setRouteTemplatesMap(Map<RouteTemplate, String> routeTemplatesMap) {
		this.routeTemplatesMap = routeTemplatesMap;
	}

	public String getApiForRoute(RouteTemplate route) {
		String template = routeTemplatesMap.get(route);

		if (StringUtils.isEmpty(template)) {
			return null;
		}
		return getBaseApi() + template;
	}

}
