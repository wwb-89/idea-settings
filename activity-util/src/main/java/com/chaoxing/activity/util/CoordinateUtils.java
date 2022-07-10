package com.chaoxing.activity.util;

import lombok.Data;

import java.math.BigDecimal;

/**坐标工具
 * @author wwb
 * @version ver 1.0
 * @className CoordinateUtils
 * @description
 * @blame wwb
 * @date 2022-01-20 15:55:27
 */
public class CoordinateUtils {

	static double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
	/** π */
	static double PI = 3.1415926535897932384626;
	/** 长半轴 */
	static double A = 6378245.0;
	/** 扁率 */
	static double EE = 0.00669342162296594323;

	private CoordinateUtils() {

	}

	@Data
	public static class Coordinate {

		/** 经度 */
		private BigDecimal lng;
		/** 纬度 */
		private BigDecimal lat;

		private Coordinate() {

		}

		public static Coordinate build(double lng, double lat) {
			Coordinate coordinate = new Coordinate();
			coordinate.setLng(BigDecimal.valueOf(lng));
			coordinate.setLat(BigDecimal.valueOf(lat));
			return coordinate;
		}

	}

	/**
	 * 火星坐标系(GCJ-02)转百度坐标系(BD-09)
	 *
	 * @Description 谷歌、高德——>百度
	 * @param lng 火星坐标经度
	 * @param lat 火星坐标纬度
	 * @return 百度坐标数组
	 */
	public static Coordinate gcj02tobd09(double lng, double lat) {
		double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
		double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * X_PI);
		double bdLng = z * Math.cos(theta) + 0.0065;
		double bdLat = z * Math.sin(theta) + 0.006;
		return Coordinate.build(bdLng, bdLat);
	}

	/**
	 * 百度坐标系(BD-09)转火星坐标系(GCJ-02)
	 * @Description 百度——>谷歌、高德
	 * @param bdLon 百度坐标纬度
	 * @param bdLat 百度坐标经度
	 * @return 火星坐标数组
	 */
	public static Coordinate bd09togcj02(double bdLon, double bdLat) {
		double x = bdLon - 0.0065;
		double y = bdLat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
		double ggLng = z * Math.cos(theta);
		double ggLat = z * Math.sin(theta);
		return Coordinate.build(ggLng, ggLat);
	}

	/**
	 * WGS84转GCJ02(火星坐标系)
	 *
	 * @param lng WGS84坐标系的经度
	 * @param lat WGS84坐标系的纬度
	 * @return 火星坐标数组
	 */
	public static Coordinate wgs84togcj02(double lng, double lat) {
		if (outOfChina(lng, lat)) {
			return Coordinate.build(lng, lat);
		}
		double dlat = transformlat(lng - 105.0, lat - 35.0);
		double dlng = transformlng(lng - 105.0, lat - 35.0);
		double radlat = lat / 180.0 * PI;
		double magic = Math.sin(radlat);
		magic = 1 - EE * magic * magic;
		double sqrtmagic = Math.sqrt(magic);
		dlat = (dlat * 180.0) / ((A * (1 - EE)) / (magic * sqrtmagic) * PI);
		dlng = (dlng * 180.0) / (A / sqrtmagic * Math.cos(radlat) * PI);
		double mglng = lng + dlng;
		double mglat = lat + dlat;
		return Coordinate.build(mglng, mglat);
	}

	/**
	 * GCJ02(火星坐标系)转GPS84
	 *
	 * @param lng 火星坐标系的经度
	 * @param lat 火星坐标系纬度
	 * @return WGS84坐标数组
	 */
	public static Coordinate gcj02towgs84(double lng, double lat) {
		if (outOfChina(lng, lat)) {
			return Coordinate.build(lng, lat);
		}
		double dlat = transformlat(lng - 105.0, lat - 35.0);
		double dlng = transformlng(lng - 105.0, lat - 35.0);
		double radlat = lat / 180.0 * PI;
		double magic = Math.sin(radlat);
		magic = 1 - EE * magic * magic;
		double sqrtmagic = Math.sqrt(magic);
		dlat = (dlat * 180.0) / ((A * (1 - EE)) / (magic * sqrtmagic) * PI);
		dlng = (dlng * 180.0) / (A / sqrtmagic * Math.cos(radlat) * PI);
		double mglat = lat + dlat;
		double mglng = lng + dlng;
		return Coordinate.build(lng * 2 - mglng, lat * 2 - mglat);
	}

	/**
	 * 纬度转换
	 *
	 * @param lng
	 * @param lat
	 * @return
	 */
	public static double transformlat(double lng, double lat) {
		double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	/**
	 * 经度转换
	 *
	 * @param lng
	 * @param lat
	 * @return
	 */
	public static double transformlng(double lng, double lat) {
		double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
		return ret;
	}

	/**
	 * 判断是否在国内，不在国内不做偏移
	 *
	 * @param lng
	 * @param lat
	 * @return
	 */
	public static boolean outOfChina(double lng, double lat) {
		if (lng < 72.004 || lng > 137.8347) {
			return true;
		} else if (lat < 0.8293 || lat > 55.8271) {
			return true;
		}
		return false;
	}

}