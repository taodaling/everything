package com.daltao.oj.old.submit.codeforces;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by dalt on 2018/5/1.
 */
public class CF927A {

    static final int INF = (int) 1e8;
    static final int MOD = (int) 1e9 + 7;
    public static BlockReader input;
    public static PrintStream output;
    public static Debug debug;

    public static void main(String[] args) throws FileNotFoundException {
        init();

        solve();

        destroy();
    }

    public static void init() throws FileNotFoundException {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            // input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\codeforces\\CF963C.in"));
            input = new BlockReader(System.in);
            output = System.out;
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        debug = new Debug();
        debug.enter("main");
    }

    public static void solve() {
        RuntimeSystem.run();
    }

    public static void destroy() {
        output.flush();
        debug.exit();
        debug.statistic();
    }

    public static interface Cmd {
        public void finish();

        //If the process is canceled, return true, else false
        public boolean cancel(int time);

        public int costTime();

    }

    public static class MoveAndPickCmd implements Cmd {
        Passenger passenger;
        Driver driver;
        int time;

        public MoveAndPickCmd(Driver driver, Passenger passenger) {
            this.driver = driver;
            this.passenger = passenger;
            time = Eval.distance(driver.location, passenger.source);
        }

        @Override
        public void finish() {
            driver.passengerList.add(passenger);
            driver.location = passenger.source;
            passenger.waitUntil = RuntimeSystem.now();

            System.out.println(String.format("driver #%d drive to [%d, %d] and pick up %d on %d", driver.id, driver.location.x, driver.location.y,
                    passenger.id, RuntimeSystem.now()));
        }

        @Override
        public boolean cancel(int time) {
            return false;
        }

        @Override
        public int costTime() {
            return time;
        }
    }

    public static class MoveAndDropOffCmd implements Cmd {
        Passenger passenger;
        Driver driver;
        int time;

        public MoveAndDropOffCmd(Driver driver, Passenger passenger) {
            this.driver = driver;
            this.passenger = passenger;
            time = Eval.distance(driver.location, passenger.target);
        }

        @Override
        public void finish() {
            driver.passengerList.remove(passenger);
            driver.location = passenger.target;

            System.out.println(String.format("driver #%d drive to [%d, %d] and drop off passenger #%d on %d",
                    driver.id, driver.location.x, driver.location.y, passenger.id, RuntimeSystem.now()));
        }

        @Override
        public boolean cancel(int time) {
            driver.location = Eval.move(driver.location, passenger.target, time);

            System.out.println(String.format("driver #%d drive to [%d, %d] on %d", driver.id, driver.location.x, driver.location.y,
                    RuntimeSystem.now()));
            return true;
        }

        @Override
        public int costTime() {
            return time;
        }
    }

    public static class WaitCmd implements Cmd {
        @Override
        public void finish() {

        }

        @Override
        public boolean cancel(int time) {
            return true;
        }

        @Override
        public int costTime() {
            return (int) 1e8;
        }
    }

    public static class CmdFuture {
        Cmd cmd;
        Driver driver;
        int startTime;
        int endTime;
        boolean cancel;

        public CmdFuture(Driver driver, Cmd cmd) {
            this.driver = driver;
            this.cmd = cmd;
            this.startTime = RuntimeSystem.now();
            this.endTime = startTime + cmd.costTime();
        }

        public boolean cancel() {
            if (cancel) {
                return true;
            }
            cancel = cmd.cancel(RuntimeSystem.now() - startTime);
            return cancel;
        }

        public void finish() {
            if (cancel) {
                return;
            }
            cmd.finish();
            cancel = true;
        }
    }

    public static class Eval {
        public static int profit(Passenger passenger, int pickUpTime, int arriveAtTime) {
            double waitTime = pickUpTime - passenger.waitSince;
            double differenceBetweenTravelTime = arriveAtTime - pickUpTime - passenger.ideaTime;
            double alpha = 1 - Math.min(waitTime * waitTime +
                    differenceBetweenTravelTime * differenceBetweenTravelTime, 1e7) / 1e7;
            return (int) (alpha * (100 + passenger.ideaTime));
        }

        public static Location move(Location source, Location target, int time) {

            Location newLocation = new Location();
            newLocation.x = source.x;
            newLocation.y = source.y;

            int dx = Math.min(Math.abs(source.x - target.x), time);
            int dy = Math.min(Math.abs(source.y - target.y), time - dx);

            if (newLocation.x < target.x) {
                newLocation.x += dx;
            } else {
                newLocation.x -= dx;
            }

            if (newLocation.y < target.y) {
                newLocation.y += dy;
            } else {
                newLocation.y -= dy;
            }

            return newLocation;
        }

        public static int distance(Location a, Location b) {
            return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        }
    }

    public static class Driver {
        List<Passenger> passengerList = new ArrayList<>(4);
        Location location;
        CmdFuture cmdFuture;
        int id;

        public void acceptBill(Passenger passenger) {
            Cmd cmd = new MoveAndPickCmd(this, passenger);
            cmdFuture = RuntimeSystem.addCmd(this, cmd);
        }

        public void cmdFinished() {
            cmdFuture = null;
            for (Passenger passenger : RuntimeSystem.waiting) {
                if (this.passengerList.size() == 4) {
                    break;
                }
                newOrder(passenger);
                if (cmdFuture != null) {
                    return;
                }
            }

            if (this.passengerList.size() == 0) {
                cmdFuture = RuntimeSystem.addCmd(this, new WaitCmd());
            } else {
                int next = (int) maxProfitAndNextPassenger(RuntimeSystem.now(), location);
                cmdFuture = RuntimeSystem.addCmd(this, new MoveAndDropOffCmd(this, passengerList.get(next)));
            }
        }

        public void newOrder(Passenger passenger) {
            if (passengerList.size() == 4) {
                return;
            }

            //If driver is going to pick up passenger, he will ignore the newOrder
            if (!cmdFuture.cancel()) {
                return;
            }

            int currentFee = 0;
            for (Passenger picked : passengerList) {
                currentFee += picked.acceptedBill.profit;
            }

            passengerList.add(passenger);
            int from = passenger.waitUntil = RuntimeSystem.now() + Eval.distance(location, passenger.source);
            Location currentLocation = passenger.source;

            int maxProfit = (int) (maxProfitAndNextPassenger(from, currentLocation) >> 32);

            passengerList.remove(passengerList.size() - 1);
            if (maxProfit <= currentFee) {
                return;
            }

            Bill bill = new Bill(this, passenger, maxProfit - currentFee);
            bill.send();
        }

        //The first 32 bit represents the fee, and the lower 32 bits mean the next passenger
        public long maxProfitAndNextPassenger(int from, Location next) {
            int[][] permutations = RuntimeSystem.permutation[passengerList.size()];
            int maxProfit = -1;
            int nextPassenger = 0;
            for (int[] permutation : permutations) {
                Location currentLocation2 = next;
                int from2 = from;
                int fee = 0;
                for (int v : permutation) {
                    Passenger picked = passengerList.get(v);
                    from2 = from2 + Eval.distance(currentLocation2, picked.target);
                    currentLocation2 = picked.target;
                    fee += Eval.profit(picked, picked.waitUntil, from2);
                }
                maxProfit = Math.max(maxProfit, fee);
                if (fee > maxProfit) {
                    maxProfit = fee;
                    nextPassenger = permutation[0];
                }
            }
            return ((long) maxProfit << 32) | nextPassenger;
        }
    }

    public static class RuntimeSystem {
        static List<Passenger> waiting = new ArrayList<>();
        static PriorityQueue<CmdFuture> cmdPriorityQueue = new PriorityQueue<CmdFuture>(new Comparator<CmdFuture>() {
            @Override
            public int compare(CmdFuture a, CmdFuture b) {
                return a.endTime - b.endTime;
            }
        });
        static Driver[] registeredDrivers;
        static int time;
        static Random random = new Random(19950823);
        static int[][][] permutation = new int[][][]{
                new int[0][1], new int[1][1], new int[2][2], new int[6][3], new int[24][4]
        };

        static {
            for (int i = 1; i <= 4; i++) {
                getPermutation(i, 0, new int[1], new int[i]);
            }
        }

        static void getPermutation(int maxValue, int step, int[] inc, int[] trace) {
            if (step == maxValue) {
                permutation[maxValue][inc[0]++] = trace.clone();
                return;
            }
            for (int i = 0; i < maxValue; i++) {
                boolean exist = false;
                for (int j = 0; j < step; j++) {
                    if (trace[j] == i) {
                        exist = true;
                        break;
                    }
                }
                if (exist) {
                    continue;
                }
                trace[step] = i;
                getPermutation(maxValue, step + 1, inc, trace);
            }
        }

        public static int now() {
            return time;
        }

        public static void run() {
            int w = input.nextInteger();
            int h = input.nextInteger();

            int carNum = input.nextInteger();
            registeredDrivers = new Driver[carNum];
            for (int i = 0; i < carNum; i++) {
                Driver driver = new Driver();
                driver.id = i;
                driver.cmdFuture = addCmd(driver, new WaitCmd());
                driver.location = new Location();
                driver.location.x = input.nextInteger();
                driver.location.y = input.nextInteger();
                registeredDrivers[i] = driver;
            }

            int passengerNum = 0;
            while (true) {
                Passenger passenger = new Passenger();
                passenger.id = passengerNum++;
                passenger.waitSince = input.nextInteger();
                passenger.source = new Location();
                passenger.source.x = input.nextInteger();
                passenger.source.y = input.nextInteger();
                passenger.target = new Location();
                passenger.target.x = input.nextInteger();
                passenger.target.y = input.nextInteger();
                passenger.ideaTime = Eval.distance(passenger.source, passenger.target);
                passenger.ideaProfit = passenger.ideaTime + 100;
                if (passenger.waitSince == -1) {
                    passenger.waitSince = Integer.MAX_VALUE;
                }

                while (!cmdPriorityQueue.isEmpty()) {
                    CmdFuture next = cmdPriorityQueue.poll();
                    if (next.endTime <= passenger.waitSince) {
                        time = next.endTime;
                        next.finish();
                        next.driver.cmdFinished();
                    } else {
                        cmdPriorityQueue.add(next);
                    }
                }

                time = passenger.waitSince;
                if (passenger.waitSince != Integer.MAX_VALUE) {
                    waiting.add(passenger);
                    for (Driver driver : registeredDrivers) {
                        if (passenger.acceptedBill != null) {
                            break;
                        }
                        driver.newOrder(passenger);
                    }
                } else {
                    break;
                }
            }
        }

        public static CmdFuture addCmd(Driver driver, Cmd cmd) {
            CmdFuture cmdFuture = new CmdFuture(driver, cmd);
            if (cmd.getClass() != WaitCmd.class) {
                cmdPriorityQueue.add(cmdFuture);
            }
            return cmdFuture;
        }

        public static double nextDouble() {
            return random.nextDouble();
        }
    }

    public static class Bill {
        int profit;
        Driver driver;
        Passenger passenger;

        public Bill(Driver driver, Passenger passenger, int profit) {
            this.driver = driver;
            this.passenger = passenger;
            this.profit = profit;
        }

        public void send() {
            passenger.receiveBill(this);
        }

        /**
         * The passenger accept this deal
         */
        public void accept() {
            RuntimeSystem.waiting.remove(passenger);

            driver.acceptBill(passenger);
        }

    }

    public static class Passenger {
        final static double k = 1;
        Location target;
        Location source;
        int waitSince;
        int waitUntil;
        int arriveAt;
        int ideaTime;
        Bill acceptedBill;
        int id;
        int ideaProfit;

        public void receiveBill(Bill bill) {
            if (acceptedBill != null) {
                return;
            }

            acceptedBill = bill;
            bill.accept();

            /*int waitTime = RuntimeSystem.now() - waitSince;
            //random() < exp((fixedWeight - weight) / (k * T))
            if (RuntimeSystem.nextDouble() < 2 * Math.exp((double)(bill.profit - ideaProfit) / (Math.max(1, waitTime) * k))) {
                acceptedBill = bill;
                bill.accept();
            }*/
        }
    }

    public static class Location {
        int x;
        int y;
    }

    public static class Debug {
        boolean debug = System.getProperty("ONLINE_JUDGE") == null;
        Deque<ModuleRecorder> stack = new ArrayDeque<>();
        Map<String, Module> fragmentMap = new HashMap<>();

        public void enter(String module) {
            if (debug) {
                stack.push(new ModuleRecorder(getModule(module)));
            }
        }

        public Module getModule(String moduleName) {
            Module module = fragmentMap.get(moduleName);
            if (module == null) {
                module = new Module(moduleName);
                fragmentMap.put(moduleName, module);
            }
            return module;
        }

        public void exit() {
            if (debug) {
                ModuleRecorder fragment = stack.pop();
                fragment.exit();
            }
        }

        public void statistic() {
            if (!debug) {
                return;
            }

            if (stack.size() > 0) {
                throw new RuntimeException("Exist unexited tag");
            }
            System.out.println("\n------------------------------------------");

            System.out.println("memory used " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + "M");

            System.out.println("\n------------------------------------------");
            for (Module module : fragmentMap.values()) {
                System.out.println(String.format("Module %s : enter %d : cost %d", module.moduleName, module.enterTime, module.totaltime));
            }

            System.out.println("------------------------------------------");
        }

        public static class ModuleRecorder {
            Module fragment;
            long time;

            public ModuleRecorder(Module fragment) {
                this.fragment = fragment;
                time = System.currentTimeMillis();
            }

            public void exit() {
                fragment.totaltime += System.currentTimeMillis() - time;
                fragment.enterTime++;
            }
        }

        public static class Module {
            String moduleName;
            long totaltime;
            long enterTime;

            public Module(String moduleName) {
                this.moduleName = moduleName;
            }
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 8192);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
        }

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public int nextInteger() {
            skipBlank();
            int ret = 0;
            boolean rev = false;
            if (next == '+' || next == '-') {
                rev = next == '-';
                next = nextByte();
            }
            while (next >= '0' && next <= '9') {
                ret = (ret << 3) + (ret << 1) + next - '0';
                next = nextByte();
            }
            return rev ? -ret : ret;
        }

        public long nextLong() {
            skipBlank();
            long ret = 0;
            boolean rev = false;
            if (next == '+' || next == '-') {
                rev = next == '-';
                next = nextByte();
            }
            while (next >= '0' && next <= '9') {
                ret = (ret << 3) + (ret << 1) + next - '0';
                next = nextByte();
            }
            return rev ? -ret : ret;
        }

        public int nextBlock(char[] data, int offset) {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() {
            skipBlank();
            return next != EOF;
        }
    }
}
