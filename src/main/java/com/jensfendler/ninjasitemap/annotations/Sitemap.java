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
package com.jensfendler.ninjasitemap.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jensfendler.ninjasitemap.SitemapMultiPageProvider;
import com.jensfendler.ninjasitemap.SitemapRouteDetails;

/**
 * @author Jens Fendler
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sitemap {

    public int NEVER = 0;

    public int HOURLY = 1;

    public int DAILY = 2;

    public int WEEKLY = 3;

    public int MONTHLY = 4;

    public int YEARLY = 5;

    public int ALWAYS = 6;

    public String NO_MULTIPAGE_PROVIDER = "";

    public String NO_PATH = "";

    public double DEFAULT_PRIORITY = 0.5;

    public int DEFAULT_CHANGE_FREQUENCY = DAILY;

    /**
     * This value (used on the {@link #priority()} parameter) indicates that the
     * {@link SitemapRouteDetails} should be used to determine the priority
     * programmatically.
     * 
     * (Only applicable to non-dynamic routes).
     */
    public double PRIORITY_DYNAMIC = -1000;

    /**
     * This value (used on the {@link #changeFrequency()} parameter) indicates
     * that the {@link SitemapRouteDetails} should be used to determine the
     * change frequency for the route programmatically.
     * 
     * (Only applicable to non-dynamic routes).
     */
    public int CHANGE_FREQUENCY_DYNAMIC = -1000;

    /**
     * In case of routes which might result in more than one sitemap pages per
     * route, this parapeter can contain the name of a class implemening the
     * SitemapMultiPageProvider interface, to generate a set of pages from one
     * route.
     * 
     * @return the full class name of the {@link SitemapMultiPageProvider}
     *         implementation to use for this controller method
     */
    String multiPageProvider() default NO_MULTIPAGE_PROVIDER;

    /**
     * If set to true, instances of the {@link SitemapMultiPageProvider}
     * specified in the {@link #multiPageProvider()} property will be created
     * via the Guice injector (if possible). This will allow the created objects
     * to access other DAOs or Ninja objects via Guice dependency injection.
     * 
     * For this to work, the class specified in the {@link #multiPageProvider()}
     * property must be bound in your <code>ninja.Module</code> class -
     * preferably <em>before</em> the Ninja-Sitemap module is installed.
     * 
     * @return true, if the Guice injector should be used to create instances of
     *         the {@link SitemapMultiPageProvider} class; or false (the
     *         default) for plain Java class instantiation.
     */
    boolean useInjector() default false;

    /**
     * Explicitly provide a path for the sitemap entry of this controller
     * method. Note: setting this parameter only makes sense for non-dynamic
     * routes.
     * 
     * By default, this value will be automatically determined from the route's
     * URI.
     * 
     * @return the path (page name) to use in the sitemap.
     */
    String path() default NO_PATH;

    /**
     * The priority of the page (should be in the range [0..1.0]
     * 
     * @return the priority of the page
     */
    double priority() default DEFAULT_PRIORITY;

    /**
     * The change frequency of this page in the sitemap. Should be one of the
     * frequency constants of the {@link Sitemap} annotation.
     * 
     * @return the change frequency (in the range 0.0 to 1.0)
     */
    int changeFrequency() default DEFAULT_CHANGE_FREQUENCY;

}
