package cz.cuni.mff.d3s.been.web.services;

import java.io.IOException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.beaneditor.DataTypeConstants;
import org.apache.tapestry5.internal.services.BeanBlockSourceImpl;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Order;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.services.*;
import org.slf4j.Logger;

/**
 * Main Web Interface App.
 */
public class AppModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(BeenApiService.class, BeenApiServiceImpl.class);
		binder.bind(LiveFeedService.class, LiveFeedServiceImpl.class).eagerLoad();
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.override(SymbolConstants.APPLICATION_VERSION, "1.0");
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE, "alI87U3Jzbhdsjf12POksldqmlp9");
	}

	public void contributeServiceOverride(MappedConfiguration<Class, Object> configuration,
			@Local RequestExceptionHandler handler) {
		configuration.add(RequestExceptionHandler.class, handler);
	}

	public RequestExceptionHandler buildAppRequestExceptionHandler(final Logger logger, final ResponseRenderer renderer,
			final ComponentSource componentSource) {
		return new RequestExceptionHandler() {
			public void handleRequestException(Throwable exception) throws IOException {
				logger.error("Unexpected runtime exception: " + exception.getMessage(), exception);

				ExceptionReporter reporter = (ExceptionReporter) componentSource.getPage("Exception");
				reporter.reportException(exception);

				renderer.renderPageMarkupResponse("Exception");
			}
		};
	}

}
