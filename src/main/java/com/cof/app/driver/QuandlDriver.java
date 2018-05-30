package com.cof.app.driver;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cof.app.config.QuandlConfig;
import com.cof.app.exception.DataNotFoundException;
import com.cof.app.model.QuandlApiQueryParam;
import com.cof.app.model.QuandlRouteTemplate;
import com.cof.app.model.QuandlTickerPricingData;

@Service
public class QuandlDriver extends PricingDriver {

	private static final Logger LOG = LoggerFactory.getLogger(QuandlDriver.class);

	@Autowired
	private QuandlConfig config;

	public QuandlDriver(RestTemplate rest) {
		this.rest = rest;
	}

	/**
	 * Fetches QuandlTickerPricingData for specified ticker in date range via
	 * Quandl's API.
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @return QuandlTickerPricingData
	 */
	public QuandlTickerPricingData getPricingData(String ticker, String startDate,
			String endDate) {
		URI uri = buildURI(QuandlRouteTemplate.PRICING_DATA, ticker, startDate, endDate);

		QuandlTickerPricingData pricingData = rest
				.exchange(uri, HttpMethod.GET, getDefaultHttpEntity(), QuandlTickerPricingData.class)
				.getBody();

		return pricingData;
	}

	/**
	 * Fetches a ticker's Quandl pricing data and checks if any data was
	 * returned/included, throws DataNotFoundException if not.
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @return A list of each day's pricing data which is a list of objects for
	 *         the specified ticker.
	 */
	public List<List<Object>> getTickerDayDataList(String ticker, String startDate,
			String endDate) {
		QuandlTickerPricingData quandlTickerPricingData = getPricingData(ticker, startDate,
				endDate);

		if ((quandlTickerPricingData == null) || (quandlTickerPricingData.getDataset() == null)
				|| CollectionUtils.isEmpty(quandlTickerPricingData.getDataset().getData())) {
			throw new DataNotFoundException(ticker + "'s pricing data between " + startDate
					+ " and " + endDate + " not found.");
		}
		return quandlTickerPricingData.getDataset().getData();
	}

	///////////// URI Helper/Builder Methods /////////////

	private URI buildURI(QuandlRouteTemplate routeTemplate, String ticker, String startDate,
			String endDate) {
		String api = config.getApiForRoute(routeTemplate);

		switch (routeTemplate) {
			case PRICING_DATA:
				api = String.format(api, ticker);
				break;
			default:
				LOG.error("RouteTemplate must be specified.");
				break;
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(api);
		appendDefaultParams(builder, startDate, endDate);

		URI uri = builder.build().encode().toUri();
		return uri;
	}

	private void appendDefaultParams(UriComponentsBuilder builder, String startDate,
			String endDate) {
		builder.queryParam(QuandlApiQueryParam.API_KEY.value(), config.getApiKey());
		builder.queryParam(QuandlApiQueryParam.START_DATE.value(), startDate);
		builder.queryParam(QuandlApiQueryParam.END_DATE.value(), endDate);
		builder.queryParam(QuandlApiQueryParam.ORDER.value(), "asc");
	}

}
