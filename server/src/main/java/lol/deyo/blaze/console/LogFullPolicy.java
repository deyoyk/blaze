package lol.deyo.blaze.console;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.async.AsyncQueueFullPolicy;
import org.apache.logging.log4j.core.async.EventRoute;

public final class LogFullPolicy implements AsyncQueueFullPolicy {
   public EventRoute getRoute(long backgroundThreadId, Level level) {
      return EventRoute.ENQUEUE;
   }
}
