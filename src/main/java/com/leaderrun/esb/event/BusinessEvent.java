package com.leaderrun.esb.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessEvent<T> {

	final BusinessEventHeader header;
	final T body;

	@JsonCreator
	public BusinessEvent( //
			@JsonProperty("header") BusinessEventHeader header, //
			@JsonProperty("body") T body //
	) {
		super();
		this.header = header;
		this.body = body;
	}

	public BusinessEventHeader getHeader() {
		return header;
	}

	public T getBody() {
		return body;
	}

}
