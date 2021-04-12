package com.github.cbuschka.poboard.domain.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MockDataProvider
{
	@Value("classpath:mock-data.yaml")
	private Resource mockDataResource;

	public MockData getMockData()
	{
		try
		{
			MockData mockData = new ObjectMapper(new YAMLFactory()).readerFor(MockData.class).readValue(this.mockDataResource.getInputStream());

			return mockData;
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
