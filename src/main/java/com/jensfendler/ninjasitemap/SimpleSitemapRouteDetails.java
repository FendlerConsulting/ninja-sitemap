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
 * Classes like this one (implementing the {@link SitemapRouteDetails}
 * interface) can be provided by the developer to determine some parameters of
 * the sitemap generation programmatically at runtime.
 * 
 * To do this, developers should bind their own implementation to this
 * interface, after installing the NinjaSitemapModule().
 * 
 * @author Jens Fendler
 *
 */
public class SimpleSitemapRouteDetails implements SitemapRouteDetails {

    public SimpleSitemapRouteDetails() {
    }

    /**
     * @see com.jensfendler.ninjasitemap.SitemapRouteDetails#getLastModifiedDateForRoute(ninja.Route,
     *      com.jensfendler.ninjasitemap.annotations.Sitemap)
     */
    public Date getLastModifiedDateForRoute(Route route, Sitemap sitemapAnnotation) {
        // assume the page has just been updated
        return new Date();
    }

    /**
     * @see com.jensfendler.ninjasitemap.SitemapRouteDetails#getPriorityForRoute(ninja.Route,
     *      com.jensfendler.ninjasitemap.annotations.Sitemap)
     */
    public double getPriorityForRoute(Route route, Sitemap sitemap) {
        // always use the default priority
        return Sitemap.DEFAULT_PRIORITY;
    }

    /**
     * @see com.jensfendler.ninjasitemap.SitemapRouteDetails#getChangeFrequencyForRoute(ninja.Route,
     *      com.jensfendler.ninjasitemap.annotations.Sitemap)
     */
    public int getChangeFrequencyForRoute(Route route, Sitemap sitemap) {
        // always use the default frequency
        return Sitemap.DEFAULT_CHANGE_FREQUENCY;
    }

}
