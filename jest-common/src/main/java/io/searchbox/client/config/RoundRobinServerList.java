package io.searchbox.client.config;


import io.searchbox.client.config.exception.NoServerConfiguredException;
import io.searchbox.client.util.PaddedAtomicInteger;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Uses an String[] Array to loop through the given list of servers in a round robin fashion
 *
 */
public class RoundRobinServerList implements ServerList {

    private final Set<String> servers;
    private final String[] serverList;
    private final int wrapPoint;
    private final CircularIncrement incrementer;


    public RoundRobinServerList(Set<String> servers) {
        this(servers,-1,true);
    }

    public RoundRobinServerList(Set<String> servers,boolean strictOrdering) {
        this(servers,-1, strictOrdering);
    }

    public RoundRobinServerList(Set<String> servers,int startAt) {
        this(servers,startAt, true);
    }

    public RoundRobinServerList(Set<String> servers,int startAt,boolean strictOrdering) throws NoServerConfiguredException {
        if(servers.size()==0) throw new NoServerConfiguredException("No Server is assigned to client to connect");
        this.servers = servers;
        wrapPoint = servers.size();
        int i =0;
        serverList = new String[wrapPoint];
        for(String elasticSearchServer: servers) {
            serverList[i++] = elasticSearchServer.endsWith("/") ?
                    elasticSearchServer.substring(0, elasticSearchServer.length() - 1) : elasticSearchServer;
        }

        int nextPowerOfTwo = ceilingNextPowerOfTwo(wrapPoint);

        if(nextPowerOfTwo == wrapPoint) {
            incrementer = new PowerOfTwoIncrement(nextPowerOfTwo,startAt);
        } else if(strictOrdering) {
            incrementer = new StrictOrderModulusIncrement(wrapPoint,startAt);
        } else {
            incrementer = new ModulusIncrement(wrapPoint,startAt);
        }
    }

    @Override
    public Set getServers() {
        return servers;
    }

    @Override
    public String getServer() {
        return serverList[incrementer.nextVal()];
    }


    /**
     * Calculate the next power of 2, greater than or equal to x.<p>
     * From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
     *
     * @param x Value to round up
     * @return The next power of 2 from x inclusive
     */
    public static int ceilingNextPowerOfTwo(final int x)
    {
        return 1 << (32 - Integer.numberOfLeadingZeros(x - 1));
    }

    private interface CircularIncrement {
        public int nextVal();
    }

    /**
     * Uses power of two bit masking to rotate around the ring buffer of the ServerList
     */
    private class PowerOfTwoIncrement implements CircularIncrement {

        private final AtomicInteger nextPointer;

        private final int mask;
        public PowerOfTwoIncrement(int sizeOfArray, int startPosition) {
            nextPointer = new PaddedAtomicInteger(startPosition);
            mask = sizeOfArray-1;
        }

        @Override
        public int nextVal() {
            return nextPointer.incrementAndGet()&mask;
        }
    }

    /**
     * Uses modulus operator "%" to loop around the array.
     * When the MAX_VALUE of int has been hit, the rotation around the array will loop
     * until it goes back into +p've values; when it will be in order again.. i.e.
     *
     * i.e. lets say MAX_VALUE will return element 2 in the array.
     *               MAX_VALUE+1 will return element 3
     *               MAX_VALUE+2 will return element 2 in the array.
     *               MAX_VALUE+3 will return element 1 in the array
     */
    private class ModulusIncrement implements CircularIncrement {
        private final int mask;
        private final AtomicInteger nextPointer;


        public ModulusIncrement(int sizeOfArray,int startPosition) {
            nextPointer = new PaddedAtomicInteger(startPosition);
            mask = sizeOfArray;
        }

        @Override
        public int nextVal() {
            return Math.abs(nextPointer.incrementAndGet()%mask);
        }
    }

    /**
     * Uses modulus operator "%" to loop around the array.
     * When the MAX_VALUE of int has been hit, the rotation around the array will loop
     * continue in the same direction.  This is done by using a CAS LOOP to set the next value
     * if MAX_VALUE has been reached.
     *
     */
    private class StrictOrderModulusIncrement implements CircularIncrement {
        private final int mask;
        private final AtomicInteger nextPointer;


        public StrictOrderModulusIncrement(int sizeOfArray,int startPosition) {
            nextPointer = new PaddedAtomicInteger(startPosition);
            mask = sizeOfArray;
        }

        @Override
        public int nextVal() {
            int currentVal;
            int nextVal;
            do {
                currentVal = nextPointer.get();
                nextVal = currentVal+1;
                if(currentVal==Integer.MAX_VALUE) {
                    int prev = ((currentVal%mask)+1);
                    nextVal= (prev > mask) ? 0 : prev;
                }
            } while(!nextPointer.compareAndSet(currentVal,nextVal));

            return nextVal%mask;
        }
    }
}
