
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;
import java.nio.charset.*;
import java.lang.*;


public class NonBlockingServer {
    private static final int BUF_SIZE = 1024;
    public Selector sel = null;
    public ServerSocketChannel server = null;
    public SocketChannel socket = null;
    public int port = 4900;
    String result = null;


    public NonBlockingServer() {
        System.out.println("Inside default ctor");
    }

    public NonBlockingServer(int port) {
        System.out.println("Inside the other ctor");
        port = port;
    }

    public void initializeOperations() throws IOException, UnknownHostException {
        System.out.println("Inside initialization");
        sel = Selector.open();
        server = ServerSocketChannel.open();
        server.configureBlocking(false);

        InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), port);
        server.socket().bind(isa);
    }

    public void startServer() throws IOException {
        System.out.println("Inside startserver");
        initializeOperations();
        System.out.println("Abt to block on select()");
        SelectionKey acceptKey = server.register(sel, SelectionKey.OP_ACCEPT);

        while (acceptKey.selector().select() > 0) {

            Set readyKeys = sel.selectedKeys();
            Iterator it = readyKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();

                if (key.isAcceptable()) {
                    System.out.println("Key is Acceptable");
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    socket = ssc.accept();
                    socket.configureBlocking(false);
                    socket.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }
                if (key.isReadable()) {
                    String ret = readMessage(key);
                    if (!ret.isEmpty() && !ret.equals("quit") && !ret.equals("shutdown")) {
                        System.out.println("Key is readable");
                        try {
                            RandomAccessFile rdm = new RandomAccessFile(new File(ret), "r");
                            FileChannel fc = rdm.getChannel();
                            ByteBuffer buf = ByteBuffer.allocate((int) rdm.length());
                            fc.read(buf);
                            buf.flip();
                            key.attach(buf);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        writeMessage(socket, ret);
                    }
                }
                if (key.isWritable()) {
                    String ret = readMessage(key);
                    socket = (SocketChannel) key.channel();
                    ByteBuffer buf = (ByteBuffer) key.attachment();
                    if (result.length() > 0 && buf != null && buf.hasRemaining()) {
                        System.out.println("THe key is writable");
                        writeMessage(socket, ret);
                    }
                }
            }
        }
    }

    public void writeMessage(SocketChannel socket, String ret) {
        System.out.printf("Inside the loop%n");
        if (ret.equals("quit") || ret.equals("shutdown")) {
            return;
        }
        File file = new File(ret);
        try {
            RandomAccessFile rdm = new RandomAccessFile(file, "r");
            FileChannel fc = rdm.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
            Charset set = Charset.forName("us-ascii");
            CharsetDecoder dec = set.newDecoder();
            int nBytes = 0;
            while (fc.read(buffer) > 0) {
                buffer.flip();
                CharBuffer charBuf = dec.decode(buffer);
                System.out.printf(charBuf.toString() + "%n");
                buffer = ByteBuffer.wrap((charBuf.toString()).getBytes());
                nBytes += socket.write(buffer);
                buffer.limit(buffer.capacity()).position(0);
            }
            System.out.printf("nBytes = %d%n", nBytes);
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String readMessage(SelectionKey key) {
        int nBytes = 0;
        socket = (SocketChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocate(1024);
        try {
            nBytes = socket.read(buf);
            buf.flip();
            Charset charset = Charset.forName("us-ascii");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buf);
            result = charBuffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String args[]) {
        NonBlockingServer nb = new NonBlockingServer();
        try {
            nb.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}


// EOF
