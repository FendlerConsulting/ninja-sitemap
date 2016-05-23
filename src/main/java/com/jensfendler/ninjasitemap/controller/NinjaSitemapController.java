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
package com.jensfendler.ninjasitemap.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.jensfendler.ninjasitemap.SitemapRouteDetails;
import com.jensfendler.ninjasitemap.NinjaSitemapRoutes;
import com.jensfendler.ninjasitemap.SitemapEntry;
import com.jensfendler.ninjasitemap.SitemapMultiPageProvider;
import com.jensfendler.ninjasitemap.annotations.Sitemap;

import cz.jiripinkas.jsitemapgenerator.ChangeFreq;
import cz.jiripinkas.jsitemapgenerator.WebPage;
import cz.jiripinkas.jsitemapgenerator.exception.GWTException;
import cz.jiripinkas.jsitemapgenerator.generator.SitemapGenerator;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.Router;
import ninja.cache.NinjaCache;
import ninja.utils.NinjaProperties;

/**
 * @author Jens Fendler
 *
 */
@Singleton
public class NinjaSitemapController {

    protected static final Logger LOG = LoggerFactory.getLogger(NinjaSitemapController.class);

    /**
     * Routes are matched against this expression to check if they contain a
     * dynamic part.
     */
    protected static final String DYNAMIC_ROUTE_PATTERN = ".*\\{.*\\}.*";

    /**
     * The key name in application.conf to contain the protocol, server name,
     * port, and possibly a path component to prepend to all sitemap entries.
     */
    private static final String KEY_SITEMAP_PREFIX = "ninja.sitemap.prefix";

    /**
     * An application.conf property to control the expiry time of the sitemap in
     * Ninja's cache. This value should preferably be less than half the
     * shortest 'changeFrequency' of your sitemap entries.
     */
    private static final String KEY_NINJA_SITEMAP_EXPIRED = "ninja.sitemap.expires";

    /**
     * The default expiry time of the sitemap string in Ninja's cache. Defaults
     * to 6 hours ("6h"). The string must be in a format compatible with the
     * {@link NinjaCache} methods.
     */
    private static final String DEFAULT_SITEMAP_EXPIRY_TIME = "12h";

    /**
     * If this application.conf property is 'true', the Google search engine is
     * informed of updates to the sitemap. Default: false.
     */
    private static final String KEY_PING_GOOGLE = "ninja.sitemap.ping.google";

    /**
     * If this application.conf property is 'true', the Bing search engine is
     * informed of updates to the sitemap. Default: false.
     */
    private static final String KEY_PING_BING = "ninja.sitemap.ping.bing";

    /**
     * The cache key to use for the sitemap.
     */
    private static final String SITEMAP_CACHE_KEY = NinjaSitemapController.class.getSimpleName() + "-sitemap";

    /**
     * If this key is set to 'false', no warnings will be logged when using a
     * {@link SitemapMultiPageProvider} for a non-dynamic route. If this
     * property is 'true' (the default), a warning will be logged.
     */
    private static final String KEY_SHOW_MPP_WARNINGS = "ninja.sitemap.multiPageWarnings";

    @Inject
    protected NinjaCache cache;

    @Inject
    protected NinjaProperties ninjaProperties;

    @Inject
    protected Router router;

    @Inject
    protected SitemapRouteDetails sitemapDetailsProvider;

    @Inject
    protected Injector injector;

    /**
     * Returns the sitemap.xml data following a GET request to /sitemap.xml
     * 
     * @param context
     *            the request context (currently not used)
     * @return the Result containing the sitemap.xml data
     */
    public Result getSitemapXml(Context context) {

        // attempt a cache lookup first.
        String sitemapString = (String) cache.get(SITEMAP_CACHE_KEY);

        if (sitemapString == null) {
            // sitemap is not in cache. re-create.
            sitemapString = createSitemap(context);
        }

        return Results.ok().xml().renderRaw(sitemapString.getBytes());
    }

    /**
     * Create the sitemap from scratch.
     * 
     * @param context
     *            the request context (not used)
     * @return the XML data of the sitemap (as one string)
     */
    private String createSitemap(Context context) {
        // get the (optional) prefix to prepend to all URLs provided in the
        // sitemap
        String siteUrlPrefix = ninjaProperties.get(KEY_SITEMAP_PREFIX);
        if (siteUrlPrefix == null) {
            siteUrlPrefix = "http://" + context.getHostname();
            LOG.warn(
                    "No {} configured in application conf. Using default prefix '{}'. You should configure this property in application.conf.",
                    KEY_SITEMAP_PREFIX, siteUrlPrefix);
        }

        siteUrlPrefix = siteUrlPrefix.replaceAll("/$", "");

        final SitemapGenerator generator = new SitemapGenerator(siteUrlPrefix);

        for (Route route : router.getRoutes()) {
            Sitemap sitemap = route.getControllerMethod().getAnnotation(Sitemap.class);
            // check if the route should be processed for the sitemap
            if (includeInSitemap(sitemap, route)) {
                LOG.debug("Including route {} in sitemap.xml", route.getUri());
                // create page(s) from the route and add to the sitemap
                generator.addPages(createSitemapPages(sitemap, route));
            } else {
                LOG.debug("Not including route {} in sitemap.xml", route.getUri());
            }
        }

        // cache the newly created sitemap
        String sitemapString = generator.constructSitemapString();
        String sitemapCacheExpires = ninjaProperties.getWithDefault(KEY_NINJA_SITEMAP_EXPIRED,
                DEFAULT_SITEMAP_EXPIRY_TIME);
        boolean isCached = cache.safeSet(SITEMAP_CACHE_KEY, sitemapString, sitemapCacheExpires);
        if (isCached) {
            LOG.info("Sitemap has been updated and cached. Will be recreated in {}.", sitemapCacheExpires);
        } else {
            // perhaps this is the first time cache
            LOG.warn("Sitemap has been updated and will be delivered, but could not be cached.");
        }

        // check if we should ping google/bing for the updated sitemap
        final boolean shouldPingGoogle = ninjaProperties.getBooleanWithDefault(KEY_PING_GOOGLE, false);
        final boolean shouldPingBing = ninjaProperties.getBooleanWithDefault(KEY_PING_BING, false);
        if (shouldPingGoogle || shouldPingBing) {

            String sitemapRoute = ninjaProperties.getWithDefault(NinjaSitemapRoutes.KEY_SITEMAP_ROUTE,
                    NinjaSitemapRoutes.DEFAULT_SITEMAP_ROUTE);
            if (!sitemapRoute.startsWith("/")) {
                sitemapRoute = "/" + sitemapRoute;
            }
            final String sitemapUrl = siteUrlPrefix + sitemapRoute;

            // we only want to issue search engine pings in production mode
            if (ninjaProperties.isProd()) {

                // pinging search engines will take some time. run pinging in
                // separate threads so that the current request can immediately
                // be served.

                // possibly ping Google search engine in separate thread
                if (shouldPingGoogle) {
                    Executors.defaultThreadFactory().newThread(new Runnable() {
                        public void run() {
                            try {
                                generator.pingGoogle(sitemapUrl);
                                LOG.info("Google search engine has been notified of updated sitemap at '{}'.",
                                        sitemapUrl);
                            } catch (GWTException e) {
                                LOG.warn("Failed to ping Google with updated sitemap.", e);
                            }
                        }
                    }).start();
                }

                // possibly ping Bing search engine in separate thread
                if (shouldPingBing) {
                    Executors.defaultThreadFactory().newThread(new Runnable() {
                        public void run() {
                            try {
                                generator.pingBing(sitemapUrl);
                                LOG.info("Bing search engine has been notified of updated sitemap at '{}'.",
                                        sitemapUrl);
                            } catch (GWTException e) {
                                LOG.warn("Failed to ping Google with updated sitemap.", e);
                            }
                        }
                    }).start();
                }
            } else {
                LOG.info("Not pinging search engines in non-production mode.");
            }

        }

        // return the sitemap.xml data as a string
        return sitemapString;
    }

    /**
     * Creates a set of zero or more {@link WebPage} objects to include in the
     * sitemap, based on the given annotation and route
     * 
     * @param sitemap
     *            the {@link Sitemap} annotation of the route's controller
     *            method
     * @param route
     *            the {@link Route}
     * @return a {@link Collection} of WebPages for the sitemap
     */
    @SuppressWarnings("unchecked")
    private Collection<WebPage> createSitemapPages(Sitemap sitemap, Route route) {
        Collection<WebPage> pages = new ArrayList<WebPage>();

        // allow for multiple pages if there is at least one @PathParam
        // annotation in the controller method's arguments.
        boolean dynamicRoute = route.getUri().matches(DYNAMIC_ROUTE_PATTERN);

        // check if we have a SitemapMultiPageProvider registered for this route
        String smppClassName = sitemap.multiPageProvider();
        if (!Sitemap.NO_MULTIPAGE_PROVIDER.equals(smppClassName)) {
            try {
                Class<? extends SitemapMultiPageProvider> smppClass = (Class<? extends SitemapMultiPageProvider>) Class
                        .forName(smppClassName);

                SitemapMultiPageProvider smpp = sitemap.useInjector() ? injector.getInstance(smppClass)
                        : smppClass.newInstance();

                if (!dynamicRoute && ninjaProperties.getBooleanWithDefault(KEY_SHOW_MPP_WARNINGS, true)) {
                    // using a MultiPageProvider for a non-dynamic route is
                    // usually strange. warn about it.
                    LOG.warn(
                            "Using {} to create sitemap entries for non-dynamic route {} to {}.{}. Is this really intended?",
                            smppClassName, route.getUri(), route.getControllerClass().getName(),
                            route.getControllerMethod().getName());
                }

                try {
                    List<SitemapEntry> entries = smpp.getSitemapEntries(route, sitemap);
                    if ((entries == null) || (entries.size() == 0)) {
                        LOG.warn("{} did not return any sitemap entries for route {} to {}.{}.", smppClassName,
                                route.getUri(), route.getControllerClass().getName(),
                                route.getControllerMethod().getName());
                    } else {
                        for (SitemapEntry se : entries) {
                            WebPage wp = new WebPage();
                            wp.setName(se.getPagePath().replaceFirst("^/", ""));
                            wp.setShortName(se.getShortName());
                            wp.setShortDescription(se.getShortDescription());
                            wp.setLastMod(se.getLastModified());
                            wp.setPriority(se.getPriority());
                            wp.setChangeFreq(changeFrequencyFromInteger(se.getChangeFrequency()));

                            // add to the list of pages for this route
                            pages.add(wp);
                        }
                    }
                } catch (NullPointerException npe) {
                    if (sitemap.useInjector()) {
                        // using injector - show an error message pointing to a
                        // likely cause of the problem.
                        LOG.error(
                                "NullPointerException in {} when using 'useInjector'. Perhaps you forgot to bind() your class in ninja.Module?",
                                smppClass.getName());
                    }
                    throw npe;
                }

            } catch (ClassNotFoundException e) {
                LOG.error("Could not find class " + smppClassName + " as specified for "
                        + route.getControllerClass().getName() + "." + route.getControllerMethod().getName()
                        + ". Not including in sitemap.", e);
            } catch (InstantiationException e) {
                LOG.error("Could not instantiate class " + smppClassName + " as specified for "
                        + route.getControllerClass().getName() + "." + route.getControllerMethod().getName()
                        + ". Not including in sitemap.", e);
            } catch (IllegalAccessException e) {
                LOG.error("Illegal access to constructor of class " + smppClassName + " as specified for "
                        + route.getControllerClass().getName() + "." + route.getControllerMethod().getName()
                        + ". Not including in sitemap.", e);
            }

        } else if (!dynamicRoute) {
            // no SitemapMultiPageProvider given, and not a dynamic route.
            // standard case.
            WebPage wp = new WebPage();
            if (!Sitemap.NO_PATH.equals(sitemap.path())) {
                // a path (name) was explicitly given
                wp.setName(sitemap.path().replaceFirst("^/", ""));
            } else {
                // set the path from the router
                wp.setName(route.getUri().replaceFirst("^/", ""));
            }
            wp.setLastMod(sitemapDetailsProvider.getLastModifiedDateForRoute(route, sitemap));

            if (sitemap.priority() == Sitemap.PRIORITY_DYNAMIC) {
                wp.setPriority(sitemapDetailsProvider.getPriorityForRoute(route, sitemap));
            } else {
                wp.setPriority(sitemap.priority());
            }

            if (sitemap.changeFrequency() == Sitemap.CHANGE_FREQUENCY_DYNAMIC) {
                wp.setChangeFreq(
                        changeFrequencyFromInteger(sitemapDetailsProvider.getChangeFrequencyForRoute(route, sitemap)));
            } else {
                wp.setChangeFreq(changeFrequencyFromInteger(sitemap.changeFrequency()));
            }

            // add to the list of pages for this route
            pages.add(wp);

        } else {
            // no SitemapMultiPageProvider given, but a dynamic route. warn
            // about this.
            LOG.warn(
                    "Dynamic Route {} in controller {}.{} does not provide a {} implementation. Not including in sitemap.",
                    route.getUri(), route.getControllerClass().getName(), route.getControllerMethod().getName(),
                    SitemapMultiPageProvider.class.getSimpleName());
        }

        LOG.debug("Returning {} pages in sitemap.xml for route {}.", pages.size(), route.getUri());
        return pages;
    }

    /**
     * @param changeFrequency
     * @return
     */
    private ChangeFreq changeFrequencyFromInteger(int changeFrequencyConstant) {
        switch (changeFrequencyConstant) {
        case Sitemap.NEVER:
            return ChangeFreq.NEVER;
        case Sitemap.HOURLY:
            return ChangeFreq.HOURLY;
        case Sitemap.DAILY:
            return ChangeFreq.DAILY;
        case Sitemap.WEEKLY:
            return ChangeFreq.WEEKLY;
        case Sitemap.MONTHLY:
            return ChangeFreq.MONTHLY;
        case Sitemap.YEARLY:
            return ChangeFreq.YEARLY;
        case Sitemap.ALWAYS:
            return ChangeFreq.ALWAYS;
        default:
            LOG.warn("Invalid change frequency value {}. Using 'DAILY'.", changeFrequencyConstant);
            return ChangeFreq.DAILY;
        }
    }

    /**
     * Tests if the given {@link Route} should result in any pages for the
     * sitemap.
     * 
     * @param sitemap
     *            the {@link Sitemap} annotation of the {@link Route}'s
     *            controller method.
     * @param route
     *            the {@link Route} itself
     * @return true, if the route should result in sitemap pages, otherwise
     *         false
     */
    private boolean includeInSitemap(Sitemap sitemap, Route route) {
        if (sitemap == null) {
            LOG.debug("No @Sitemap annotation for controller {}.{}. Not including in sitemap.xml.",
                    route.getControllerClass().getName(), route.getControllerMethod().getName());
            return false;

        } else {

            // TODO implement additional filtering of routes for sitemap
            // generation based on annotation

            return true;
        }
    }

}
