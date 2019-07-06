package com.mindtree.cpe.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.mindtree.cpe.entity.JenkinsConfig;
import com.mindtree.cpe.entity.JenkinsJob;
import com.mindtree.cpe.entity.JenkinsNode;
import com.mindtree.cpe.entity.JenkinsPipeline;

/**
 * @author Abhilash Hegde
 *
 */
@Service
public class XmlHandler {

	public String createJobXml(JenkinsJob jenkinsJob, JenkinsConfig jenkinsConfig, String baseUrl) {

		String jobName = jenkinsJob.getName();
		String description = jenkinsJob.getDescription();
		String JMeterScriptPath = jenkinsJob.getJmeterScript();
		String releaseVersion = jenkinsJob.getRelease();
		boolean baseline = jenkinsJob.getBaseline();
		String email = jenkinsJob.getEmail();
		String emailTriggers = "";
		String slave = "  <canRoam>true</canRoam>\r\n";
		String distributedTesting = "";
		String proxy = "";

		if (jenkinsJob.isEnableProxy()) {
			proxy = "-H " + jenkinsJob.getProxyAddress() + " -P " + jenkinsJob.getPort() + " ";
		}

		String successTrigger = "<hudson.plugins.emailext.plugins.trigger.SuccessTrigger>\r\n" + "<email>\r\n"
				+ "<recipientList>" + email + "</recipientList>\r\n" + "<subject>$PROJECT_DEFAULT_SUBJECT</subject>\r\n"
				+ "<body>\r\n" + "${FILE,path=\"" + changeSlash(jenkinsConfig.getJenkinsPath())
				+ "/workspace/${JOB_NAME}/${BUILD_NUMBER}/response.html\"}\r\n" + "${FILE,path=\""
				+ changeSlash(jenkinsConfig.getJenkinsPath())
				+ "/workspace/${JOB_NAME}/${BUILD_NUMBER}/grafana.html\"}\r\n" + "</body>\r\n"
				+ "<recipientProviders>\r\n"
				+ "<hudson.plugins.emailext.plugins.recipients.DevelopersRecipientProvider/>\r\n"
				+ "</recipientProviders>\r\n" + "<attachmentsPattern/>\r\n"
				+ "<attachBuildLog>false</attachBuildLog>\r\n" + "<compressBuildLog>false</compressBuildLog>\r\n"
				+ "<replyTo>$PROJECT_DEFAULT_REPLYTO</replyTo>\r\n" + "<contentType>text/html</contentType>\r\n"
				+ "</email>\r\n" + "</hudson.plugins.emailext.plugins.trigger.SuccessTrigger>\r\n";
		String failureTrigger = "<hudson.plugins.emailext.plugins.trigger.FailureTrigger>\r\n" + "<email>\r\n"
				+ "<recipientList>" + email + "</recipientList>\r\n" + "<subject>$PROJECT_DEFAULT_SUBJECT</subject>\r\n"
				+ "<body>$PROJECT_DEFAULT_CONTENT</body>\r\n" + "<recipientProviders>\r\n"
				+ "<hudson.plugins.emailext.plugins.recipients.DevelopersRecipientProvider/>\r\n"
				+ "</recipientProviders>\r\n" + "<attachmentsPattern/>\r\n"
				+ "<attachBuildLog>false</attachBuildLog>\r\n" + "<compressBuildLog>false</compressBuildLog>\r\n"
				+ "<replyTo>$PROJECT_DEFAULT_REPLYTO</replyTo>\r\n" + "<contentType>project</contentType>\r\n"
				+ "</email>\r\n" + "</hudson.plugins.emailext.plugins.trigger.FailureTrigger>\r\n";

		String beforeBuildTrigger = "<hudson.plugins.emailext.plugins.trigger.PreBuildTrigger>\r\n" + "<email>\r\n"
				+ "<recipientList>" + email + "</recipientList>\r\n" + "<subject>$PROJECT_DEFAULT_SUBJECT</subject>\r\n"
				+ "<body>$PROJECT_DEFAULT_CONTENT</body>\r\n" + "<recipientProviders>\r\n"
				+ "<hudson.plugins.emailext.plugins.recipients.ListRecipientProvider/>\r\n"
				+ "</recipientProviders>\r\n" + "<attachmentsPattern/>\r\n"
				+ "<attachBuildLog>false</attachBuildLog>\r\n" + "<compressBuildLog>false</compressBuildLog>\r\n"
				+ "<replyTo>$PROJECT_DEFAULT_REPLYTO</replyTo>\r\n" + "<contentType>project</contentType>\r\n"
				+ "</email>\r\n" + "</hudson.plugins.emailext.plugins.trigger.PreBuildTrigger>\r\n";

		if (jenkinsJob.isSuccessTrigger())
			emailTriggers = emailTriggers + successTrigger;
		if (jenkinsJob.isBeforebuildTrigger())
			emailTriggers = emailTriggers + beforeBuildTrigger;
		if (jenkinsJob.isFailureTrigger())
			emailTriggers = emailTriggers + failureTrigger;

		if (jenkinsJob.isEnableSlave())
			slave = "  <assignedNode>" + jenkinsJob.getSlave() + "</assignedNode>\r\n"
					+ "  <canRoam>false</canRoam>\r\n";

		if (jenkinsJob.isEnableDistributedT()) {
			String remoteHosts = String.join(",", jenkinsJob.getRemoteHosts());
			distributedTesting = " -R " + remoteHosts;
		}
		System.out.println(slave);

		// System.out.println(emailTriggers);

		String xml = "<?xml version='1.0' encoding='UTF-8'?>\r\n" + "<project>\r\n" + "  <actions/>\r\n"
				+ "  <description>" + description + "</description>\r\n"
				+ "  <keepDependencies>false</keepDependencies>\r\n" + "  <properties>\r\n"
				+ "    <hudson.model.ParametersDefinitionProperty>\r\n" + "      <parameterDefinitions>\r\n"
				+ "        <hudson.model.StringParameterDefinition>\r\n" + "          <name>releaseVersion</name>\r\n"
				+ "          <description></description>\r\n" + "          <defaultValue>" + releaseVersion
				+ "</defaultValue>\r\n" + "        </hudson.model.StringParameterDefinition>\r\n"
				+ "        <hudson.model.BooleanParameterDefinition>\r\n" + "          <name>baseline</name>\r\n"
				+ "          <description></description>\r\n" + "          <defaultValue>" + baseline
				+ "</defaultValue>\r\n" + "        </hudson.model.BooleanParameterDefinition>\r\n"
				+ "      </parameterDefinitions>\r\n" + "    </hudson.model.ParametersDefinitionProperty>\r\n"
				+ "  </properties>\r\n" + "  <scm class=\"hudson.scm.NullSCM\"/>\r\n" + slave
				+ "  <disabled>false</disabled>\r\n"
				+ "  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\r\n"
				+ "  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\r\n" + "  <triggers/>\r\n"
				+ "  <concurrentBuild>false</concurrentBuild>\r\n" + "  <builders>\r\n"
				+ "    <hudson.tasks.BatchFile>\r\n" + "      <command>cd %WORKSPACE%&#xd;\r\n"
				+ "mkdir %BUILD_NUMBER%</command>\r\n" + "    </hudson.tasks.BatchFile>\r\n"
				+ "    <jenkins.plugins.http__request.HttpRequest plugin=\"http_request@1.8.22\">\r\n"
				+ "		<url>\r\n" + baseUrl + "/buildStart?jobName=${JOB_NAME}\r\n" + "		</url>\r\n"
				+ "		<ignoreSslErrors>false</ignoreSslErrors>\r\n" + "		<httpMode>POST</httpMode>\r\n"
				+ "		<httpProxy/>\r\n" + "		<passBuildParameters>false</passBuildParameters>\r\n"
				+ "		<validResponseCodes>100:399</validResponseCodes>\r\n"
				+ "		<validResponseContent>success</validResponseContent>\r\n"
				+ "		<acceptType>TEXT_HTML</acceptType>\r\n" + "		<contentType>TEXT_HTML</contentType>\r\n"
				+ "		<outputFile/>\r\n" + "		<timeout>100</timeout>\r\n"
				+ "		<consoleLogResponseBody>false</consoleLogResponseBody>\r\n" + "		<quiet>false</quiet>\r\n"
				+ "		<authentication/>\r\n" + "		<requestBody/>\r\n"
				+ "		<customHeaders class=\"empty-list\"/>\r\n" + "	</jenkins.plugins.http__request.HttpRequest>"
				+ "    <hudson.tasks.BatchFile>\r\n" + "      <command>jmeter " + proxy + "-n -t " + JMeterScriptPath
				+ distributedTesting + " -l %WORKSPACE%\\%BUILD_NUMBER%\\jmeter-report.jtl</command>\r\n"
				+ "    </hudson.tasks.BatchFile>\r\n"
				+ "	 	<jenkins.plugins.http__request.HttpRequest plugin=\"http_request@1.8.22\">\r\n"
				+ "		<url>\r\n" + baseUrl + "/buildCompleted?jobName=${JOB_NAME}\r\n" + "		</url>\r\n"
				+ "		<ignoreSslErrors>false</ignoreSslErrors>\r\n" + "		<httpMode>GET</httpMode>\r\n"
				+ "		<httpProxy/>\r\n" + "		<passBuildParameters>false</passBuildParameters>\r\n"
				+ "		<validResponseCodes>100:399</validResponseCodes>\r\n"
				+ "		<validResponseContent></validResponseContent>\r\n"
				+ "		<acceptType>TEXT_PLAIN</acceptType>\r\n" + "		<contentType>TEXT_PLAIN</contentType>\r\n"
				+ "		<outputFile>${WORKSPACE}/${BUILD_NUMBER}/grafana.html</outputFile>\r\n"
				+ "		<timeout>100</timeout>\r\n" + "		<consoleLogResponseBody>false</consoleLogResponseBody>\r\n"
				+ "		<quiet>false</quiet>\r\n" + "		<authentication/>\r\n" + "		<requestBody/>\r\n"
				+ "		<customHeaders class=\"empty-list\"/>\r\n" + "	</jenkins.plugins.http__request.HttpRequest>"
				+ "    <hudson.tasks.BatchFile>\r\n" + "      <command>" + jenkinsConfig.getJmeterHome()
				+ "\\bin\\JMeterPluginsCMD.bat --generate-csv %WORKSPACE%\\%BUILD_NUMBER%\\result.csv --input-jtl %WORKSPACE%\\%BUILD_NUMBER%\\jmeter-report.jtl --plugin-type SynthesisReport</command>\r\n"
				+ "    </hudson.tasks.BatchFile>\r\n"
				+ "    <jenkins.plugins.http__request.HttpRequest plugin=\"http_request@1.8.22\">\r\n" + "      <url>"
				+ jenkinsConfig.getJmf()
				+ "/uploadapi?application=${JOB_NAME}&amp;domain=cpe&amp;release=${releaseVersion}&amp;baseline=${baseline}&amp;testtype=load&amp;testname=${BUILD_NUMBER}&amp;filepath="
				+ encode(jenkinsConfig.getJenkinsPath()) + "%5Cworkspace%5C" + jobName
				+ "%5C${BUILD_NUMBER}%5Cresult.csv</url>\r\n" + "      <ignoreSslErrors>false</ignoreSslErrors>\r\n"
				+ "      <httpMode>GET</httpMode>\r\n" + "      <httpProxy></httpProxy>\r\n"
				+ "      <passBuildParameters>false</passBuildParameters>\r\n"
				+ "      <validResponseCodes>100:500</validResponseCodes>\r\n"
				+ "      <validResponseContent></validResponseContent>\r\n"
				+ "      <acceptType>NOT_SET</acceptType>\r\n" + "      <contentType>NOT_SET</contentType>\r\n"
				+ "      <outputFile>${WORKSPACE}/${BUILD_NUMBER}/response.html</outputFile>\r\n"
				+ "      <timeout>0</timeout>\r\n" + "      <consoleLogResponseBody>true</consoleLogResponseBody>\r\n"
				+ "      <quiet>false</quiet>\r\n" + "      <authentication></authentication>\r\n"
				+ "      <requestBody></requestBody>\r\n" + "      <customHeaders class=\"empty-list\"/>\r\n"
				+ "    </jenkins.plugins.http__request.HttpRequest>\r\n" + "  </builders>\r\n" + "<publishers>\r\n"
				+ "<hudson.plugins.emailext.ExtendedEmailPublisher plugin=\"email-ext@2.61\">\r\n" + "<recipientList>"
				+ email + "</recipientList>\r\n" + "<configuredTriggers>\r\n" + emailTriggers
				+ "</configuredTriggers>\r\n" + "<contentType>text/html</contentType>\r\n"
				+ "<defaultSubject>$DEFAULT_SUBJECT</defaultSubject>\r\n" + "<defaultContent>\r\n" + "${FILE,path=\""
				+ changeSlash(jenkinsConfig.getJenkinsPath())
				+ "/workspace/${JOB_NAME}/${BUILD_NUMBER}/response.html\"}\r\n" + "${FILE,path=\""
				+ changeSlash(jenkinsConfig.getJenkinsPath())
				+ "/workspace/${JOB_NAME}/${BUILD_NUMBER}/grafana.html\"}\r\n" + "</defaultContent>\r\n"
				+ "<attachmentsPattern/>\r\n" + "<presendScript>$DEFAULT_PRESEND_SCRIPT</presendScript>\r\n"
				+ "<postsendScript>$DEFAULT_POSTSEND_SCRIPT</postsendScript>\r\n"
				+ "<attachBuildLog>false</attachBuildLog>\r\n" + "<compressBuildLog>false</compressBuildLog>\r\n"
				+ "<replyTo>$PROJECT_DEFAULT_REPLYTO</replyTo>\r\n" + "<saveOutput>false</saveOutput>\r\n"
				+ "<disabled>false</disabled>\r\n" + "</hudson.plugins.emailext.ExtendedEmailPublisher>\r\n"
				+ "</publishers>\r\n" + "<buildWrappers/>\r\n" + "</project>";
		return xml;
	}

	public static String encode(String url) {
		try {
			String encodeURL = URLEncoder.encode(url, "UTF-8");
			return encodeURL;
		} catch (UnsupportedEncodingException e) {
			return "Issue while encoding" + e.getMessage();
		}
	}

	public static String changeSlash(String str) {
		String newStr = str.replace("\\", "/");
		return newStr;
	}

	public JenkinsJob parseXml(StringBuffer response, String jobName)
			throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		Pattern pattern;
		Matcher matcher;
		JenkinsJob jenkinsJob = new JenkinsJob();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(response.toString())));

		// get release version

		NodeList property1 = ((Element) doc.getElementsByTagName("properties").item(0))
				.getElementsByTagName("hudson.model.ParametersDefinitionProperty");
		NodeList property2 = ((Element) property1.item(0))
				.getElementsByTagName("hudson.model.StringParameterDefinition");
		NodeList releaseNodelist = ((Element) property2.item(0)).getElementsByTagName("defaultValue");
		String release = ((Element) releaseNodelist.item(0)).getChildNodes().item(0).getNodeValue().trim();
		jenkinsJob.setRelease(release);

		// get baseline
		NodeList property3 = ((Element) property1.item(0))
				.getElementsByTagName("hudson.model.BooleanParameterDefinition");
		NodeList booleanNodelist = ((Element) property3.item(0)).getElementsByTagName("defaultValue");
		String baseline = ((Element) booleanNodelist.item(0)).getChildNodes().item(0).getNodeValue().trim();
		if (baseline.equals("true")) {
			jenkinsJob.setBaseline(true);
		} else {
			jenkinsJob.setBaseline(false);
		}

		// get description

		NodeList descriptionNodeList = doc.getElementsByTagName("description");

		String description = null;
		try {
			description = (descriptionNodeList.item(0)).getChildNodes().item(0).getNodeValue().trim();
		} catch (NullPointerException e) {
			description = new String("");
		}

		jenkinsJob.setDescription(description);

		NodeList builderList = doc.getElementsByTagName("builders");
		Element element = (Element) builderList.item(0);

		// get jmx path and remotehosts
		NodeList batchFileList = element.getElementsByTagName("hudson.tasks.BatchFile");
		Element batchFile = (Element) batchFileList.item(1);
		NodeList commandList = batchFile.getElementsByTagName("command");
		Element firstWd1Element = (Element) commandList.item(0);
		NodeList textWdList = firstWd1Element.getChildNodes();
		System.out.println(((Node) textWdList.item(0)).getNodeValue().trim());
		String jmeterScriptPath = ((Node) textWdList.item(0)).getNodeValue().trim();

		System.out.println("jmeterScriptPath = " + jmeterScriptPath);


		String jmeterProxy = jmeterScriptPath.substring(jmeterScriptPath.indexOf("jmeter") + 6, jmeterScriptPath.indexOf("-n"))
				.trim();

		if (jmeterProxy.length() != 0) {
			String proxyAddress = jmeterScriptPath
					.substring(jmeterScriptPath.indexOf("-H") + 2, jmeterScriptPath.indexOf("-P")).trim();
			// System.out.println("proxyAddress = "+proxyAddress);

			String port = jmeterScriptPath.substring(jmeterScriptPath.indexOf("-P") + 2, jmeterScriptPath.indexOf("-n"))
					.trim();
			// System.out.println("port = "+port);
			
			jenkinsJob.setProxyAddress(proxyAddress);
			jenkinsJob.setPort(Integer.parseInt(port));
			jenkinsJob.setEnableProxy(true);
		} else {
			jenkinsJob.setProxyAddress("");
			jenkinsJob.setPort(Integer.parseInt("0"));
			jenkinsJob.setEnableProxy(false);
		}

		pattern = Pattern.compile("-t\\s(.*).jmx");
		matcher = pattern.matcher(jmeterScriptPath);
		if (matcher.find()) {
			jenkinsJob.setJmeterScript(matcher.group(1) + ".jmx");
			System.out.println(matcher.group(1));
		} else {
			jenkinsJob.setJmeterScript("");
		}

		pattern = Pattern.compile("\\s-R\\s(.*)\\s-l\\s");
		matcher = pattern.matcher(jmeterScriptPath);
		if (matcher.find()) {
			List<String> remoteHosts = Arrays.asList(matcher.group(1).split("\\s*,\\s*"));
			jenkinsJob.setRemoteHosts(remoteHosts);
			jenkinsJob.setEnableDistributedT(true);
		} else {
			jenkinsJob.setEnableDistributedT(false);
		}

		// email & triggers
		NodeList publishers = doc.getElementsByTagName("publishers");
		Element email = (Element) ((Element) publishers.item(0))
				.getElementsByTagName("hudson.plugins.emailext.ExtendedEmailPublisher").item(0);

		// get email address
		if (email != null) {
			Node recipientNode = email.getElementsByTagName("recipientList").item(0);
			NodeList recipient = ((Element) recipientNode).getChildNodes();
			String emailRecipient = ((Node) recipient.item(0)).getNodeValue().trim();
			jenkinsJob.setEmail(emailRecipient);

			// get triggers
			NodeList triggerList = ((Element) email.getElementsByTagName("configuredTriggers").item(0)).getChildNodes();
			for (int i = 0; i < triggerList.getLength(); i++) {
				String triggerName = triggerList.item(i).getNodeName();
				if (triggerName.equalsIgnoreCase("hudson.plugins.emailext.plugins.trigger.SuccessTrigger")) {
					jenkinsJob.setSuccessTrigger(true);
				}
				if (triggerName.equalsIgnoreCase("hudson.plugins.emailext.plugins.trigger.FailureTrigger")) {
					jenkinsJob.setFailureTrigger(true);
				}
				if (triggerName.equalsIgnoreCase("hudson.plugins.emailext.plugins.trigger.PreBuildTrigger")) {
					jenkinsJob.setBeforebuildTrigger(true);
				}
			}

		} else {
			jenkinsJob.setEmail("");
			jenkinsJob.setSuccessTrigger(false);
			jenkinsJob.setFailureTrigger(false);
			jenkinsJob.setBeforebuildTrigger(false);

		}

		// get slave
		NodeList canRoam = doc.getElementsByTagName("canRoam");
		String slaveEnabled = (canRoam.item(0)).getChildNodes().item(0).getNodeValue().trim();
		if (slaveEnabled == "false") {
			NodeList assignedNode = doc.getElementsByTagName("assignedNode");
			String node = (assignedNode.item(0)).getChildNodes().item(0).getNodeValue().trim();
			jenkinsJob.setSlave(node);
			jenkinsJob.setEnableSlave(true);
		} else {
			jenkinsJob.setEnableSlave(false);
			jenkinsJob.setSlave("");
		}

		jenkinsJob.setName(jobName);
		return jenkinsJob;
	}

	public String createPipelineXml(JenkinsPipeline jenkinsPipeline) throws TransformerException {
		String script = "node { stage ('Performance Test') { build job: '" + jenkinsPipeline.getJobName()
				+ "', parameters: [string(name: 'releaseVersion', value: '" + jenkinsPipeline.getRelease() + "')] } }";
		String pipeline = "<?xml version=\"1.0\"?>\r\n" + "<flow-definition plugin=\"workflow-job@2.17\">\r\n"
				+ "  <actions/>\r\n" + "  <description/>\r\n" + "  <keepDependencies>false</keepDependencies>\r\n"
				+ "  <properties>\r\n"
				+ "    <org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty>\r\n"
				+ "      <triggers/>\r\n"
				+ "    </org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty>\r\n"
				+ "  </properties>\r\n"
				+ "  <definition class=\"org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition\" plugin=\"workflow-cps@2.45\">\r\n"
				+ "    <script>\r\n" + script + "\r\n" + "</script>\r\n" + "    <sandbox>true</sandbox>\r\n"
				+ "  </definition>\r\n" + "  <triggers/>\r\n" + "  <disabled>false</disabled>\r\n"
				+ "</flow-definition>";

		return pipeline;
	}

	public String updatePipelineXml(JenkinsPipeline jenkinsPipeline, String xmlResponse) throws TransformerException {
		Pattern pattern;
		Matcher matcher;
		String whiteSpace = " ";
		String performanceStage = "stage ('Performance Test') { build job: '" + jenkinsPipeline.getJobName() + "',"
				+ " parameters: [string(name: 'releaseVersion', value: '" + jenkinsPipeline.getRelease() + "')] }";

		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader strReader = new StringReader(xmlResponse);
			InputSource is = new InputSource(strReader);
			doc = (Document) builder.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

		NodeList definition = doc.getElementsByTagName("definition");
		Node node = ((Element) definition.item(0)).getElementsByTagName("script").item(0);
		Element script = (Element) node;
		String pipelineScript = "node { ";

		if (jenkinsPipeline.getStageRef() != null || jenkinsPipeline.getWhere() != null) {
			if (!jenkinsPipeline.getWhere().equalsIgnoreCase("starting")) {
				String groovyScript = script.getChildNodes().item(0).getNodeValue().trim();
				pattern = Pattern.compile("\\{(.*)\\}");
				matcher = pattern.matcher(groovyScript);
				String insideNode = null;
				if (matcher.find()) {
					insideNode = matcher.group(1).trim();
				}
				String[] stages = insideNode.split("(stage)");
				for (int i = 1; i < stages.length; i++) {
					stages[i] = "stage " + stages[i].trim();
				}

				pattern = Pattern.compile("stage\\s*\\(\\s*'(.*?)'\\s*\\)");
				for (int i = 1; i < stages.length; i++) {
					matcher = pattern.matcher(stages[i]);
					if (matcher.find()) {

						if (jenkinsPipeline.getStageRef().equalsIgnoreCase(matcher.group(1).trim())) {
							if (jenkinsPipeline.getWhere().equalsIgnoreCase("after")) {
								pipelineScript = pipelineScript + stages[i] + whiteSpace + performanceStage
										+ whiteSpace;
							} else {
								pipelineScript = pipelineScript + performanceStage + whiteSpace + stages[i]
										+ whiteSpace;
							}

						} else {
							pipelineScript = pipelineScript + stages[i] + whiteSpace;
						}

					}
				}

			} else {
				pipelineScript = pipelineScript + performanceStage;
			}

		} else {
			String groovyScript = script.getChildNodes().item(0).getNodeValue().trim();
			pattern = Pattern.compile("\\{(.*)\\}");
			matcher = pattern.matcher(groovyScript);
			String insideNode = null;
			if (matcher.find()) {
				insideNode = matcher.group(1).trim();
			}
			String[] stages = insideNode.split("(stage)");
			for (int i = 1; i < stages.length; i++) {
				stages[i] = "stage " + stages[i].trim();
			}
			pattern = Pattern.compile("stage\\s*\\(\\s*'(.*?)'\\s*\\)");
			for (int i = 1; i < stages.length; i++) {
				matcher = pattern.matcher(stages[i]);
				if (matcher.find()) {
					if (matcher.group(1).trim().equalsIgnoreCase("Performance Test")) {
						pipelineScript = pipelineScript + performanceStage + whiteSpace;
					} else {
						pipelineScript = pipelineScript + stages[i] + whiteSpace;
					}

				}
			}
		}

		System.out.println(pipelineScript);
		// node.setNodeValue(s);

		pipelineScript = pipelineScript + " }";
		if (script.getChildNodes().item(0) != null) {
			script.getChildNodes().item(0).setNodeValue(pipelineScript);
		} else {
			script.appendChild(doc.createTextNode(pipelineScript));
		}

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		String output = writer.getBuffer().toString();
		System.out.println(output);

		return output;
	}

	public JenkinsPipeline parsePipelineXml(String response, String pipelineName)
			throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		Pattern pattern;
		Matcher matcher;
		JenkinsPipeline jenkinsPipeline = new JenkinsPipeline();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(response)));

		NodeList definition = doc.getElementsByTagName("definition");
		Element script = (Element) ((Element) definition.item(0)).getElementsByTagName("script").item(0);
		jenkinsPipeline.setName(pipelineName);
		String groovyScript = script.getChildNodes().item(0).getNodeValue().trim();
		pattern = Pattern.compile("build job:\\s'(.*?)',");
		matcher = pattern.matcher(groovyScript);
		if (matcher.find()) {
			jenkinsPipeline.setJobName(matcher.group(1));
			System.out.println(matcher.group(1));
		}

		pattern = Pattern.compile("'releaseVersion', value:\\s'(.*?)'\\)");
		matcher = pattern.matcher(groovyScript);
		if (matcher.find()) {
			jenkinsPipeline.setRelease(matcher.group(1));
			System.out.println(matcher.group(1));
		}
		return jenkinsPipeline;
	}

	public ArrayList<String> parsePipelineStagesXml(String response)
			throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		Pattern pattern;
		Matcher matcher;
		ArrayList<String> pipelineStages = new ArrayList<String>();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(response)));

		NodeList definition = doc.getElementsByTagName("definition");
		Element script = (Element) ((Element) definition.item(0)).getElementsByTagName("script").item(0);
		if (script.getChildNodes().getLength() > 0) {
			String groovyScript = script.getChildNodes().item(0).getNodeValue().trim();
			pattern = Pattern.compile("stage\\s*\\(\\s*'(.*?)'\\s*\\)");
			matcher = pattern.matcher(groovyScript);
			while (matcher.find()) {
				pipelineStages.add(matcher.group(1));
				System.out.println(matcher.group(1));
			}
		}

		return pipelineStages;
	}

	public JenkinsNode parseNodeXml(StringBuffer response, String nodeName) {
		JenkinsNode jenkinsNode = new JenkinsNode();
		jenkinsNode.setName(nodeName);
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(response.toString())));

			NodeList description = doc.getElementsByTagName("description");

			String nodeDescription = (description.item(0)).getChildNodes().item(0).getNodeValue().trim();
			jenkinsNode.setDescription(nodeDescription);

			NodeList remoteFS = doc.getElementsByTagName("remoteFS");
			String rootDirectory = (remoteFS.item(0)).getChildNodes().item(0).getNodeValue().trim();
			jenkinsNode.setRootDirectory(rootDirectory);
			System.out.println(nodeDescription + "--" + rootDirectory);
		} catch (SAXException | IOException | ParserConfigurationException | FactoryConfigurationError e) {
			e.printStackTrace();
		}
		return jenkinsNode;
	}

	public String createNodeJson(JenkinsNode node) {
		String json = "json={\r\n" + "	'name': '" + node.getName() + "',\r\n" + "	'nodeDescription': '"
				+ node.getDescription() + "',\r\n" + "	'numExecutors': '2',\r\n" + "	'remoteFS': '"
				+ node.getRootDirectory() + "',\r\n" + "	'labelString': 'labels',\r\n" + "	'mode': 'NORMAL',\r\n"
				+ "	'': ['hudson.slaves.JNLPLauncher'],\r\n" + "	'launcher': {\r\n"
				+ "	'stapler-class': 'hudson.slaves.JNLPLauncher',\r\n" + "	'class': 'hudson.slaves.JNLPLauncher',\r\n"
				+ "		'workDirSettings': {\r\n" + "			'disabled': 'false',\r\n"
				+ "			'internalDir': 'remoting',\r\n" + "			'failIfWorkDirIsMissing': 'false'\r\n"
				+ "		}\r\n" + "	},\r\n" + "	'nodeProperties': {\r\n" + "		'stapler-class-bag': true,\r\n"
				+ "		'com-cloudbees-jenkins-plugins-nodesplus-OwnerNodeProperty': {\r\n"
				+ "			'owners': 'test@test.com',\r\n" + "			'onOnline': true,\r\n"
				+ "			'onOffline': true,\r\n" + "			'onLaunchFailure': true,\r\n"
				+ "			'onFirstLaunchFailure': true,\r\n" + "			'onTemporaryOfflineApplied': true,\r\n"
				+ "			'onTemporaryOfflineRemoved': true\r\n" + "		}\r\n" + "	}\r\n" + "}";
		return json;
	}

	public String createNodeXml(JenkinsNode node) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "<slave>\r\n" + "  <name>" + node.getName()
				+ "</name>\r\n" + "  <description>" + node.getDescription() + "</description>\r\n" + "  <remoteFS>"
				+ node.getRootDirectory() + "</remoteFS>\r\n" + "  <numExecutors>2</numExecutors>\r\n"
				+ "  <mode>NORMAL</mode>\r\n" + "  <launcher class=\"hudson.slaves.JNLPLauncher\">\r\n"
				+ "    <workDirSettings>\r\n" + "      <disabled>false</disabled>\r\n"
				+ "      <internalDir>remoting</internalDir>\r\n"
				+ "      <failIfWorkDirIsMissing>false</failIfWorkDirIsMissing>\r\n" + "    </workDirSettings>\r\n"
				+ "  </launcher>\r\n" + "  <label>labels</label>\r\n" + "  <nodeProperties/>\r\n" + "</slave>";
		return xml;
	}
}