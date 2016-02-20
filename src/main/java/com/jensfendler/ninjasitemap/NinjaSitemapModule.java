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

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * @author Jens Fendler
 *
 */
public class NinjaSitemapModule extends AbstractModule {

	static final Logger LOG = LoggerFactory.getLogger(NinjaSitemapModule.class);

	/**
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		LOG.info("Initialising Ninja Sitemap.");
		bind(SitemapRouteDetails.class).toProvider(SitemapRouteDetailsProvider.class).in(Singleton.class);
	}

}
