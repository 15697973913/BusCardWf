package com.example.buscardXiAn.util;


/**
 *
 * @author 杰 线路信息实体类
 */
public class LineMsg_Util {
	private String StationId;// 线路id
	private String LineWord;// 线路名称
	private String StationUpLast;// 上行终点站
	private String StationDownLast;// 下行终点站
	private String StationUpStartTime;// 上行首班时间
	private String StationUpEndTime;// 上行末班时间
	private String StationDownStartTime;// 下行首班时间
	private String StationDownEndTime;// 下行末班时间
	private String Ticket;// 票价

	public String getStationId() {
		return StationId;
	}

	public void setStationId(String stationId) {
		StationId = stationId;
	}

	public String getLineWord() {
		return LineWord;
	}

	public void setLineWord(String lineWor) {
		LineWord = lineWor;
	}

	public String getStationUpLast() {
		return StationUpLast;
	}

	public void setStationUpLast(String stationUpLast) {
		StationUpLast = stationUpLast;
	}

	public String getStationDownLast() {
		return StationDownLast;
	}

	public void setStationDownLast(String stationDownLast) {
		StationDownLast = stationDownLast;
	}

	public String getStationUpStartTime() {
		return StationUpStartTime;
	}

	public void setStationUpStartTime(String stationUpStartTime) {
		StationUpStartTime = stationUpStartTime;
	}

	public String getStationUpEndTime() {
		return StationUpEndTime;
	}

	public void setStationUpEndTime(String stationUpEndTime) {
		StationUpEndTime = stationUpEndTime;
	}

	public String getStationDownStartTime() {
		return StationDownStartTime;
	}

	public void setStationDownStartTime(String stationDownStartTime) {
		StationDownStartTime = stationDownStartTime;
	}

	public String getStationDownEndTime() {
		return StationDownEndTime;
	}

	public void setStationDownEndTime(String stationDownEndTime) {
		StationDownEndTime = stationDownEndTime;
	}

	public String getTicket() {
		return Ticket;
	}

	public void setTicket(String ticket) {
		Ticket = ticket;
	}

	public String toString() {
		return "LineMsg_Util [StationId=" + StationId + ", LineWord=" + LineWord
				+ ", StationUpLast=" + StationUpLast + ", StationDownLast="
				+ StationDownLast + ", StationUpStartTime="
				+ StationUpStartTime + ", StationUpEndTime=" + StationUpEndTime
				+ ", StationDownStartTime=" + StationDownStartTime
				+ ", StationDownEndTime=" + StationDownEndTime + ", Ticket="
				+ Ticket + "]";
	}
}

