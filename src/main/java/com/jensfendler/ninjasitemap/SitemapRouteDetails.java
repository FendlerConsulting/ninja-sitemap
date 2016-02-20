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

import java.util.Date;

import com.jensfendler.ninjasitemap.annotations.Sitemap;

import ninja.Route;

/**
 * @author Jens Fendler
 *
 */
public interface SitemapRouteDetails {

	/**
	 * Provide the default "last modified date" value for sitemap pages
	 * resulting from the given route (with its sitemap annotation).
	 * 
	 * @param route
	 *            the {@link Route}
	 * @param sitemapAnnotation
	 *            the {@link Sitemap} annotation.
	 * @return the default {@link Date} to use as value for "lastModified"
	 */
	public Date getLastModifiedDateForRoute(Route route, Sitemap sitemapAnnotation);

	/**
	 * If the {@link Sitemap#priority()} value is set to
	 * {@link Sitemap#PRIORITY_DYNAMIC}, and the route is non-dynamic, then this
	 * method is called to determine the prioriy of the page programmatically.
	 * 
	 * @param route
	 *            the route
	 * @param sitemap
	 *            the {@link Sitemap} annotation
	 * @return the priority value (in the range 0 to 1.0) to use for this
	 *         route's entry in the sitemap
	 */
	public double getPriorityForRoute(Route route, Sitemap sitemap);

	/**
	 * If the {@link Sitemap#changeFrequency()} value is set to
	 * {@link Sitemap#CHANGE_FREQUENCY_DYNAMIC}, and the route is non-dynamic,
	 * then this method is called to determine the prioriy of the page
	 * programmatically.
	 * 
	 * @param route
	 *            the route for which to determine the change frequency
	 * @param sitemap
	 *            the {@link Sitemap} annotation of the route's controller
	 *            method.
	 * @return one of the frequency constants from {@link Sitemap}:
	 *         {@link Sitemap#ALWAYS}, {@link Sitemap#HOURLY},
	 *         {@link Sitemap#DAILY}, {@link Sitemap#WEEKLY},
	 *         {@link Sitemap#MONTHLY}, {@link Sitemap#YEARLY}, or
	 *         {@link Sitemap#NEVER}
	 */
	public int getChangeFrequencyForRoute(Route route, Sitemap sitemap);

}
