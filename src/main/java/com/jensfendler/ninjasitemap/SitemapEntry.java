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

import ninja.Router;

/**
 * Simple bean to wrap all properties making up a single page entry in the
 * sitemap.
 * 
 * @author Jens Fendler
 *
 */
public class SitemapEntry {

	/**
	 * The date of last modification
	 */
	private Date lastModified;

	/**
	 * The path to the page
	 */
	private String pagePath;

	/**
	 * The page priority (0..1)
	 */
	private double priority;

	/**
	 * The change frequency (use one of the frequency constants from
	 * {@link Sitemap}.
	 */
	private int changeFrequency;

	/**
	 * Currently not used.
	 */
	private String shortName;

	/**
	 * currently not used.
	 */
	private String shortDescription;

	/**
	 * @param pagePath
	 *            the path of the URL (as in the {@link Router} for non-dynamic
	 *            routes)
	 */
	public SitemapEntry(String pagePath) {
		this(pagePath, new Date(), Sitemap.DAILY, 0.5);
	}

	/**
	 * @param pagePath
	 *            the path of the URL (as in the {@link Router} for non-dynamic
	 *            routes)
	 * @param lastModified
	 *            the last modified Date
	 * @param changeFrequency
	 *            the expected change frequency for the page (use one of the
	 *            constants from {@link Sitemap#ALWAYS},{@link Sitemap#HOURLY},
	 *            {@link Sitemap#DAILY},{@link Sitemap#WEEKLY},
	 *            {@link Sitemap#MONTHLY},{@link Sitemap#YEARLY}, or
	 *            {@link Sitemap#NEVER},
	 * @param priority
	 *            the priority of the page (between 0 and 1)
	 */
	public SitemapEntry(String pagePath, Date lastModified, int changeFrequency, double priority) {
		this.pagePath = pagePath;
		this.lastModified = lastModified;
		this.priority = priority;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getPagePath() {
		return pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public int getChangeFrequency() {
		return changeFrequency;
	}

	public void setChangeFrequency(int changeFrequency) {
		this.changeFrequency = changeFrequency;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

}
