package com.cof.app.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;

import com.fasterxml.jackson.databind.JavaType;

public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

	private static final Logger LOG = LoggerFactory
			.getLogger(CustomMappingJackson2HttpMessageConverter.class);

	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		JavaType javaType = getJavaType(type, contextClass);
		return readJavaType(javaType, inputMessage);
	}

	private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
		try {
			InputStream is = inputMessage.getBody();

			MediaType contentType = inputMessage.getHeaders().getContentType();
			String jsContentType = "application/javascript;charset=UTF-8";

			if (StringUtils.equals(contentType.toString(), jsContentType)) {
				String body = IOUtils.toString(is, StandardCharsets.UTF_8);
				LOG.info("JSONP response: {}", body);

				if (body.startsWith("(") && body.endsWith(")")) {
					body = body.substring(1, body.length() - 1);
				}

				is = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
			}

			if (inputMessage instanceof MappingJacksonInputMessage) {
				Class<?> deserializationView = ((MappingJacksonInputMessage) inputMessage)
						.getDeserializationView();
				if (deserializationView != null) {
					return objectMapper.readerWithView(deserializationView).forType(javaType)
							.readValue(is);
				}
			}

			return objectMapper.readValue(is, javaType);

		} catch (IOException ex) {
			throw new HttpMessageNotReadableException(
					"Could not read JSON document: " + ex.getMessage(), ex);
		}
	}

}
