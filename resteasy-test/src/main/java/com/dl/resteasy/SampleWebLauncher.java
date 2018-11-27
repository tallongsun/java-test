package com.dl.resteasy;

//import org.apache.catalina.core.AprLifecycleListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SampleWebLauncher {
//    AprLifecycleListener arpLifecycle = null;

    private boolean enableApr = false;

	public static void main(String[] args) {
		SpringApplication.run(SampleWebLauncher.class, args);
	}

	/**
	 * [linux]
yum install apr-devel
wget https://www.openssl.org/source/openssl-1.0.2e.tar.gz
tar -zxvf openssl-1.0.2e.tar.gz
cd openssl-1.0.2e
./config --prefix=/usr/local/openssl -fPIC
make && make install

cd tomcat-native-1.2.10-src/native
./configure --with-apr=/usr/bin/apr-1-config --with-ssl=/usr/local/openssl --with-java-home=/usr/java/jdk1.8.0_45
make && make install

java -jar -Xmx4096m -Xms4096m -Xmn2g -Djava.security.egd=file:/dev/./u/urandom -Djava.library.path=/usr/local/apr/lib resteasy-test-0.0.1-SNAPSHOT.jar    
	 */
//    @Bean
//    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        if (enableApr) {
//            arpLifecycle = new AprLifecycleListener();
//            tomcat.setProtocol("org.apache.coyote.http11.Http11AprProtocol");
//        }
//        return tomcat;
//    }
}
