package com.leaderrun.esb.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

	final static ObjectMapper objMapper = new ObjectMapper();

	public static String toJsonString(Object o) {
		try {
			return objMapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] toJsonBytes(Object o) {
		try {
			return objMapper.writeValueAsBytes(o);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T toObject(byte[] body, TypeReference<T> valueTypeRef) {
		try {
			return objMapper.readValue(body, valueTypeRef);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
