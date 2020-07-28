package com.leaderrun.esb.event;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessEventHeader {

	final String msgId;
	final String sysId;
	final String type;
	final String version;
	final Date timestamp;

	@JsonCreator
	public BusinessEventHeader( //
			@JsonProperty("msgId") String msgId, //
			@JsonProperty("sysId") String sysId, //
			@JsonProperty("type") String type, //
			@JsonProperty("version") String version, //
			@JsonProperty("timestamp") Date timestamp //
	) {
		super();
		this.msgId = msgId;
		this.sysId = sysId;
		this.type = type;
		this.version = version;
		this.timestamp = timestamp;
	}

	public String getMsgId() {
		return msgId;
	}

	public String getSysId() {
		return sysId;
	}

	public String getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

	public Date getTimestamp() {
		return timestamp;
	}

}
