package entity;

import java.io.Serializable;

/**
 * 
 * @author ping
 * @create 2014-9-22 下午5:24:23
 */
public class Test implements Serializable{
	private int id;
	private String content;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
