package com.example.hello;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

@SpringBootApplication
public class DbFunApplication {

	Logger log = LoggerFactory.getLogger(DbFunApplication.class);

	private JdbcTemplate jdbcTemplate;

	private String userQuery = "SELECT USER() FROM (VALUES(0))";

	@Bean
	public Function<String, String> hello() {
		return (in) -> {
			String dbUser = this.jdbcTemplate.queryForObject(
				userQuery, String.class);
			return "Hello " + in + " from " + dbUser;
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(DbFunApplication.class, args);
	}

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		try {
			Object dbType = JdbcUtils.extractDatabaseMetaData(
				jdbcTemplate.getDataSource(), "getDatabaseProductName");
			log.info("Database product is " + dbType);
			if (dbType.toString().startsWith("MySQL")) {
				this.userQuery = "SELECT USER()";
			}
		} catch (MetaDataAccessException e) {
			log.error("Failed extracting database metadata", e);
		}
	}
}
