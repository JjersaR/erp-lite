package com.jersa.configs;

import com.jersa.persistence.aws.model.RAwsS3Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(RAwsS3Properties.class)
@PropertySource(value = "classpath:aws/aws.yml", factory = YamlPropertySourceFactory.class)
public class YmlConfig {

}
