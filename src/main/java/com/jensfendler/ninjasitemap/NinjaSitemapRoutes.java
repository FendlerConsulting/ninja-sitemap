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
import com.jensfendler.ninjasitemap.controller.NinjaSitemapController;

import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;

/**
 * @author Jens Fendler
 *
 */
public class NinjaSitemapRoutes implements ApplicationRoutes {

    public static final String KEY_SITEMAP_ROUTE = "ninja.sitemap.route";

    public static final String DEFAULT_SITEMAP_ROUTE = "/sitemap.xml";

    protected static final Logger LOG = LoggerFactory.getLogger(NinjaSitemapRoutes.class);

    @Inject
    protected NinjaProperties ninjaProperties;

    /**
     * @see ninja.application.ApplicationRoutes#init(ninja.Router)
     */
    public void init(Router router) {
        String sitemapRoute = ninjaProperties.getWithDefault(KEY_SITEMAP_ROUTE, DEFAULT_SITEMAP_ROUTE);
        LOG.info("Installing Ninja Sitemap routes: {}", sitemapRoute);
        router.GET().route(sitemapRoute).with(NinjaSitemapController.class, "getSitemapXml");
    }

}
