package com.cof.app.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuandlPricingDataset {

	private Long id;
	@JsonProperty("dataset_code")
	private String datasetCode;
	@JsonProperty("database_code")
	private String databaseCode;
	private String name;
	private String description;
	@JsonProperty("refreshed_at")
	private String refreshedAt;
	@JsonProperty("newest_available_date")
	private String newestAvailableDate;
	@JsonProperty("oldest_available_date")
	private String oldestAvailableDate;
	@JsonProperty("column_names")
	private List<String> columnNames;
	private String frequency;
	private String type;
	private boolean premium;
	private Integer limit;
	private String transform;
	@JsonProperty("column_index")
	private Integer columnIndex;
	@JsonProperty("start_date")
	private String startDate;
	@JsonProperty("end_date")
	private String endDate;
	private List<List<Object>> data;
	private String collapse;
	private String order;
	@JsonProperty("database_id")
	private Long databaseId;

	public QuandlPricingDataset() {

	}

	/**
	 * Used for testing purposes.
	 * 
	 * @param datasetCode
	 * @param data
	 */
	public QuandlPricingDataset(String datasetCode, List<List<Object>> data) {
		this.datasetCode = datasetCode;
		this.data = data;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getDatabaseCode() {
		return databaseCode;
	}

	public void setDatabaseCode(String databaseCode) {
		this.databaseCode = databaseCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRefreshedAt() {
		return refreshedAt;
	}

	public void setRefreshedAt(String refreshedAt) {
		this.refreshedAt = refreshedAt;
	}

	public String getNewestAvailableDate() {
		return newestAvailableDate;
	}

	public void setNewestAvailableDate(String newestAvailableDate) {
		this.newestAvailableDate = newestAvailableDate;
	}

	public String getOldestAvailableDate() {
		return oldestAvailableDate;
	}

	public void setOldestAvailableDate(String oldestAvailableDate) {
		this.oldestAvailableDate = oldestAvailableDate;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getTransform() {
		return transform;
	}

	public void setTransform(String transform) {
		this.transform = transform;
	}

	public Integer getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(Integer columnIndex) {
		this.columnIndex = columnIndex;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<List<Object>> getData() {
		return data;
	}

	public void setData(List<List<Object>> data) {
		this.data = data;
	}

	public String getCollapse() {
		return collapse;
	}

	public void setCollapse(String collapse) {
		this.collapse = collapse;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Long getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}

}
