# Ninja Sitemap
A Sitemap generator module for the [Ninja Framework](https://github.com/ninjaframework/ninja), based on the Jirka Pinkas' [Java Sitemap Generator](https://github.com/jirkapinkas/jsitemapgenerator).


Features:
---------
The Ninja Sitemap module can automatically create single sitemap entries for non-dynamic routes (i.e. routes for unique URIs, without any path parameters).
For "dynamic" routes (i.e. routes for URIs with `{...}`-style variable parts, you can register a custom `SitemapMultiPageProvider` instance for every such route, which can create any number of entries for your `sitemap.xml` (e.g. based on your database contents). Of course, you can also register a `SitemapMultiPageProvider` for non-dynamic routes, if you want them to result in a different sitemap entry, or result in more than one entry for your sitemap.

For every entry in your sitemap, you can specify the priority, the changeFrequency, and the actual path, if you want to. All of these are optional. However, for "dynamic" routes you will have to provide a `SitemapMultiPageProvider`, as the module has no chance to figure out what kind of content in your URIs should result in a sitemap entry.
 

Basic Usage:
------------

- ninja-sitemap has been published on Maven Central. Add the following dependency to your Ninja application's `pom.xml`:

```xml

    <dependency>
        <groupId>com.jensfendler</groupId>
        <artifactId>ninja-sitemap</artifactId>
        <version>0.0.1</version>
    </dependency>

```

- Install an instance of the `NinjaSitemapModule` in your Ninja application's `conf.Modules class`:

```java

	install(new NinjaSitemapModule());

```

- Inject `NinjaSitemapRoutes` into your `conf.Router` class, and make a call to its `init(Router)` method:

```java
	@Inject
	NinjaSitemapRoutes sitemapRoutes;

    public void init(Router router) {

        sitemapRoutes.init(router);
        
        // ... your application's routes go here
    }

```

- Configure the URL prefix for all entries in your sitemap through your `application.conf`:

```

ninja.sitemap.prefix=https://www.myserver.com:8443/basePath/

```

- Annotate all controller methods with `@Sitemap` annotations, if you want them to be included in your resulting sitemap.xml.

Example:

```java

@Singleton
public class MyContoller {

    // create a single sitemap entry with maximum priority and an hourly change frequency
     
    @Sitemap(priority=1.0, changeFrequency=Sitemap.HOURLY)
    public Result homepage(Context context) {
        // ...
    }


	// create a single sitemap entry with default priority and change frequency
	
    @Sitemap
    public Result aboutUs(Context context) {
        // ...
    }
    
    
    // create multiple entries in the sitemap for a controller method with `@PathParam` arguments
    @Sitemap(multiPageProvider="modules.MyMultiPageProvider")
    public void productDetails(Context context, @PathParam("productId") long productId) {
        // ...
    }
}

```

## License

Copyright (C) 2016 Fendler Consulting cc.
This work is licensed under the Apache License, Version 2.0. See LICENSE for details.
