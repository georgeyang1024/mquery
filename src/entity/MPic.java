package entity;
/**
 * 
 * @author ping
 * @create 2014-11-17 下午7:25:47
 */
public class MPic  {
    private String desc;
    private String tag;
    private String tag2;
    private String image_url;
    private int image_width;
    private int image_height;
    private long bdid;
	private String thumbnail_url;//缩略图
    private int thumbnail_width;
    private int thumbnail_heigth;
    private String abs;
    private String thumb_large_url;//大一点的图片
    private int thumb_large_width;
    private int thumb_large_heigth;
    private String download_url;//图片下载地址
    private String tags;
	private long id;
    private long photo_id;
    private String site_url;//官网
    private String from_url;//来源路径
    private String obj_url;//原下载路径
    private String hostname;//主机

    public String getAbs() {
		return abs;
	}

	public void setAbs(String abs) {
		this.abs = abs;
	}
	public String getTag2() {
		return tag2;
	}

	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	public long getBdid() {
		return bdid;
	}

	public void setBdid(long bdid) {
		this.bdid = bdid;
	}
    public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
    
    public int getThumbnail_width() {
        return thumbnail_width;
    }

    public void setThumbnail_width(int thumbnail_width) {
        this.thumbnail_width = thumbnail_width;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getImage_width() {
        return image_width;
    }

    public void setImage_width(int image_width) {
        this.image_width = image_width;
    }

    public int getImage_height() {
        return image_height;
    }

    public void setImage_height(int image_height) {
        this.image_height = image_height;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public int getThumbnail_heigth() {
        return thumbnail_heigth;
    }

    public void setThumbnail_heigth(int thumbnail_heigth) {
        this.thumbnail_heigth = thumbnail_heigth;
    }

    public String getThumb_large_url() {
        return thumb_large_url;
    }

    public void setThumb_large_url(String thumb_large_url) {
        this.thumb_large_url = thumb_large_url;
    }

    public int getThumb_large_width() {
        return thumb_large_width;
    }

    public void setThumb_large_width(int thumb_large_width) {
        this.thumb_large_width = thumb_large_width;
    }

    public int getThumb_large_heigth() {
        return thumb_large_heigth;
    }

    public void setThumb_large_heigth(int thumb_large_heigth) {
        this.thumb_large_heigth = thumb_large_heigth;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }



    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(long photoId) {
		photo_id = photoId;
	}

	public String getSite_url() {
        return site_url;
    }

    public void setSite_url(String site_url) {
        this.site_url = site_url;
    }

    public String getFrom_url() {
        return from_url;
    }

    public void setFrom_url(String from_url) {
        this.from_url = from_url;
    }

    public String getObj_url() {
        return obj_url;
    }

    public void setObj_url(String obj_url) {
        this.obj_url = obj_url;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}