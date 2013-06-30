package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.web.services.BeenApiService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.slf4j.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import static cz.cuni.mff.d3s.been.web.components.Layout.Section;

/**
 * User: donarus Date: 4/27/13 Time: 1:05 PM
 */
public abstract class Page {




	@Inject
	@Property
	protected BeenApiService api;

	@Inject
	protected Logger log;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;



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
		return pageRenderLinkSource.createPageRenderLinkWithContext(cz.cuni.mff.d3s.been.web.pages.runtime.Detail.class, runtimeId).toString();
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
				double d = ((double)obj) * 100.0;
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
				return new StringBuffer(((String)obj).substring(0, 8));
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

	public String taskStateWithIcon(TaskState state) {
		String s;
		if (state == TaskState.RUNNING)
			s =  "<i class=\"icon-play\" style=\"color: green;\"></i>";
		else if (state == TaskState.FINISHED)
			s =  "<i class=\"icon-stop\" style=\"color: gray;\"></i>";
		else if (state == TaskState.WAITING)
			s = "<i class=\"icon-pause\" style=\"color: #eeaa00;\"></i>";
		else
			s = "<i class=\"icon-warning-sign\" style=\"color: red;\"></i>";

		return s + " " + state;
	}

	public String contextStateWithIcon(TaskContextState state) {
		String s;
		if (state == TaskContextState.RUNNING)
			s =  "<i class=\"icon-play\" style=\"color: green;\"></i>";
		else if (state == TaskContextState.FINISHED)
			s =  "<i class=\"icon-stop\" style=\"color: gray;\"></i>";
		else if (state == TaskContextState.WAITING)
			s = "<i class=\"icon-pause\" style=\"color: #eeaa00;\"></i>";
		else
			s = "<i class=\"icon-warning-sign\" style=\"color: red;\"></i>";

		return s + " " + state;
	}
}
