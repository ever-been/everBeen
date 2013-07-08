package cz.cuni.mff.d3s.been.swrepository;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.FileUtils;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServerException;

/**
 * A simulation of actual software repository use-cases. Consists in running a
 * {@link SoftwareRepository} server and launching clients against it, testing
 * whether the right responses and files are obtained.
 * 
 * @author darklight
 * 
 */
public class TestSoftwareRepositoryTransportByHTTP extends Assert {

	private class RunningServerStatement extends Statement {
		private final Statement base;
		private final ServerAllocatorRule rule;
		private HttpServer server = null;

		RunningServerStatement(Statement base, ServerAllocatorRule rule) {
			this.base = base;
			this.rule = rule;
		}

		@Override
		public void evaluate() throws Throwable {
			startServer();
			base.evaluate();
			stopServer();
		}

		private void startServer() throws IOException, HttpServerException {
			// find a random free socket
			ServerSocket probeSocket = null;
			probeSocket = new ServerSocket(0);

			final int port = probeSocket.getLocalPort();
			final InetAddress addr = probeSocket.getInetAddress();
			probeSocket.close();
			server = new HttpServer(new InetSocketAddress(addr, port));
			SoftwareStore dataStore = FSBasedStore.createServer(new Properties());
			server.getResolver().register("/bpk*", new BpkRequestHandler(dataStore));
			server.getResolver().register(
					"/artifact*",
					new ArtifactRequestHandler(dataStore));
			server.start();
			rule.host = server.getHost().getHostName();
			rule.port = server.getPort();
		}

		private void stopServer() {
			server.stop();
			server = null;
		}
	}

	private class ServerAllocatorRule implements TestRule {

		private String host;
		private int port;

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		@Override
		public Statement apply(Statement base, Description description) {
			if (description.getMethodName() != null && description.getMethodName().endsWith(
					"_serverDown")) {
				host = "localhost";
				port = 0; // markup for random free port allocation
				return base;
			} else {
				return new RunningServerStatement(base, this);
			}
		}
	}

	/** Root folder of SWRepo's persistence. */
	private static final File SERVER_PERSISTENCE_ROOT_FOLDER = new File(".swrepository");
	/** Root folder of the client's persistence. */
	private static final File CLIENT_PERSISTENCE_ROOT_FOLDER = new File(".swcache");
    /** Software package store */
	private final SoftwareStore dataStore;
    /** Software repository client factory */
	private final SwRepoClientFactory clientFactory;

	public TestSoftwareRepositoryTransportByHTTP() {
		dataStore = FSBasedStore.createCache(new Properties());
		clientFactory = new SwRepoClientFactory(dataStore);
	}

	private SwRepoClient client = null;
	private File randomContentFile = null;
	private BpkIdentifier bpkId = null;
	private ArtifactIdentifier artifactId = null;

	@Rule
	public ServerAllocatorRule serverAllocatorRule = new ServerAllocatorRule();

	/**
	 * Fill test fields.
	 * 
	 * @throws IOException
	 *           On failure when creating test files
	 */
	@Before
	public void fillFields() throws IOException {
		randomContentFile = File.createTempFile(
				"testSwRepoTraffic",
				"randomContent");
		FileWriter fw = new FileWriter(randomContentFile);
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < 1024; ++i) {
			fw.write(random.nextInt());
		}
		fw.flush();
		fw.close();

		bpkId = new BpkIdentifier();
		bpkId.setBpkId("evil-package");
		bpkId.setGroupId("cz.cuni.mff.d3s.been.swrepository.test");
		bpkId.setVersion("0.0.7");

		artifactId = new ArtifactIdentifier();
		artifactId.setArtifactId("bigBaddaBoom");
		artifactId.setGroupId("cz.cuni.mff.d3s.been.swrepository.test");
		artifactId.setVersion("3.2.1...");
	}

	@Before
	public void setUpClient() throws UnknownHostException {
		// assuming JUnit4 runner executes @Rules before @Before methods
		client = clientFactory.getClient(
				serverAllocatorRule.getHost(),
				serverAllocatorRule.getPort());
	}

	/**
	 * Scratch the test fields.
	 */
	@After
	public void scratchFields() {
		randomContentFile = null;
		bpkId = null;
	}

	/**
	 * Clean up the persistence folder.
	 * 
	 * @throws IOException
	 *           When problems come up when cleaning the folder
	 */
	@After
	public void scratchPersistence() throws IOException {
		FileUtils.deleteDirectory(SERVER_PERSISTENCE_ROOT_FOLDER);
		FileUtils.deleteDirectory(CLIENT_PERSISTENCE_ROOT_FOLDER);
	}

	@After
	public void tearDownClient() {
		client = null;
	}

	@Test
	public void testUploadBpk() throws IOException {
		assertTrue(client.putBpk(bpkId, new FileInputStream(randomContentFile)));
		assertFilePresent(
				SERVER_PERSISTENCE_ROOT_FOLDER,
				String.format("%s-%s.bpk", bpkId.getBpkId(), bpkId.getVersion()),
				"bpks",
				randomContentFile,
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());
	}

	@Test
	public void testUploadBpk_duplicateEntry() throws IOException {
		assertTrue(client.putBpk(bpkId, new FileInputStream(randomContentFile)));
		assertFalse(client.putBpk(bpkId, new FileInputStream(randomContentFile)));
	}

	@Test
	public void testUploadBpk_badIdentifier()throws IOException {
		bpkId.setBpkId(null);
		assertFalse(client.putBpk(bpkId, new FileInputStream(randomContentFile)));
	}

	@Test
	public void testUploadBpk_serverDown()throws IOException {
		assertFalse(client.putBpk(bpkId, new FileInputStream(randomContentFile)));
	}

	// test delete bpk that exists
	// test delete bpk that doesn't exist

	@Test
	public void testDownloadBpk() throws IOException {
		File persistedFile = getFileFromPathAndName(
				SERVER_PERSISTENCE_ROOT_FOLDER,
				String.format("%s-%s.bpk", bpkId.getBpkId(), bpkId.getVersion()),
				"bpks",
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());
		persistedFile.getParentFile().mkdirs();
		persistedFile.createNewFile();
		FileWriter fw = new FileWriter(persistedFile);
		final String evilContent = "THIS CONTENT IS EVIL, DON'T READ IT!";
		fw.write(evilContent);
		fw.close();

		Bpk bpk = client.getBpk(bpkId);
		assertNotNull(bpk);
		InputStream bpkIs = bpk.getInputStream();
		final String downloadedContent = IOUtils.toString(bpkIs);
		bpkIs.close();
		assertEquals(evilContent, downloadedContent);
	}

	@Test
	public void testDownloadBpk_badIdentifier() {
		bpkId.setBpkId(null);
		assertNull(client.getBpk(bpkId));
	}

	@Test
	public void testDownloadBpk_serverDown() {
		assertNull(client.getBpk(bpkId));
	}

	@Test
	public void testDownloadBpkInCache_serverDown() throws IOException {
		File fileInCache = getFileFromPathAndName(
				CLIENT_PERSISTENCE_ROOT_FOLDER,
				String.format("%s-%s.bpk", bpkId.getBpkId(), bpkId.getVersion()),
				"bpks",
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());
		fileInCache.getParentFile().mkdirs();
		fileInCache.createNewFile();
		FileWriter fw = new FileWriter(fileInCache);
		final String cacheContent = "I'm in cache and I know it!";
		fw.write(cacheContent);
		fw.close();

		Bpk bpk = client.getBpk(bpkId);
		assertNotNull(bpk);
		InputStream bpkIs = bpk.getInputStream();
		assertNotNull(bpkIs);
		assertEquals(cacheContent, IOUtils.toString(bpkIs));
		bpkIs.close();
	}

	@Test
	public void testDownloadedBpkCaching() throws IOException {
		File serverFile = getFileFromPathAndName(
				SERVER_PERSISTENCE_ROOT_FOLDER,
				String.format("%s-%s.bpk", bpkId.getBpkId(), bpkId.getVersion()),
				"bpks",
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());
		serverFile.getParentFile().mkdirs();
		serverFile.createNewFile();
		FileWriter fw = new FileWriter(serverFile);
		fw.write("I'm a server file and I'm proud of it.");
		fw.close();

		Bpk bpk = client.getBpk(bpkId);
		assertNotNull(bpk);
		InputStream bpkFileStream = bpk.getInputStream();

		File fileInCache = getFileFromPathAndName(
				CLIENT_PERSISTENCE_ROOT_FOLDER,
				String.format("%s-%s.bpk", bpkId.getBpkId(), bpkId.getVersion()),
				"bpks",
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());
		assertNotNull(fileInCache);
		assertTrue(fileInCache.exists());
		assertEquals(
				IOUtils.toString(bpkFileStream),
				FileUtils.fileRead(fileInCache));
	}

	@Test
	public void testDownloadArtifact() throws IOException {
		File serverFile = getFileFromPathAndName(
				SERVER_PERSISTENCE_ROOT_FOLDER,
				String.format(
						"%s-%s.jar",
						artifactId.getArtifactId(),
						artifactId.getVersion()),
				"artifacts",
				artifactId.getGroupId(),
				artifactId.getArtifactId(),
				artifactId.getVersion());
		serverFile.getParentFile().mkdirs();
		serverFile.createNewFile();
		FileWriter fw = new FileWriter(serverFile);
		fw.write("KABOOOOOOOOOOOOOOOOOOOOOOM!!!");
		fw.close();

		Artifact artifact = client.getArtifact(artifactId);
		assertNotNull(artifact);
		assertFalse(artifact.getFile().equals(serverFile));
		assertEquals(
				FileUtils.fileRead(serverFile),
				FileUtils.fileRead(artifact.getFile()));
	}

	@Test
	public void testDownloadArtifact_badIdentifier() {
		artifactId.setArtifactId(null);
		assertNull(client.getArtifact(artifactId));
	}

	@Test
	public void testDownloadArtifact_serverDown() throws IOException {
		File serverFile = getFileFromPathAndName(
				SERVER_PERSISTENCE_ROOT_FOLDER,
				String.format(
						"%s-%s.jar",
						artifactId.getArtifactId(),
						artifactId.getVersion()),
				"artifacts",
				artifactId.getGroupId(),
				artifactId.getArtifactId(),
				artifactId.getVersion());
		serverFile.getParentFile().mkdirs();
		serverFile.createNewFile();
		FileWriter fw = new FileWriter(serverFile);
		fw.write("KABOOOOOOOOOOOOOOOOOOOOOOM!!!");
		fw.close();

		Artifact artifact = client.getArtifact(artifactId);
		assertNull(artifact);
	}

	@Test
	public void testDownloadArtifactInCache_serverDown() throws IOException {
		File cacheFile = getFileFromPathAndName(
				CLIENT_PERSISTENCE_ROOT_FOLDER,
				String.format(
						"%s-%s.jar",
						artifactId.getArtifactId(),
						artifactId.getVersion()),
				"artifacts",
				artifactId.getGroupId(),
				artifactId.getArtifactId(),
				artifactId.getVersion());
		cacheFile.getParentFile().mkdirs();
		cacheFile.createNewFile();
		FileWriter fw = new FileWriter(cacheFile);
		fw.write("KABOOOOOOOOOOOOOOOOOOOOOOM!!!");
		fw.close();

		Artifact artifact = client.getArtifact(artifactId);
		assertNotNull(artifact);
		assertNotNull(artifact.getFile());
		assertTrue(artifact.getFile().exists());
		assertEquals(
				FileUtils.fileRead(cacheFile),
				FileUtils.fileRead(artifact.getFile()));
	}

	@Test
	public void testUploadArtifact() throws IOException {
		client.putArtifact(artifactId, new FileInputStream(randomContentFile));
		File serverItem = getFileFromPathAndName(
				SERVER_PERSISTENCE_ROOT_FOLDER,
				String.format(
						"%s-%s.jar",
						artifactId.getArtifactId(),
						artifactId.getVersion()),
				"artifacts",
				artifactId.getGroupId(),
				artifactId.getArtifactId(),
				artifactId.getVersion());
		assertNotNull(serverItem);
		assertTrue(serverItem.exists());
		assertEquals(
				FileUtils.fileRead(randomContentFile),
				FileUtils.fileRead(serverItem));
	}

	@Test
	public void testUploadArtifact_serverDown() throws Exception  {
		assertFalse(client.putArtifact(artifactId, new FileInputStream(randomContentFile)));
	}

	@Test
	public void testUploadArtifact_duplicateEntry() throws Exception  {
		assertTrue(client.putArtifact(artifactId, new FileInputStream(randomContentFile)));
		assertTrue(client.putArtifact(artifactId, new FileInputStream(randomContentFile))); // will be changed to assertFalse once the behavior has changed to the desired version
	}

	/**
	 * Assert that a file can be found in the server persistence and that its
	 * content is equal to the content of the reference file.
	 * 
	 * @param root
	 *          FS root of the data store we should look in
	 * @param fileName
	 *          The name of the file we're expecting to find
	 * @param storeName
	 *          Name of the store this file lies in
	 * @param referenceFile
	 *          Reference content of the file we're expecting to find
	 * @param groupId
	 *          groupId of tested file
	 * @param itemId
	 *          itemId of tested file
	 * @param itemVersion
	 *          itemVersion of tested file
	 * 
	 * @throws IOException
	 *           On reference or actual file read error
	 */
	public void assertFilePresent(
			File root,
			String fileName,
			String storeName,
			File referenceFile,
			String groupId,
			String itemId,
			String itemVersion) throws IOException {
		final File file = getFileFromPathAndName(
				root,
				fileName,
				storeName,
				groupId,
				itemId,
				itemVersion);
		assertTrue(file.exists());
		final String actualFileContent = FileUtils.fileRead(referenceFile);
		final String referenceFileContent = FileUtils.fileRead(file);
		assertEquals(referenceFileContent, actualFileContent);
	}
	/**
	 * Assert that a file can not be found in the server persistence.
	 * 
	 * @param root
	 *          FS root of the data store we should look in
	 * @param fileName
	 *          Name of the file we're not expecting to find
	 * @param storeName
	 *          Name of the store this file lies in
	 * @param groupId
	 *          groupId of tested file
	 * @param itemId
	 *          itemId of tested file
	 * @param itemVersion
	 *          itemVersion of tested file
	 */
	public void assertFileAbsent(
			File root,
			String fileName,
			String storeName,
			String groupId,
			String itemId,
			String itemVersion) {
		final File file = getFileFromPathAndName(
				root,
				fileName,
				storeName,
				groupId,
				itemId,
				itemVersion);
		assertFalse(file.exists());
	}
	private File getFileFromPathAndName(
			File root,
			String fileName,
			String storeName,
			String groupId,
			String itemId,
			String itemVersion) {

		List<String> pathItemList = new LinkedList<String>();
		for (String grpIdPart : groupId.split("\\.")) {
			pathItemList.add(grpIdPart);
		}
		pathItemList.add(itemId);
		pathItemList.add(itemVersion);
		Path path = FileSystems.getDefault().getPath(
				root.getPath() + File.separator + storeName,
				pathItemList.toArray(new String[pathItemList.size()]));
		return new File(path.toFile(), fileName);
	}
}