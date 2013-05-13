package io.searchbox.client.config;


import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 05/05/2013
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class RoundRobinServerListTest {

    private final String SERVER1 = "server1";
    private final String SERVER2 = "server2";
    private final String SERVER3 = "server3";
    private final String SERVER4 = "server4";
    private final String SERVER5 = "server5";
    private final String SERVER6 = "server6";
    private final String SERVER7 = "server7";

    @Test
    public void testCorrectListReturnedForOneServer() {
        ServerList serverList = new RoundRobinServerList(new HashSet() {{add(SERVER1);}});

        assertEquals(SERVER1,serverList.getServer());
        assertEquals(SERVER1,serverList.getServer());
        assertEquals(SERVER1,serverList.getServer());
    }

    @Test
    public void testCorrectListReturnedForTwoServers() {
      ServerList serverList = new RoundRobinServerList(new LinkedHashSet() {{add(SERVER1);add(SERVER2);}});

      assertEquals(SERVER1,serverList.getServer());
        assertEquals(SERVER2,serverList.getServer());
        assertEquals(SERVER1,serverList.getServer());
        assertEquals(SERVER2,serverList.getServer());
    }

    @Test
    public void testCorrectListReturnedForFiveServers() {
        ServerList serverList = new RoundRobinServerList(new LinkedHashSet() {{add(SERVER1);add(SERVER2);add(SERVER3);add(SERVER4);add(SERVER5);}});

        assertEquals(SERVER1,serverList.getServer());
        assertEquals(SERVER2,serverList.getServer());
        assertEquals(SERVER3,serverList.getServer());
        assertEquals(SERVER4,serverList.getServer());
        assertEquals(SERVER5,serverList.getServer());
        assertEquals(SERVER1,serverList.getServer());
        assertEquals(SERVER2,serverList.getServer());
        assertEquals(SERVER3,serverList.getServer());
        assertEquals(SERVER4,serverList.getServer());
        assertEquals(SERVER5,serverList.getServer());
    }

    /**
     * Ignored on purpose.  Iterates the entire 0 -> INTEGER.MAX_VALUE
     * width.  Therefore the test takes a while (few mins) to run.
     * another concurrent test @see #testConcurrentGetServer
     */
    @Test
    @Ignore
    public void testConcurrentIntegerWrap() {
        final ServerList serverList = new RoundRobinServerList(new LinkedHashSet() {{add(SERVER1);add(SERVER2);add(SERVER3);add(SERVER4);add(SERVER5);}});
        final int noThreads = 4;

        ExecutorService executorService = Executors.newFixedThreadPool(noThreads);

        int createThreadsLoop = noThreads+1;
        final CountDownLatch latch = new CountDownLatch(noThreads);

        while(--createThreadsLoop!=0) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        int loops = (Integer.MAX_VALUE/noThreads)+1;

                        try {
                            while(--loops !=0) {
                                serverList.getServer();
                            }
                        } finally {
                            latch.countDown();
                        }
                    }
                });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        int remainingLoops = (Integer.MAX_VALUE - ((int)((Integer.MAX_VALUE/noThreads))*noThreads))+1;

        while(--remainingLoops!=0) {
            serverList.getServer();

        }

        assertEquals(SERVER3, serverList.getServer());
        assertEquals(SERVER4, serverList.getServer());
        assertEquals(SERVER5, serverList.getServer());
        assertEquals(SERVER1, serverList.getServer());

    }

    /**
     * Test that given a concurrent number of getServer requests that
     * after the threads have finished executing an equal number of
     * getServer requests, the next getServer request returns the expected
     * value
     */
    @Test
    public void testConcurrentGetServer() {
        final int numberOfServers = 5;
        Set<String> servers = new LinkedHashSet() {{add(SERVER1);add(SERVER2);add(SERVER3);add(SERVER4);add(SERVER5);}};
        final ServerList serverList = new RoundRobinServerList(servers);
        final int noThreads = 8;

        ExecutorService executorService = Executors.newFixedThreadPool(noThreads);

        int createThreadsLoop = noThreads+1;
        final CountDownLatch latch = new CountDownLatch(noThreads);

        final int noOfInvocations = 10000000;
        while(--createThreadsLoop!=0) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    int loops = (noOfInvocations/noThreads)+1;

                    try {
                        while(--loops !=0) {
                            serverList.getServer();
                        }
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        int remainingLoops = (noOfInvocations - ((int)((noOfInvocations/noThreads))*noThreads))+1;

        while(--remainingLoops!=0) {
            serverList.getServer();

        }

        int nextItem = (noOfInvocations)%numberOfServers;

        String next = (servers.toArray(new String[servers.size()]))[nextItem];

        assertEquals(next,serverList.getServer());
    }

    @Test
    public void integerBoundaryWrapTest() {
        final ServerList powerOfTwoServerList = new RoundRobinServerList(new LinkedHashSet() {{add(SERVER1);add(SERVER2);add(SERVER3);add(SERVER4);}},Integer.MAX_VALUE);
        final ServerList nonPowerOfTwoServerList = new RoundRobinServerList(new LinkedHashSet() {{add(SERVER1);add(SERVER2);add(SERVER3);add(SERVER4);add(SERVER5);add(SERVER6);add(SERVER7);}},Integer.MAX_VALUE);

        final ServerList nonPowerOfTwoNoStrictOrderingServerList = new RoundRobinServerList(new LinkedHashSet() {{add(SERVER1);add(SERVER2);add(SERVER3);add(SERVER4);add(SERVER5);add(SERVER6);add(SERVER7);}},Integer.MAX_VALUE-1,false);


        assertEquals(SERVER1, powerOfTwoServerList.getServer());
        assertEquals(SERVER2, powerOfTwoServerList.getServer());
        assertEquals(SERVER3, powerOfTwoServerList.getServer());
        assertEquals(SERVER4, powerOfTwoServerList.getServer());
        assertEquals(SERVER1, powerOfTwoServerList.getServer());
        assertEquals(SERVER2, powerOfTwoServerList.getServer());


        assertEquals(SERVER3, nonPowerOfTwoServerList.getServer());
        assertEquals(SERVER4, nonPowerOfTwoServerList.getServer());
        assertEquals(SERVER5, nonPowerOfTwoServerList.getServer());
        assertEquals(SERVER6, nonPowerOfTwoServerList.getServer());
        assertEquals(SERVER7, nonPowerOfTwoServerList.getServer());
        assertEquals(SERVER1, nonPowerOfTwoServerList.getServer());

        // When we hit the Integer Width, it will start to go in reverse.
        // Integer.MAX_VALUE +1 will be 1 greater, then it will reverse
        assertEquals(SERVER2, nonPowerOfTwoNoStrictOrderingServerList.getServer());
        assertEquals(SERVER3, nonPowerOfTwoNoStrictOrderingServerList.getServer());
        assertEquals(SERVER2, nonPowerOfTwoNoStrictOrderingServerList.getServer());
        assertEquals(SERVER1, nonPowerOfTwoNoStrictOrderingServerList.getServer());
        assertEquals(SERVER7, nonPowerOfTwoNoStrictOrderingServerList.getServer());
        assertEquals(SERVER6, nonPowerOfTwoNoStrictOrderingServerList.getServer());
        assertEquals(SERVER5, nonPowerOfTwoNoStrictOrderingServerList.getServer());

    }
}
