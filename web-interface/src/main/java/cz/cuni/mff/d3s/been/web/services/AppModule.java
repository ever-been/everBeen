package cz.cuni.mff.d3s.been.web.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.RequestFilter;
import org.slf4j.Logger;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
public class AppModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(BeenApiService.class, BeenApiServiceImpl.class);
		// binder.bind(MyServiceInterface.class, MyServiceImpl.class);

		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
	}

	public static void contributeFactoryDefaults(
			MappedConfiguration<String, Object> configuration) {
		// The application version number is incorprated into URLs for some
		// assets. Web browsers will cache assets because of the far future expires
		// header. If existing assets are changed, the version number should also
		// change, to force the browser to download new versions. This overrides Tapesty's default
		// (a random hexadecimal number), but may be further overriden by DevelopmentModule or
		// QaModule.
		configuration.override(SymbolConstants.APPLICATION_VERSION, "1.0");
    }

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, Object> configuration) {
		// Contributions to ApplicationDefaults will override any contributions to
		// FactoryDefaults (with the same key). Here we're restricting the supported
		// locales to just "en" (English). As you add localised message catalogs and other assets,
		// you can extend this list of locales (it's a comma separated series of locale names;
		// the first locale name is the default when there's no reasonable match).

		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
        // The plaintext phrase used to set the key for
        // HMAC securing of serialized object data. The
        // default is blank, which causes a runtime alert
        // and console error. You should set this to a
        // reasonably unique, private value, and ensure  s
        // (in a cluster) that all servers use the same
        // value – typically by making a contribution in
        // your applications module class (normally
        // AppModule.java).*/
        configuration.add(SymbolConstants.HMAC_PASSPHRASE, "alI87U3Jzbhdsjf12POksldqmlp9");
	}

	public void contributeRequestHandler(
			OrderedConfiguration<RequestFilter> configuration,
			PageRenderLinkSource pageRenderLinkSource, BeenApiService beenApiService,
			Logger log) {
		configuration.add(
				"ApiConnectionRequestFilter",
				new ApiConnectionRequestFilter(pageRenderLinkSource, beenApiService, log),
				"before:*");

	}
}
