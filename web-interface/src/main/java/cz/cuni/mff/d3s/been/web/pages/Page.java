package cz.cuni.mff.d3s.been.web.pages;

import static cz.cuni.mff.d3s.been.web.components.Layout.Section;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.api.ClusterConnectionUnavailableException;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.web.services.BeenApiService;

/**
 * User: donarus Date: 4/27/13 Time: 1:05 PM
 */
@Import(library = { "context:js/bootstrap.js" })
public abstract class Page {

	@Inject
	@Property
	protected BeenApiService api;

	@Inject
	protected Logger log;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	public void setupRender() {
		javaScriptSupport.addScript("$(document).on('hover', '.show_tooltip', \n" + " function(){\n " + "   $(this).tooltip('show');\n" + "   $(this).removeClass('show_tooltip');\n" + " });\n");
	}

	public Section getSection() {

		Navigation sectionAnnotation = this.getClass().getAnnotation(Navigation.class);
		if (sectionAnnotation != null) {
			return sectionAnnotation.section();
		}
		return null;
	}

	Object onActivate() {
		if (!api.isConnected()) {
			log.info("Been Api is not connected. Redirecting to Connect page.");
			return Connect.class;
		}
		return null;
	}

	/**
	 * User: donarus Date: 4/28/13 Time: 1:40 PM
	 */

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Navigation {
		public Section section();
	}

	public String getRuntimeLink(String runtimeId) {
		return pageRenderLinkSource.createPageRenderLinkWithContext(
				cz.cuni.mff.d3s.been.web.pages.runtime.Detail.class,
				runtimeId).toString();
	}

	/*
	      Common formatters.
	 */

	public Format getLoadFormat() {
		return new DecimalFormat("#.##");
	}

	public Format getCpuUsageFormat() {
		return new Format() {
			@Override
			public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
				double d = ((double) obj) * 100.0;
				return new StringBuffer(String.format("%.1f", d));
			}

			@Override
			public Object parseObject(String source, ParsePosition pos) {
				return null;
			}
		};
	}

	public Format getIdFormat() {
		return new Format() {
			@Override
			public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
				return new StringBuffer(((String) obj).substring(0, 8));
			}

			@Override
			public Object parseObject(String source, ParsePosition pos) {
				return null;
			}
		};
	}

	public boolean taskRunning(TaskEntry taskEntry) {
		return taskEntry.getState() == TaskState.RUNNING;
	}

	public boolean taskFinished(TaskEntry taskEntry) {
		return taskEntry.getState() == TaskState.FINISHED;
	}

	public boolean taskWaiting(TaskEntry taskEntry) {
		return taskEntry.getState() == TaskState.WAITING;
	}

	public boolean taskBenchmark(TaskEntry taskEntry) {
		return taskEntry.getTaskDescriptor().getType() == TaskType.BENCHMARK;
	}

	public boolean taskDescriptorBenchmark(TaskDescriptor taskDescriptor) {
		return taskDescriptor.getType() == TaskType.BENCHMARK;
	}

	public Date taskLastChanged(TaskEntry taskEntry) {
		java.util.List<StateChangeEntry> logEntries = taskEntry.getStateChangeLog().getLogEntries();
		if (logEntries.size() == 0)
			return null;
		StateChangeEntry entry = logEntries.get(0);
		if (entry.getTimestamp() == 0)
			return null;
		return new Date(entry.getTimestamp());
	}

	public String timestampToString(long timestamp) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(timestamp));
	}

	public String dateToString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public String nanotimeToString(long nanotime) {
		return timestampToString(TimeUnit.NANOSECONDS.toMillis(nanotime));
	}

	public String logLevelToString(int logLevel) {
		if (logLevel == 1)
			return "TRACE";
		if (logLevel == 2)
			return "DEBUG";
		if (logLevel == 3)
			return "INFO";
		if (logLevel == 4)
			return "WARN";
		if (logLevel == 5)
			return "ERROR";
		return Integer.toString(logLevel);
	}

	// stolen from http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
	public String bytesReadable(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public boolean isSwRepositoryOnline() throws BeenApiException {
		return this.api.getApi().isSwRepositoryOnline();
	}

	protected BeenApi getApi() throws ClusterConnectionUnavailableException {
		return this.api.getApi();
	}

}
