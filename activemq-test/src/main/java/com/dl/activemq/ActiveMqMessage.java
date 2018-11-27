package com.dl.activemq;

public class ActiveMqMessage {
    //任务名字
    private String taskName;
    
    //服务名字
    private String serviceName;

    
    
    public ActiveMqMessage(String taskName, String serviceName) {
		this.taskName = taskName;
		this.serviceName = serviceName;
	}

	public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
