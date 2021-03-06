package com.ofg.microservice.blog.rss_fetching.data_extraction

import com.ofg.microservice.blog.rss_fetching.BaseActorMessage
import com.rometools.rome.feed.synd.SyndFeed
import groovy.transform.Canonical
import groovy.transform.TypeChecked

@TypeChecked
@Canonical
class RssData extends BaseActorMessage {
    String rssUrl
    SyndFeed feed

    RssData(String rssUrl, SyndFeed feed, String pairId, String correlationId) {
        super(pairId, correlationId)
        this.rssUrl = rssUrl
        this.feed = feed
    }
}
