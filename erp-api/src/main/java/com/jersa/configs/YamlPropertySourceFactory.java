package com.jersa.configs;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

public class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource source) throws IOException {
    var factory = new YamlPropertiesFactoryBean(); // de yml a properties
    factory.setResources(source.getResource());

    Properties properties = factory.getObject();

    assert properties != null;
    return new PropertiesPropertySource(Objects.requireNonNull(source.getResource().getFilename()), properties); // devolver
  }

}
