package com.cof.app.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution) throws IOException {
		traceRequest(request, body);
		ClientHttpResponse response = null;
		try {
			response = execution.execute(request, body);
		} catch (Throwable t) {
			throw t;
		} finally {
			traceResponse(response);
		}

		return response;
	}

	private void traceRequest(HttpRequest request, byte[] body) throws IOException {
		LOG.debug("-------------------- REQUEST --------------------\n");
		LOG.debug("URI : {}", request.getURI());
		LOG.debug("Headers: {}", request.getHeaders());
		LOG.debug("Method : {}", request.getMethod());
		String requestBody = getRequestBody(body);
		if (StringUtils.isNotBlank(requestBody)) {
			LOG.debug("Body :\n{}\n", getRequestBody(body));
		}
	}

	private void traceResponse(ClientHttpResponse response) throws IOException {
		LOG.debug("-------------------- RESPONSE --------------------\n");
		LOG.debug("Status Code: {}", response.getStatusCode());
		LOG.debug("Status Text: {}", response.getStatusText());
		String body = getResponseBody(response);
		if (StringUtils.isNotBlank(body)) {
			LOG.debug("Body:\n{}\n", body);
		}
	}

	private String getRequestBody(byte[] body) throws UnsupportedEncodingException {
		if ((body != null) && (body.length > 0)) {
			String content = new String(body, StandardCharsets.UTF_8);

			if (!StringUtils.isBlank(content)) {
				if (!isValidJSON(content)) {
					content.replaceAll("\"", "\\\"");
					content = "\"" + content + "\"";
				}
				return content;
			}
		}
		return null;
	}

	private String getResponseBody(ClientHttpResponse response) {
		try {
			if ((response != null) && (response.getBody() != null)) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
				String line = bufferedReader.readLine();
				while (line != null) {

					if (StringUtils.endsWith(line, "}, {")) {
						line = line.replace(" {", "");
						builder.append(line);

						String closingBracket = "\n";
						for (int i = 0; i < (line.length() - 2); i++) {
							closingBracket += " ";
						}
						closingBracket += "{\n";
						builder.append(closingBracket);
					} else {
						builder.append(line);
						builder.append("\n");
					}
					line = bufferedReader.readLine();
				}
				return builder.toString();
			} else {
				return null;
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	public boolean isValidJSON(final String json) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.readTree(json);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
