package com.dl.db.perf.conf;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfiguration {
    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.cluster.name}")
    private String clusterName;

    @Bean
    public Client getClient() throws UnknownHostException {
        TransportClient client = TransportClient.builder().settings(
                Settings.settingsBuilder().put("cluster.name", clusterName).build()).build();
        String[] hosts = host.split(",");
        for (String pair : hosts) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(parts[0].trim()),
                        Integer.parseInt(parts[1])));
            }
        }
        return client;
    }
}
