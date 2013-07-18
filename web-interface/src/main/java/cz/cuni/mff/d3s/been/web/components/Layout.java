package cz.cuni.mff.d3s.been.web.components;

import cz.cuni.mff.d3s.been.web.pages.Overview;
import cz.cuni.mff.d3s.been.web.pages.Page;
import cz.cuni.mff.d3s.been.web.services.BeenApiService;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Import(library = {"context:js/sugar-1.3.5.min.js"})
public class Layout {

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    @Property
    @Parameter(name = "section")
    private Section activeSection;

    @Property
    private Section section;

    @Inject
    @Property
    protected BeenApiService api;

    public boolean isConnected() {
        return api.isConnected();
    }

    public String classNameForSection(Section mySection) {
        if (activeSection != null && activeSection.sectionName.equals(mySection.sectionName)) {
            return "active";
        }

        return "";
    }

    public static final Map<Section, String> links = Collections.synchronizedMap(new HashMap<Section, String>());

    public String getLink(Section mySection) {
        if (!links.containsKey(mySection)) {
            links.put(mySection, pageRenderLinkSource.createPageRenderLink(mySection.page).toString());//toAbsoluteURI().toString());
        }
        return links.get(mySection);
    }

    @Cached
    public java.util.List getAvailableSections() {
        java.util.List listedSections = new ArrayList<>();
        java.util.List sections = new ArrayList<>();
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

    private static Map<String, java.util.List> subsectionsBySectionName = null; // for caching purposes

    public java.util.List getAvailableSubSections(Section section) {
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

        OVERVIEW(Overview.class, "Overview", null),

        CONNECT(cz.cuni.mff.d3s.been.web.pages.Connect.class, "Connect", null, true),

        PACKAGE_LIST(cz.cuni.mff.d3s.been.web.pages.bpkpackage.List.class, "Packages", "List"),

        PACKAGE_UPLOAD(cz.cuni.mff.d3s.been.web.pages.bpkpackage.Upload.class, "Packages", "Upload"),

        ABOUT(cz.cuni.mff.d3s.been.web.pages.About.class, "About", null, true),

        TASK_TREE(cz.cuni.mff.d3s.been.web.pages.task.Tree.class, "Benchmarks & Tasks", "Benchmark tree"),

        TASK_TASKS(cz.cuni.mff.d3s.been.web.pages.task.Tree.class, "Benchmarks & Tasks", "Tasks"),

        TASK_CONTEXTS(cz.cuni.mff.d3s.been.web.pages.task.Tree.class, "Benchmarks & Tasks", "Task contexts"),

        TASK_DETAIL(cz.cuni.mff.d3s.been.web.pages.task.Detail.class, "Benchmarks & Tasks", null),

        TASK_SUBMIT(cz.cuni.mff.d3s.been.web.pages.task.Submit.class, "Benchmarks & Tasks", "Submit new item"),

        RUNTIME_LIST(cz.cuni.mff.d3s.been.web.pages.runtime.List.class, "Runtimes", null),

        RUNTIME_DETAIL(cz.cuni.mff.d3s.been.web.pages.runtime.Detail.class, "Runtimes", null),

        LOGS(cz.cuni.mff.d3s.been.web.pages.Logs.class, "Logs", null, true),

        CONTEXT_DETAIL(cz.cuni.mff.d3s.been.web.pages.context.Detail.class, "Contexts", null, true),

        CONFIGURATION(cz.cuni.mff.d3s.been.web.pages.Configuration.class, "Configuration", null, true);

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
