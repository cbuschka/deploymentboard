package com.github.cbuschka.poboard.domain.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MockDataProvider
{
	@Value("classpath:mock-data.json")
	private Resource mockDataResource;

	public MockData getMockData()
	{
		try
		{
			return new ObjectMapper().readerFor(MockData.class).readValue(this.mockDataResource.getInputStream());
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
