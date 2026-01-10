package com.coderiverside.quicknote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.coderiverside.quicknote.config.AppInfo;
import com.coderiverside.quicknote.config.SystemLists;
import com.coderiverside.quicknote.config.FeatureSettings;

@SpringBootApplication
@EnableConfigurationProperties({AppInfo.class, SystemLists.class, FeatureSettings.class})
public class QuicknoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuicknoteApplication.class, args);
	}

}
