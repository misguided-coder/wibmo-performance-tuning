import java.util.Vector;

/* Network simulation model */

// Sim Class
public class Sim implements Runnable {
    Server server;
    Client[] client = null;
    Network network = null;
    int clientId = 0;
    int tmpId = 0;
    boolean fixLogin = false;
    boolean fixConnection = false;

    public interface CacheInterface {
        void init();

        Connection getConnection();

        void freeConnection(Connection connection);

        Connection getConnection(int hashKey);

        void freeConnection(int hashKey);
    }

    public class SimpleCache implements CacheInterface {
        // Simple but not efficient: only one connection shared among all users
        Connection connection;
        boolean isBusy;

        public void init() {
            connection = new Connection();
        }

        public Connection getConnection() {
            for (;;) {
                synchronized (this) {
                    if (!isBusy) {
                        isBusy = true;
                        return connection;
                    }
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void freeConnection(Connection connection) {
            synchronized (this) {
                isBusy = false;
            }
        }

        public Connection getConnection(int hashKey) {
            return getConnection();
        }

        public void freeConnection(int hashKey) {
            freeConnection(null);
        }

    }

    public class PoolCache implements CacheInterface {
        // Manage a pool of connections: better than SimpleCache
        Connection[] connections;
        boolean[] isBusy;
        int poolSize = 5;

        public void init() {
            connections = new Connection[poolSize];
            isBusy = new boolean[poolSize];

            for (int i = 0; i < 5; i++) {
                connections[i] = new Connection();
                isBusy[i] = false;
            }
        }

        /* Slow version since it requires synchronization */
        public Connection getConnection() {
            for (;;) {
                synchronized (this) {
                    for (int i = 0; i < 5; i++) {
                        if (!isBusy[i]) {
                            isBusy[i] = true;
                            return connections[i];
                        }
                    }
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void freeConnection(Connection connection) {
            synchronized (this) {
                for (int i = 0; i < 5; i++) {
                    if (connections[i] == connection) {
                        isBusy[i] = false;
                        return;
                    }
                }
            }
        }

        /*
         * No synchronization assuming a unique mapping between hash key and an
         * entry in the connection pool
         */
        public Connection getConnection(int hashKey) {
            isBusy[hashKey] = true;
            return connections[hashKey];
        }

        public void freeConnection(int hashKey) {
            isBusy[hashKey] = false;
        }
    }

    /* For JAVA2 USERS ONLY: FAST CACHE */
    public class LocalCache implements CacheInterface {
        // Only work with Java 2: A Connection is allocated for each thread -
        // much faster than having to
        // use a lock as in SimpleCache and PoolCache - if you are not using
        // Java2, you can implement a
        // similar mechanism by matching your thread uniquely with a Connection
        // object
        private final ThreadLocal local_cache = new ThreadLocal() {
            protected Object initialValue() {
                return new Connection();
            }
        };

        public void init() {
        }

        public Connection getConnection() {
            return (Connection) local_cache.get();
        }

        public void freeConnection(Connection connection) {
        }

        public Connection getConnection(int hashKey) {
            return getConnection();
        }

        public void freeConnection(int hashKey) {
        }
    }

    /* Inner class representing the Server */
    public class Server {
        CacheInterface cache;

        public void start() {
            if (Sim.this.fixConnection) {
                // this.cache = new SimpleCache();
                // this.cache = new PoolCache();
                this.cache = new LocalCache();
                cache.init();
            }
        }

        public Result query(int id, String login, String password,
                String request) {
            // Verify login and password, connect to DataBase, disconnect and
            // return result
            Connection connect;
            if (Sim.this.fixConnection) {
                connect = cache.getConnection(id); // Good!
            } else {
                connect = new Connection(); // Bad!
            }

            Result result = new Result();
            connect.query(login, password, request, result);
            connect.release();

            if (Sim.this.fixConnection) {
                cache.freeConnection(id);
            }

            return result;
        }

        public Result query(int id, String login, String password,
                StringBuffer request) {
            // Verify login and password, connect to DataBase, disconnect and
            // return result
            Connection connect;
            if (Sim.this.fixConnection) {
                connect = cache.getConnection(id); // Good!
            } else {
                connect = new Connection(); // Bad!
            }

            Result result = new Result();
            connect.query(login, password, request, result);
            connect.release();

            if (Sim.this.fixConnection) {
                cache.freeConnection(id);
            }

            return result;
        }
    }

    // a real program could use a dictionary of queries or manipulate a
    // StringBuffer to avoid the creation
    // of multiple Strings
    static String login = "my_login";
    static String pswd = "my_password";
    static StringBuffer query = new StringBuffer("my_query goes right here");

    /* Inner class representing a client */
    public class Client {
        Server server;
        Vector results;
        int id;
        int cnt = 0;
        boolean isRunning;

        public Client(int id, Server server) {

            this.id = id;
            isRunning = false;
            this.server = server;
            results = new Vector();
        }

        public void start() {
            isRunning = true;

            for (int primaryKey = 0; primaryKey < 100; primaryKey++) {
                if (network != null)
                    network.setConnectionClientServerState(id, true);

                for (int product = 0; product < 10; product++) {
                    cnt++;

                    Result result;
                    if (Sim.this.fixLogin) {
                        result = server.query(primaryKey % 5, login, pswd,
                                query);
                    } else {
                        result = server.query(primaryKey % 5, "my_login"
                                + primaryKey, "my_password",
                                "SELECT customer FROM sales WHERE location = "
                                        + primaryKey + " AND product = "
                                        + product);
                    }

                    // Store all results
                    results.addElement(result);
                }

                if (network != null)
                    network.setConnectionClientServerState(id, false);

                if (!isRunning)
                    return;

                try {
                    if (primaryKey % 10 == 0) {
                        // We force a garbage collection periodically to
                        // simulate a
                        // large application running frequently out of memory
                        // This is absolutely not necessary.
                        System.gc();
                    }
                    Thread.sleep(10 + (cnt * id * id) % 200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            isRunning = false;
        }
    }

    public class Connection {

        public Connection() {
            // Create connection to database which takes a significant amount of
            // time
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void query(String login, String password, String request,
                Result result) {
            // Verify identity of the user and, if valid,
            // extract desired data from database and save it into the Result
            // object
            result.set(42);
            if (network != null)
                network.setConnectionDataBaseState(true);
        }

        public void query(String login, String password, StringBuffer request,
                Result result) {
            // Verify identity of the user and, if valid,
            // extract desired data from database and save it into the Result
            // object
            result.set(42);
            if (network != null) {
                network.setConnectionDataBaseState(true);
            }
        }

        public void release() {
            // Close connection opened with database
            if (network != null) {
                network.setConnectionDataBaseState(false);
            }
        }
    }

    public class Result {
        int value;

        public void set(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }
    }

    public Sim(Network network, boolean fixLogin, boolean fixConnection) {
        this.network = network;
        this.fixLogin = fixLogin;
        this.fixConnection = fixConnection;
        server = new Server();
        client = new Client[5];
        for (int id = 0; id < 5; id++) {
            client[id] = new Client(id, server);
        }
    }

    public void init() {
        server.start();
    }

    public void start() {
        clientId = 0;
        try {
            for (int id = 0; id < 5; id++) {
                Thread t = new Thread(this);
                clientId = id + 1;
                t.start();
            }

        } catch (Exception e) {
        }
    }

    public void stop() {
        clientId = 0;
        for (int id = 0; id < 5; id++) {
            client[id].stop();
        }
    }

    public void run() {
        clientId--;
        client[clientId].start();
    }

    public static void main(String argv[]) {
        System.out.println("Non Graphic version - started");
        Sim sim = new Sim(null, isLoginFixed(argv), isConnectionFixed(argv));
        sim.init();
        sim.start();

        for (;;) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isLoginFixed(String[] argv) {
        boolean isFlagSpecified = false;
        String flag = "-fl";

        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals(flag)) {
                isFlagSpecified = true;
                break;
            }
        }
        return isFlagSpecified;
    }

    private static boolean isConnectionFixed(String[] argv) {
        boolean isFlagSpecified = false;

        String flag = "-fc";
        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals(flag)) {
                isFlagSpecified = true;
                break;
            }
        }
        return isFlagSpecified;
    }
}
