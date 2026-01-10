package com.coderiverside.quicknote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.coderiverside.quicknote.config.AppInfo;
import com.coderiverside.quicknote.config.FeatureSettings;
import com.coderiverside.quicknote.config.SystemLists;

@SpringBootApplication
@EnableConfigurationProperties({AppInfo.class, FeatureSettings.class, SystemLists.class})
public class QuicknoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuicknoteApplication.class, args);
	}

}
