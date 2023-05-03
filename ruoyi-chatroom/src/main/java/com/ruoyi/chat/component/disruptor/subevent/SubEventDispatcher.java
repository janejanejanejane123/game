package com.ruoyi.chat.component.disruptor.subevent;


import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.ruoyi.chat.component.disruptor.DisruptorUtil;
import com.ruoyi.chat.component.disruptor.subevent.SubEvent;
import com.ruoyi.chat.component.disruptor.subevent.SubEventFactory;
import com.ruoyi.chat.component.disruptor.subevent.SubEventHandler;
import com.ruoyi.chat.component.disruptor.subevent.SubEventTranslator;
import com.ruoyi.chat.config.properties.DispatchProperties;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class SubEventDispatcher {
	public static final SubEventFactory DISPATCH_EVENT_FACTORY = new SubEventFactory();
	private volatile Disruptor<SubEvent> disruptor;
//	private ThreadPoolExecutor executor;
	private DispatchProperties dispatcherProperties;
	private static final int SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS = 50;
	private static final int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 200;
	private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
	private final SubEventHandler subEventHandler;
//	@Resource
//	private MessageService messageService;
	public SubEventDispatcher(DispatchProperties dispatcherProperties, SubEventHandler subEventHandler) {
        this.dispatcherProperties = dispatcherProperties;
        this.subEventHandler = subEventHandler;
	}


	public synchronized void start() {
		if (disruptor != null) {
			log.trace("[{}] MessageDispatcher not starting new disruptor for this context, using existing object.",dispatcherProperties.getThreadName());
			return;
		}
		log.trace("[{}] MessageDispatcher creating new disruptor for this context.", dispatcherProperties.getThreadName());
		final WaitStrategy waitStrategy = DisruptorUtil.createWaitStrategy(dispatcherProperties.getStrategy(),dispatcherProperties.getTimeoutMillis(),dispatcherProperties.getSpinTimeout(),dispatcherProperties.getYieldTimeout(),dispatcherProperties.getFallbackStrategy());

		final ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(final Runnable r) {
				final SecurityManager securityManager = System.getSecurityManager();
				ThreadGroup group = securityManager != null ? securityManager.getThreadGroup()
						: Thread.currentThread().getThreadGroup();
				final Thread thread = new Thread(group, r, "MessageDispatcher[" + dispatcherProperties.getThreadName() + "]:" + THREAD_NUMBER.getAndIncrement(), Thread.NORM_PRIORITY);
				thread.setDaemon(true);
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			}
		};
		/*executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		executor.setThreadFactory(threadFactory);
		disruptor = new Disruptor<DispatchEventWrapper>(new DispatchEventFactory(), dispatcherProperties.getRingBufferSize(), executor, ProducerType.MULTI,
				waitStrategy);*/
		disruptor = new Disruptor<SubEvent>(DISPATCH_EVENT_FACTORY, dispatcherProperties.getRingBufferSize(), threadFactory, ProducerType.MULTI,
				waitStrategy);

		final ExceptionHandler<SubEvent> errorHandler = DisruptorUtil.getAsyncConfigExceptionHandler(dispatcherProperties.getExceptionHandlerClass());
		disruptor.setDefaultExceptionHandler(errorHandler);

		final SubEventHandler[] handlers = {subEventHandler};
		disruptor.handleEventsWithWorkerPool(handlers);

		log.debug("[{}] Starting MessageDispatcher disruptor for this context with ringbufferSize={}, waitStrategy={}, "
				+ "exceptionHandler={}...", dispatcherProperties.getThreadName(), disruptor.getRingBuffer().getBufferSize(), waitStrategy
				.getClass().getSimpleName(), errorHandler);
		disruptor.start();

		log.trace("[{}] MessageDispatcher use a  translator", dispatcherProperties.getThreadName());
	}

	/**
	 * Decreases the reference count. If the reference count reached zero, the Disruptor and its associated thread are
	 * shut down and their references set to {@code null}.
	 */
	public boolean stop() {
		final Disruptor<SubEvent> temp = getDisruptor();
		if (temp == null) {
			log.trace("[{}] MessageDispatcher: disruptor for this context already shut down.", dispatcherProperties.getThreadName());
			return true; // disruptor was already shut down by another thread
		}
		log.debug("[{}] MessageDispatcher: shutting down disruptor for this context.", dispatcherProperties.getThreadName());

		// We must guarantee that publishing to the RingBuffer has stopped before we call disruptor.shutdown().
		disruptor = null; // client code fails with NPE if request after stop. This is by design.


		// Calling Disruptor.shutdown() will wait until all enqueued events are fully processed,
		// but this waiting happens in a busy-spin. To avoid (postpone) wasting CPU,
		// we sleep in short chunks, up to 10 seconds, waiting for the ringbuffer to drain.
		for (int i = 0; hasBackRequest(temp) && i < MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN; i++) {
			try {
				Thread.sleep(SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS); // give up the CPU for a while
			} catch (final InterruptedException e) { // ignored
			}
		}
		try {
			// busy-spins until all events currently in the disruptor have been processed, or timeout
			temp.shutdown(dispatcherProperties.getShutdownTimeout(), TimeUnit.MILLISECONDS);
//			executor.shutdown();
		} catch (final TimeoutException e) {
			log.error("[{}] MessageDispatcher: shutdown timed out after {} {}", dispatcherProperties.getThreadName(), dispatcherProperties.getShutdownTimeout(), TimeUnit.MILLISECONDS);
			temp.halt(); // give up on remaining request events, if any
		}

		log.trace("[{}] MessageDispatcher: disruptor has been shut down.", dispatcherProperties.getThreadName());

		return true;
	}

	public Disruptor<SubEvent> getDisruptor() {
		return disruptor;
	}

	private static boolean hasBackRequest(final Disruptor<?> theDisruptor) {
		final RingBuffer<?> ringBuffer = theDisruptor.getRingBuffer();
		return !ringBuffer.hasAvailableCapacity(ringBuffer.getBufferSize());
	}

    private boolean hasBeen(final Disruptor<SubEvent> aDisruptor) {
        if (aDisruptor == null) {
            log.warn("Ignoring request event after server was shut down");
            return true;
        }
        return false;
    }

    public int remainingDisruptorCapacity() {
        final Disruptor<SubEvent> temp = disruptor;
        if (hasBeen(temp)) {
            return -1;
        }
        return (int) temp.getRingBuffer().remainingCapacity();
    }

    private boolean tryPublish(final SubEventTranslator translator) {
        try {
            return disruptor.getRingBuffer().tryPublishEvent(translator);
        } catch (final NullPointerException npe) {
            log.warn("Ignoring request event after server was shut down: {}", translator);
            return false;
        }
    }

    private void publish(final SubEventTranslator translator) {
        if (!tryPublish(translator)) {
            try {
                // Note: we deliberately access the volatile disruptor field afresh here.
                // Avoiding this and using an older reference could result in adding a request event to the disruptor after it
                // was shut down, which could cause the publishEvent method to hang and never return.
                disruptor.publishEvent(translator);
            } catch (final NullPointerException npe) {
                log.warn("Ignoring request event after server was shut down: {}", translator);
            }
        }
    }


	public boolean onData(SubEventRunnable runnable) {
		if(remainingDisruptorCapacity() > 0) {
			// Implementation note: this method is tuned for performance. MODIFY WITH CARE!
			final SubEventTranslator translator = new SubEventTranslator(runnable);
			publish(translator);
			return false;
		}else {
//			messageService.sendMsgResponse(requestType,ctx,msg,new ApiResponse<>("504", ApiResponConstant.FAILD_CODE,"busy server"));
			return true;
		}
    }
}
