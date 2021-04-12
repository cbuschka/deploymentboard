package com.github.cbuschka.poboard.domain.auth;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
public class PrivateKeyLoader
{
	public byte[] getAsciiArmoredBytesUTF8(PrivateKeyCredentials credentials)
	{
		try
		{
			String data = credentials.getData();
			if (data == null)
			{
				throw new IllegalArgumentException("Private key has no data.");
			}

			if (data.startsWith("file:"))
			{
				File file = new File(data.substring("file:".length()));
				return Files.readAllBytes(file.toPath());
			}

			return data.getBytes(StandardCharsets.UTF_8);
		}
		catch (IOException ex)
		{
			throw new RuntimeException("Loading private key failed.", ex);
		}
	}
}
