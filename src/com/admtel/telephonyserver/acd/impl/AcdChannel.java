package com.admtel.telephonyserver.acd.impl;

import java.util.Date;

public class AcdChannel implements Comparable<AcdChannel> {
	String channelId;
	Integer priority;
	AcdQueue queue;
	Date setupTime;

	public AcdChannel(String channelId, Date setupTime, Integer priority) {
		this.channelId = channelId;
		this.priority = priority;
		this.setupTime = setupTime;
	}

	@Override
	public int compareTo(AcdChannel acdChannel) {		
		int c1 = priority.compareTo(acdChannel.priority);
		if (c1 != 0) {
			return c1;
		}
		c1 = setupTime
				.compareTo(acdChannel.setupTime);
		return c1;
	}
}