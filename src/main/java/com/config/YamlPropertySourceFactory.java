package com.config;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import java.io.IOException;

public class YamlPropertySourceFactory implements PropertySourceFactory {
   @Override
   public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
       YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
       factory.setResources(resource.getResource());
       return new org.springframework.core.env.PropertiesPropertySource(
           resource.getResource().getFilename(), factory.getObject());
   }
}