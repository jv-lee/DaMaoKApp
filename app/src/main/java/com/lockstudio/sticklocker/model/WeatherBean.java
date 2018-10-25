package com.lockstudio.sticklocker.model;

public class WeatherBean {
	
	/*{
		errNum: 0,
		errMsg: "success",
		retData: {
		   city: "北京", //城市
		   pinyin: "beijing", //城市拼音
		   citycode: "101010100",  //城市编码	
		   date: "15-02-11", //日期
		   time: "11:00", //发布时间
		   postCode: "100000", //邮编
		   longitude: 116.391, //经度
		   latitude: 39.904, //维度
		   altitude: "33", //海拔	
		   weather: "晴",  //天气情况
		   temp: "10", //气温
		   l_tmp: "-4", //最低气温
		   h_tmp: "10", //最高气温
		   WD: "无持续风向",	 //风向
		   WS: "微风(<10m/h)", //风力
		   sunrise: "07:12", //日出时间
		   sunset: "17:44" //日落时间
		}
	}*/
	
	private String city_name;
	private String city_pinyin;
	private String city_code;
	private String date;
	private String time;
	private String temp;
	private String weather;
	private String wind_power;
	private String low_temp;
	private String high_temp;
	
	
	
	public String getLow_temp() {
		return low_temp;
	}
	public void setLow_temp(String low_temp) {
		this.low_temp = low_temp;
	}
	public String getHigh_temp() {
		return high_temp;
	}
	public void setHigh_temp(String high_temp) {
		this.high_temp = high_temp;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public String getCity_pinyin() {
		return city_pinyin;
	}
	public void setCity_pinyin(String city_pinyin) {
		this.city_pinyin = city_pinyin;
	}
	public String getCity_code() {
		return city_code;
	}
	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getWind_power() {
		return wind_power;
	}
	public void setWind_power(String wind_power) {
		this.wind_power = wind_power;
	}
	@Override
	public String toString() {
		return "WeatherBean [city_name=" + city_name + ", city_pinyin="
				+ city_pinyin + ", city_code=" + city_code + ", date=" + date
				+ ", time=" + time + ", temp=" + temp + ", weather=" + weather
				+ ", wind_power=" + wind_power + ", low_temp=" + low_temp
				+ ", high_temp=" + high_temp + "]";
	}

	
}
