package app.mousike.android.utils.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class MousikeDispatchersTest {

    @Test
    fun `UI aliases Dispatchers Main`() {
        assertEquals(Dispatchers.Main, MousikeDispatchers.UI)
    }

    @Test
    fun `DATA aliases Dispatchers IO`() {
        assertEquals(Dispatchers.IO, MousikeDispatchers.DATA)
    }

    @Test
    fun `PLAYBACK and VISUALIZER are distinct single-thread dispatchers`() {
        assertNotSame(MousikeDispatchers.PLAYBACK, MousikeDispatchers.VISUALIZER)
    }

    @Test
    fun `PLAYBACK runs work on a thread named mousike-playback`() = runBlocking {
        // kotlinx.coroutines debug mode appends " @coroutine#N" to the thread name for tracing -
        // startsWith avoids coupling the assertion to that debug-only suffix.
        val threadName = withContext(MousikeDispatchers.PLAYBACK) { Thread.currentThread().name }
        assertTrue(threadName.startsWith("mousike-playback"), "expected mousike-playback but was $threadName")
    }

    @Test
    fun `VISUALIZER runs work on a thread named mousike-visualizer`() = runBlocking {
        val threadName = withContext(MousikeDispatchers.VISUALIZER) { Thread.currentThread().name }
        assertTrue(threadName.startsWith("mousike-visualizer"), "expected mousike-visualizer but was $threadName")
    }

    @Test
    fun `MEDIA runs work on threads prefixed mousike-media`() = runBlocking {
        val threadName = withContext(MousikeDispatchers.MEDIA) { Thread.currentThread().name }
        assertTrue(threadName.startsWith("mousike-media-"), "expected mousike-media-* but was $threadName")
    }

    @Test
    fun `ROOM_QUERY_EXECUTOR runs submitted work on a thread prefixed mousike-room-query`() {
        val nameRef = AtomicReference<String>()
        val latch = CountDownLatch(1)
        MousikeDispatchers.ROOM_QUERY_EXECUTOR.execute {
            nameRef.set(Thread.currentThread().name)
            latch.countDown()
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS), "executor did not run the submitted task in time")
        assertTrue(nameRef.get().startsWith("mousike-room-query-"), "expected mousike-room-query-* but was ${nameRef.get()}")
    }

    @Test
    fun `ROOM_TX_EXECUTOR runs submitted work on a thread prefixed mousike-room-tx`() {
        val nameRef = AtomicReference<String>()
        val latch = CountDownLatch(1)
        MousikeDispatchers.ROOM_TX_EXECUTOR.execute {
            nameRef.set(Thread.currentThread().name)
            latch.countDown()
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS), "executor did not run the submitted task in time")
        assertTrue(nameRef.get().startsWith("mousike-room-tx-"), "expected mousike-room-tx-* but was ${nameRef.get()}")
    }

    @Test
    fun `DATA asExecutor runs submitted work`() {
        val latch = CountDownLatch(1)
        MousikeDispatchers.DATA.asExecutor().execute { latch.countDown() }
        assertTrue(latch.await(5, TimeUnit.SECONDS), "DATA asExecutor() did not run the submitted task in time")
    }

    @Test
    fun `PLAYBACK is single-threaded and preserves submission order`() = runBlocking {
        val order = CopyOnWriteArrayList<Int>()
        val jobs = (1..20).map { i ->
            launch(MousikeDispatchers.PLAYBACK) { order.add(i) }
        }
        jobs.forEach { it.join() }
        assertEquals((1..20).toList(), order, "PLAYBACK must run submitted work in submission order")
    }

    @Test
    fun `Room executors cannot be cast to ExecutorService to shut them down`() {
        assertFalse(MousikeDispatchers.ROOM_QUERY_EXECUTOR is ExecutorService, "ROOM_QUERY_EXECUTOR must not expose shutdown()")
        assertFalse(MousikeDispatchers.ROOM_TX_EXECUTOR is ExecutorService, "ROOM_TX_EXECUTOR must not expose shutdown()")
    }
}
