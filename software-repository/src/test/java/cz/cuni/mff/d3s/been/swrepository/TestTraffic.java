package cz.cuni.mff.d3s.been.swrepository;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.datastore.DataStore;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;
import cz.cuni.mff.d3s.been.swrepository.httpserver.HttpServer;

/**
 * A simulation of actual software repository use-cases. Consists in running a
 * {@link SoftwareRepository} server and launching clients against it, testing
 * whether the right responses and files are obtained.
 * 
 * @author darklight
 * 
 */
public class TestTraffic {

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

		private void startServer() throws IOException {
			// find a random free socket
			ServerSocket probeSocket = null;
			probeSocket = new ServerSocket(0);

			final int port = probeSocket.getLocalPort();
			final InetAddress addr = probeSocket.getInetAddress();
			probeSocket.close();
			server = new HttpServer(addr, port);
			DataStore dataStore = new FSBasedStore(SERVER_PERSISTENCE_ROOT_FOLDER);
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
	private static final File SERVER_PERSISTENCE_ROOT_FOLDER = new File(".server-persistence");
	/** Root folder of the client's persistence. */
	private static final File CLIENT_PERSISTENCE_ROOT_FOLDER = new File(".client-persistence");
	private final DataStore dataStore;
	private final SwRepoClientFactory clientFactory;

	public TestTraffic() {
		dataStore = new FSBasedStore(CLIENT_PERSISTENCE_ROOT_FOLDER);
		clientFactory = new SwRepoClientFactory(dataStore);
	}

	private SwRepoClient client = null;
	private File randomContentFile = null;
	private BpkIdentifier bpkId = null;

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
		bpkId = new BpkIdentifier();
		bpkId.setBpkId("evil-package");
		bpkId.setGroupId("cz.cuni.mff.d3s.been.swrepository.test");
		bpkId.setVersion("0.0.7");
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
		assertTrue(client.putBpk(bpkId, randomContentFile));
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
	public void testUploadBpk_overwrite() throws IOException {
		assertTrue(client.putBpk(bpkId, randomContentFile));
		// reset fields - generates same identifier but different content
		fillFields();
		assertTrue(client.putBpk(bpkId, randomContentFile));
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
	public void testUploadBpk_fileDoesntExist() {
		randomContentFile.delete();
		assertFalse(client.putBpk(bpkId, randomContentFile));
		assertFileAbsent(
				SERVER_PERSISTENCE_ROOT_FOLDER,
				String.format("%s-%s.bpk", bpkId.getBpkId(), bpkId.getVersion()),
				"bpks",
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());
	}

	@Test
	public void testUploadBpk_essentialIdentifiersNull() {
		bpkId.setBpkId(null);
		assertFalse(client.putBpk(bpkId, randomContentFile));
	}

	@Test
	public void testUploadBpk_serverDown() {
		assertFalse(client.putBpk(bpkId, randomContentFile));
		assertFileAbsent(
				SERVER_PERSISTENCE_ROOT_FOLDER,
				String.format("%s-%s.bpk", bpkId.getBpkId(), bpkId.getVersion()),
				"bpks",
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());
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
		final String downloadedContent = FileUtils.fileRead(bpk.getFile());
		assertEquals(evilContent, downloadedContent);
	}

	@Test
	public void testDownloadBpk_essentialIdentifiersNull() {
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
		assertNotNull(bpk.getFile());
		assertTrue(bpk.getFile().exists());
		assertEquals(cacheContent, FileUtils.fileRead(bpk.getFile()));
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
		assertNotNull(bpk.getFile());
		File bpkFile = bpk.getFile();
		assertTrue(bpkFile.exists());

		File fileInCache = getFileFromPathAndName(
				CLIENT_PERSISTENCE_ROOT_FOLDER,
				String.format("%s-%s.bpk", bpkId.getBpkId(), bpkId.getVersion()),
				"bpks",
				bpkId.getGroupId(),
				bpkId.getBpkId(),
				bpkId.getVersion());
		assertNotNull(fileInCache);
		assertTrue(fileInCache.exists());
		assertEquals(FileUtils.fileRead(bpkFile), FileUtils.fileRead(fileInCache));
	}
	// test download artifact
	// test download artifact bad identifier
	// test download artifact server down
	// test upload artifact
	// test upload artifact file doesn't exist
	// test upload artifact with parts of artifact null
	// test upload artifact server down
	// test delete artifact that exists
	// test delete artifact that doesn't exist

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
	 * @param pathItems
	 *          The Names of the file's path items within the persistence folder
	 * 
	 * @throws IOException
	 *           On reference or actual file read error
	 */
	public void assertFilePresent(
			File root,
			String fileName,
			String storeName,
			File referenceFile,
			String... pathItems) throws IOException {
		final File file = getFileFromPathAndName(
				root,
				fileName,
				storeName,
				pathItems);
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
	 * @param pathItems
	 *          Names of the file's path items within the persistence folder
	 */
	public void assertFileAbsent(
			File root,
			String fileName,
			String storeName,
			String... pathItems) {
		final File file = getFileFromPathAndName(
				root,
				fileName,
				storeName,
				pathItems);
		assertFalse(file.exists());
	}

	private File getFileFromPathAndName(
			File root,
			String fileName,
			String storeName,
			String... pathItems) {
		Path path = FileSystems.getDefault().getPath(
				root.getPath() + File.separator + storeName,
				pathItems);
		return new File(path.toFile(), fileName);
	}
}
