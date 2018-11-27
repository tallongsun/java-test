package com.dl.mybatis.po;

public class Blog {
    private Long id;

    private String title;
    public Blog() {
    	
    }
    

    public Blog(Long id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }


	@Override
	public String toString() {
		return "Blog [id=" + id + ", title=" + title + "]";
	}
    
    
}