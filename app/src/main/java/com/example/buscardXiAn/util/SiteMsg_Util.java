package com.example.buscardXiAn.util;

/**
 *
 * @author 杰
 *	站点信息实体类
 */
public class SiteMsg_Util {
	private String StationName;//站点名称
	private String StationDULNo;//双行号
	private String StationSNGNo;//单行号
	private String Longitude;//到站经度
	private String Latitude;//到站纬度
	private String Longitudeout;//出站经度
	private String Latitudeout;//出站纬度
	private String MicroDistance;//偏差距离，目前默认为0
	public String getStationName() {
		return StationName;
	}
	public void setStationName(String stationName) {
		StationName = stationName;
	}
	public String getStationDULNo() {
		return StationDULNo;
	}
	public void setStationDULNo(String stationDULNo) {
		StationDULNo = stationDULNo;
	}
	public String getStationSNGNo() {
		return StationSNGNo;
	}
	public void setStationSNGNo(String stationSNGNo) {
		StationSNGNo = stationSNGNo;
	}
	public String getLongitude() {
		return Longitude;
	}
	public void setLongitude(String longitude) {
		Longitude = longitude;
	}
	public String getLatitude() {
		return Latitude;
	}
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}
	public String getLongitudeout() {
		return Longitudeout;
	}
	public void setLongitudeout(String longitudeout) {
		Longitudeout = longitudeout;
	}
	public String getLatitudeout() {
		return Latitudeout;
	}
	public void setLatitudeout(String latitudeout) {
		Latitudeout = latitudeout;
	}
	public String getMicroDistance() {
		return MicroDistance;
	}
	public void setMicroDistance(String microDistance) {
		MicroDistance = microDistance;
	}
	@Override
	public String toString() {
		return "SiteMsg_Util [StationName=" + StationName + ", StationDULNo="
				+ StationDULNo + ", StationSNGNo=" + StationSNGNo
				+ ", Longitude=" + Longitude + ", Latitude=" + Latitude
				+ ", Longitudeout=" + Longitudeout + ", Latitudeout="
				+ Latitudeout + ", MicroDistance=" + MicroDistance + "]";
	}

}
