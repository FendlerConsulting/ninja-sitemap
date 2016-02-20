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

import java.util.List;

import com.jensfendler.ninjasitemap.annotations.Sitemap;

import ninja.Route;
import ninja.params.PathParam;

/**
 * Classes implementing this class can be referenced in the
 * {@link Sitemap#multiPageProvider()} parameter. This is useful for dynamic
 * routes (typically routes with {@link PathParam} arguments), which should
 * result in multiple page entries in the sitemap, and must be generated
 * dynamically at runtime.
 * 
 * @author Jens Fendler
 *
 */
public interface SitemapMultiPageProvider {

	/**
	 * Generate a list of {@link SitemapEntry}s (representing individual pages
	 * in the sitemap) for the given {@link Route} and {@link Sitemap}
	 * annotation.
	 * 
	 * @param route
	 *            the {@link Route}
	 * @param sitemapAnnotation
	 *            the {@link Sitemap}
	 * @return a {@link List} of {@link SitemapEntry}s to be added to the
	 *         sitemap
	 */
	public List<SitemapEntry> getSitemapEntries(Route route, Sitemap sitemapAnnotation);

}
