package cz.cuni.mff.d3s.been.web.components;

import java.util.*;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import cz.cuni.mff.d3s.been.web.pages.Page;

public class Layout {

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Property
	@Parameter(name = "section")
	private Section activeSection;

	@Property
	private Section section;

	public String classNameForSection(Section mySection) {
		if (activeSection != null && activeSection.equals(mySection)) {
			return "active";
		}

		return "";
	}

	public static final Map<Section, String> links = Collections.synchronizedMap(new HashMap<Section, String>());

	public String getLink(Section mySection) {
		if (!links.containsKey(mySection)) {
			links.put(mySection, pageRenderLinkSource.createPageRenderLink(mySection.page).toAbsoluteURI().toString());
		}
		return links.get(mySection);
	}

	@Cached
	public List<Section> getAvailableSections() {
		List<String> listedSections = new ArrayList<>();
		List<Section> sections = new ArrayList<>();
		for (Section section : Section.values()) {
            if (section.hideInMenu) {
                continue;
            }
			if (!listedSections.contains(section.sectionName)) {
				sections.add(section);
			}

			listedSections.add(section.sectionName);
		}
		return sections;
	}

	private static Map<String, List<Section>> subsectionsBySectionName = null; // for caching purposes
	public List<Section> getAvailableSubSections(Section section) {
		if (subsectionsBySectionName == null) {
			generateSubSections(); // cache it
		}

		if (activeSection == null) {
			return null;
		}

		return subsectionsBySectionName.get(section.sectionName);
	}

	private void generateSubSections() {
		subsectionsBySectionName = new HashMap<>();
		for (Section subSection : Section.values()) {
			if (subSection.subsectionName != null) {
				if (!subsectionsBySectionName.containsKey(subSection.sectionName)) {
					subsectionsBySectionName.put(subSection.sectionName, new ArrayList<Section>());
				}
				subsectionsBySectionName.get(subSection.sectionName).add(subSection);
			}
		}
	}

	public enum Section {

		OVERVIEW(cz.cuni.mff.d3s.been.web.pages.Index.class, "Overview", null),

		CONNECT(cz.cuni.mff.d3s.been.web.pages.Connect.class, "Connect", null, true),

		PACKAGE_LIST(cz.cuni.mff.d3s.been.web.pages.packages.List.class,
				"Packages", "list"), PACKAGE_UPLOAD(
				cz.cuni.mff.d3s.been.web.pages.packages.Upload.class, "Packages",
				"upload"),

		ABOUT(cz.cuni.mff.d3s.been.web.pages.About.class, "About", null),

		TASK_LIST(cz.cuni.mff.d3s.been.web.pages.Tasks.class, "Tasks", "list"), TASK_DETAIL(
				cz.cuni.mff.d3s.been.web.pages.Task.class, "Tasks", null), TASK_LOGS(
				cz.cuni.mff.d3s.been.web.pages.TaskLogs.class, "Tasks", "logs"),

		RUNTIME_LIST(cz.cuni.mff.d3s.been.web.pages.Runtimes.class, "Runtimes",
				null), RUNTIME_DETAIL(cz.cuni.mff.d3s.been.web.pages.Runtime.class,
				"Runtimes", null),

		LOGS(cz.cuni.mff.d3s.been.web.pages.Logs.class, "Logs", null),

		CONTEXT_LIST(cz.cuni.mff.d3s.been.web.pages.Contexts.class, "Context", null), CONTEXT_DETAIL(
				cz.cuni.mff.d3s.been.web.pages.Context.class, "Context", null),

		CONFIGURATION(cz.cuni.mff.d3s.been.web.pages.Configuration.class,
				"Configuration", null);


		public final Class<? extends Page> page;

		public final String sectionName;

		public final String subsectionName;

        private final boolean hideInMenu;

        Section(Class<? extends Page> page, String sectionName, String subsectionName) {
            this.page = page;
            this.sectionName = sectionName;
            this.subsectionName = subsectionName;
            this.hideInMenu = false;
        }

        Section(Class<? extends Page> page, String sectionName, String subsectionName, boolean hideInMenu) {
            this.page = page;
            this.sectionName = sectionName;
            this.subsectionName = subsectionName;
            this.hideInMenu = hideInMenu;
        }

		public Section fromString(String s) {
			return (s != null) ? Section.valueOf(s.trim().toUpperCase()) : null;
		}

	}
}
