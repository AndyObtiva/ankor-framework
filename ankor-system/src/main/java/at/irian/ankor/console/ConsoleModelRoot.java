/*
 * Copyright (C) 2013-2014  Irian Solutions  (http://www.irian.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.irian.ankor.console;

import at.irian.ankor.monitor.stats.AnkorSystemStats;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public class ConsoleModelRoot {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConsoleModelRoot.class);

    private long totalInboundMessages = 0;
    private long totalOutboundMessages = 0;

    public ConsoleModelRoot(final Ref myRef, final AnkorSystemStats stats) {
        Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        }).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    AnkorPatterns.runLater(myRef, new Runnable() {
                        @Override
                        public void run() {
                            totalInboundMessages = stats.switchboard().getTotalInboundMessages();
                            totalOutboundMessages = stats.switchboard().getTotalOutboundMessages();
                            myRef.signalValueChange();
                        }
                    });
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public long getTotalInboundMessages() {
        return totalInboundMessages;
    }

    public void setTotalInboundMessages(long totalInboundMessages) {
        this.totalInboundMessages = totalInboundMessages;
    }

    public long getTotalOutboundMessages() {
        return totalOutboundMessages;
    }

    public void setTotalOutboundMessages(long totalOutboundMessages) {
        this.totalOutboundMessages = totalOutboundMessages;
    }
}
