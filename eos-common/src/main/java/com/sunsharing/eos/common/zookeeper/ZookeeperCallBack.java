package com.sunsharing.eos.common.zookeeper;

import org.apache.zookeeper.WatchedEvent;

/**
 * Created by criss on 14-1-27.
 */
public interface ZookeeperCallBack {

    public void afterConnect(WatchedEvent event);

    public void watchNodeChange(WatchedEvent event);

}
