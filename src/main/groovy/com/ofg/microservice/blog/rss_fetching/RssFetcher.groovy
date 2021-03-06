package com.ofg.microservice.blog.rss_fetching

import akka.actor.UntypedActor
import com.google.common.base.Stopwatch
import com.ofg.infrastructure.web.filter.correlationid.CorrelationIdHolder
import com.ofg.microservice.blog.rss_fetching.data_extraction.RssData
import com.rometools.fetcher.FetcherException
import com.rometools.fetcher.impl.HttpURLFeedFetcher
import com.rometools.rome.feed.synd.SyndFeed
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.slf4j.MDC

import java.util.concurrent.TimeUnit

@TypeChecked
@Slf4j
public class RssFetcher extends UntypedActor {

    @Override
    public void onReceive(Object message) {
        if (message instanceof RssFetchRequest) {
            processRequest(message as RssFetchRequest)
        } else {
            unhandled(message);
        }
    }

    void processRequest(RssFetchRequest fetchRequest) throws Exception {
        MDC.put(CorrelationIdHolder.CORRELATION_ID_HEADER, fetchRequest.correlationId)

        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            log.info("Trying to fetch rss from " + fetchRequest.rssUrl)
            HttpURLFeedFetcher fetcher = new HttpURLFeedFetcher()
            SyndFeed feed = fetcher.retrieveFeed(new URL(fetchRequest.rssUrl))
            long executionTime = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)
            log.info("Fetched " + feed.entries.size() + " after " + executionTime + " ms")
            this.context.system().eventStream()
                    .publish(new RssData(fetchRequest.rssUrl, feed, fetchRequest.pairId, fetchRequest.correlationId))
        } catch (MalformedURLException | UnknownHostException | FetcherException e) {
            log.error("Non existing rss url " + fetchRequest.rssUrl)
        }
    }

}
