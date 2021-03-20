package com.rds.brightrecruitment.configuration;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingConfiguration {

  @Bean
  public ModelMapper modelMapper() {
    final ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setFieldAccessLevel(PRIVATE)
        .setFieldMatchingEnabled(true);
    return modelMapper;
  }

}
