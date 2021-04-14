package com.github.cbuschka.deploymentboard.domain.deployment.extraction;

import com.github.cbuschka.deploymentboard.domain.config.Config;
import com.github.cbuschka.deploymentboard.domain.config.ConfigProvider;
import com.github.cbuschka.deploymentboard.domain.deployment.DeploymentInfo;
import com.github.cbuschka.deploymentboard.domain.deployment.Endpoint;
import com.github.cbuschka.deploymentboard.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class XmlDeploymentInfoExtractionHandler implements DeploymentInfoExtractionHandler
{
	private DocumentBuilderFactory documentBuilderFactory;

	@Autowired
	private ConfigProvider configProvider;

	@PostConstruct
	void init() throws ParserConfigurationException
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setCoalescing(true);
		documentBuilderFactory.setExpandEntityReferences(false);
		documentBuilderFactory.setIgnoringComments(true);
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);
		documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		this.documentBuilderFactory = documentBuilderFactory;
	}

	@Override
	public boolean handles(Endpoint endpoint)
	{
		return "xml".equals(endpoint.getFormat()) || (endpoint.getUrl() != null && endpoint.getUrl().endsWith(".xml"));
	}

	@Override
	public DeploymentInfo extractDeploymentInfoFrom(Endpoint endpoint, InputStream in, String system, String env) throws IOException
	{
		try
		{
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(new InputSource(in));

			Config config = configProvider.getConfig();
			Collector collector = new Collector(Collections.combined(config.settings.getVersionAliases(), config.defaults.versionAliases),
					Collections.combined(config.settings.getCommitishAliases(), config.defaults.commitishAliases),
					Collections.combined(config.settings.getBranchAliases(), config.defaults.branchAliases),
					Collections.combined(config.settings.getBuildTimestampAliases(), config.defaults.buildTimestampAliases));
			walk(doc.getDocumentElement(), collector);

			return collector.toDeploymentInfo(system, env);
		}
		catch (ParserConfigurationException | SAXException e)
		{
			return DeploymentInfo.failure(system, env, "No xml.");
		}
	}

	private void walk(Element element, Collector collector)
	{
		visitElement(element, collector);

		walkAttributes(element, collector);

		walkChildElements(element, collector);
	}

	private void walkChildElements(Element element, Collector collector)
	{
		Node node = element.getFirstChild();
		while (node != null)
		{
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				walk((Element) node, collector);
			}

			node = node.getNextSibling();
		}
	}

	private void visitElement(Element element, Collector collector)
	{
		String key = element.getLocalName();
		String value = getPureTextValueFrom(element);
		if (value != null)
		{
			collector.seen(key, value);
		}
	}

	private String getPureTextValueFrom(Element element)
	{
		StringBuilder buf = new StringBuilder();
		Node node = element.getFirstChild();
		while (node != null)
		{
			if (node.getNodeType() == Node.TEXT_NODE)
			{
				buf.append(node.getNodeValue());
			}
			else if (node.getNodeType() == Node.CDATA_SECTION_NODE)
			{
				buf.append(node.getNodeValue());
			}
			else
			{
				return null;
			}

			node = node.getNextSibling();
		}

		return buf.toString();
	}

	private void walkAttributes(Element element, Collector collector)
	{
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); ++i)
		{
			Attr attr = (Attr) attributes.item(i);
			String key = attr.getLocalName();
			String value = attr.getValue();
			collector.seen(key, value);
		}
	}

	private static class Collector
	{
		private final Set<String> versionAliases;
		private final Set<String> branchAliases;
		private final Set<String> commitishAliases;
		private final Set<String> buildTimestampAliases;

		private String branch;
		private String version;
		private String commitish;
		private String buildTimestamp;

		public <T> Collector(Set<String> versionAliases, Set<String> commitishAliases, Set<String> branchAliases, Set<String> buildTimestampAliases)
		{
			this.versionAliases = versionAliases;
			this.commitishAliases = commitishAliases;
			this.branchAliases = branchAliases;
			this.buildTimestampAliases = buildTimestampAliases;
		}

		public void seen(String key, String value)
		{
			if (this.branchAliases.contains(key))
			{
				this.branch = value;
			}
			else if (this.versionAliases.contains(key))
			{
				this.version = value;
			}
			else if (this.commitishAliases.contains(key))
			{
				this.commitish = value;
			}
			else if (this.buildTimestampAliases.contains(key))
			{
				this.buildTimestamp = value;
			}
		}

		public DeploymentInfo toDeploymentInfo(String system, String env)
		{
			if (version != null || branch != null || commitish != null)
			{
				return DeploymentInfo.available(system, env, commitish, version, branch, this.buildTimestamp);
			}

			return DeploymentInfo.failure(system, env, "No xml.");
		}
	}
}
