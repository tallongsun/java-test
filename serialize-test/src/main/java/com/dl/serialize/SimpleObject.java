package com.dl.serialize;

import java.io.Serializable;

import org.msgpack.annotation.Message;

@Message
public class SimpleObject implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8892222388391288264L;
	private String uid;
    private String status;
    private String path;
    
    public SimpleObject(){}

    public SimpleObject(String uid, String status, String path) {
        this.uid = uid;
        this.status = status;
        this.path = path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((uid == null) ? 0 : uid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleObject other = (SimpleObject) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (uid == null) {
            if (other.uid != null)
                return false;
        } else if (!uid.equals(other.uid))
            return false;
        return true;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SimpleObject [uid=" + uid + ", status=" + status + ", path=" + path + "]";
    }
}
