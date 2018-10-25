package com.lockstudio.sticklocker.model;

public class BoonBean {
//	"title": "【第一期活动】T-ara活动开奖了",
//    "logo": "http://static.opda.com:808/resource/font/lock/wzsp5/finish2/20150311.png",
//    "url": "http://www.baidu.com",
//    "item": [
//        {
//            "title": "【301活动】T-ara活动开奖了",
//            "logo": "http://static.opda.com:808/resource/font/lock/wzsp5/finish2/20150312.png",
//            "url": "http://www.baidu.com"
//        },
//        {
//            "title": "【302活动】T-ara活动开奖了",
//            "logo": "http://static.opda.com:808/resource/font/lock/wzsp5/finish2/20150313.png",
//            "url": "http://www.baidu.com"
//        }
//    ]

    private String title;
    private String img_url;
    private String web_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }
}
