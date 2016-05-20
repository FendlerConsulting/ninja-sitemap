/*
 * Copyright 2016 Fendler Consulting cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jensfendler.ninjasitemap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import ninja.utils.NinjaProperties;

/**
 * @author Jens Fendler
 *
 */
public class SitemapRouteDetailsProvider implements Provider<SitemapRouteDetails> {

	protected static final Logger LOG = LoggerFactory.getLogger(SitemapRouteDetailsProvider.class);

	protected static final String KEY_SITEMAP_ROUTE_DETAILS_IMPL = "ninja.sitemap.routeDetailsProvider";

	protected static final String DEFAULT_SITEMAP_ROUTE_DETAILS_CLASSNAME = "com.jensfendler.ninjasitemap.SimpleSitemapRouteDetails";

	@Inject
	protected NinjaProperties ninjaProperties;

	/**
	 * @see com.google.inject.Provider#get()
	 */
	public SitemapRouteDetails get() {
		String sdpClassName = ninjaProperties.getWithDefault(KEY_SITEMAP_ROUTE_DETAILS_IMPL, DEFAULT_SITEMAP_ROUTE_DETAILS_CLASSNAME);
		try {
			@SuppressWarnings("unchecked")
			Class<? extends SitemapRouteDetails> srdClass = (Class<? extends SitemapRouteDetails>) Class.forName(sdpClassName);
			SitemapRouteDetails srd = srdClass.newInstance();
			LOG.info("  - Using {} as {} implementation.", srdClass, SitemapRouteDetails.class.getSimpleName());
			return srd;
		} catch (ClassNotFoundException e) {
			LOG.error("Could not load " + SitemapRouteDetails.class.getSimpleName() + " implementation " + sdpClassName + ". Please check your '"
					+ KEY_SITEMAP_ROUTE_DETAILS_IMPL + "' setting in application.conf.", e);
			return null;
		} catch (InstantiationException e) {
			LOG.error("Could not instantiate " + SitemapRouteDetails.class.getSimpleName() + " implementation " + sdpClassName + ".", e);
			return null;
		} catch (IllegalAccessException e) {
			LOG.error("Could not instantiate " + SitemapRouteDetails.class.getSimpleName() + " implementation " + sdpClassName + ".", e);
			return null;
        }
    }

}
