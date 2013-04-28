package cz.cuni.mff.d3s.been.web.pages.runtime;

import java.util.Collection;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.RUNTIME_LIST)
public class List extends Page {

	public Collection<RuntimeInfo> getRuntimes() {
		return this.api.getApi().getRuntimes();
	}

	@Property
	private RuntimeInfo runtime;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	public String getDetailLink(String id) {
        return pageRenderLinkSource.createPageRenderLinkWithContext(Detail.class, id).toString();
	}
}
