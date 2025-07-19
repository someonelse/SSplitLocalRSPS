// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   signlink.java

package sign;


import java.io.*;
import java.net.*;
import java.io.FileInputStream;
import java.io.DataInputStream;

public final class signlink
    implements Runnable
{

    public static void startpriv(InetAddress inetaddress)
    {
        threadliveid = (int)(Math.random() * 99999999D);
        if(active)
        {
            try
            {

                Thread.sleep(500L);
            }
            catch(Exception _ex) { }
            active = false;
        }
        socketreq = 0;
        threadreq = null;
        dnsreq = null;
        savereq = null;
        urlreq = null;
        socketip = inetaddress;
        Thread thread = new Thread(new signlink());
        thread.setDaemon(true);
        thread.start();
        while(!active)
            try
            {
                Thread.sleep(50L);
            }
            catch(Exception _ex) { }
    }

    @Override
    public void run() {
        System.out.println(">>> signlink.run() entered");
        active = true;
        String s = findcachedir();
        uid = getuid(s);

        // 1) Initialize cache files
        try {
            File file = new File(s + "main_file_cache.dat");
            if (file.exists() && file.length() > 0x3200000L) {
                file.delete();
            }
            cache_dat = new RandomAccessFile(s + "main_file_cache.dat", "rw");
            for (int j = 0; j < 5; j++) {
                cache_idx[j] = new RandomAccessFile(
                    s + "main_file_cache.idx" + j, "rw"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2) Main loop
        for (int i = threadliveid; threadliveid == i; ) {

            // a) Socket request
            if (socketreq != 0) {
                try {
                    socket = new Socket(socketip, socketreq);
                } catch (Exception ex) {
                    socket = null;
                }
                socketreq = 0;

            // b) Thread request
            } else if (threadreq != null) {
                Thread t = new Thread(threadreq);
                t.setDaemon(true);
                t.start();
                t.setPriority(threadreqpri);
                threadreq = null;

            // c) DNS lookup
            } else if (dnsreq != null) {
                try {
                    dns = InetAddress.getByName(dnsreq).getHostName();
                } catch (Exception ex) {
                    dns = "unknown";
                }
                dnsreq = null;

            // d) File save request
            } else if (savereq != null) {
                if (savebuf != null) {
                    try (FileOutputStream fos =
                             new FileOutputStream(s + savereq)) {
                        fos.write(savebuf, 0, savelen);
                    } catch (Exception ignore) { }
                }
                if (waveplay) {
                    waveplay = false;
                }
                if (midiplay) {
                    midi = s + savereq;
                    midiplay = false;
                }
                savereq = null;

            // e) Disk-load URL request
            } else if (urlreq != null) {
                // Top-line debug for every request
            	System.out.println("[signlink] urlreq = \"" + urlreq + "\" | resource base = resources/");

                // Try remote first for title (legacy path)
                try {
                	URL u = new File("resources/" + urlreq).toURI().toURL();
                    System.out.println("[signlink] Attempt remote URL: " + u);
                    urlstream = new DataInputStream(u.openStream());
                } catch (Exception e) {
                    System.err.println("[signlink] remote load failed: " + e);
                    // Fallback to disk
                    try {
                        String path = "Data/" + urlreq;
                        System.out.println("[signlink] Loading from disk: " + path);
                        urlstream = new DataInputStream(new FileInputStream(path));
                    } catch (Exception ex) {
                        urlstream = null;
                        System.err.println("[signlink] disk load failed: " + ex);
                    }
                }

                urlreq = null;
            }

            // f) Pause before next iteration
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ignored) { }

        } // end for-loop

    } // end run()

    public static String findcachedir() {
        String as[] = {"C:/"};
        if(storeid < 32 || storeid > 34)
            storeid = 32;
        String s = "SoulSplit1";
        for(int i = 0; i < as.length; i++)
            try {
                String s1 = as[i];
                if(s1.length() > 0) {
                    File file = new File(s1);
                    if(!file.exists())
                        continue;
                }
                File file1 = new File(s1 + s);
                if(file1.exists() || file1.mkdir())
                    return s1 + s + "/";
            }
            catch(Exception _ex) { }
        return null;
    }

    private static int getuid(String s)
    {
        try
        {
            File file = new File(s + "uid.dat");
            if(!file.exists() || file.length() < 4L)
            {
                DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(s + "uid.dat"));
                dataoutputstream.writeInt((int)(Math.random() * 99999999D));
                dataoutputstream.close();
            }
        }
        catch(Exception _ex) { }
        try
        {
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(s + "uid.dat"));
            int i = datainputstream.readInt();
            datainputstream.close();
            return i + 1;
        }
        catch(Exception _ex)
        {
            return 0;
        }
    }
    public static synchronized Socket opensocket(int i)
        throws IOException
    {
        for(socketreq = i; socketreq != 0;)
            try
            {
                Thread.sleep(50L);
            }
            catch(Exception _ex) { }

        if(socket == null)
            throw new IOException("could not open socket");
        else
            return socket;
    }

    public static synchronized DataInputStream openurl(String s)
        throws IOException
    {
        for(urlreq = s; urlreq != null;)
            try
            {
                Thread.sleep(50L);
            }
            catch(Exception _ex) { }

        if(urlstream == null)
            throw new IOException("could not open: " + s);
        else
            return urlstream;
    }

    public static synchronized void dnslookup(String s)
    {
        dns = s;
        dnsreq = s;
    }

    public static synchronized void startthread(Runnable runnable, int i)
    {
        threadreqpri = i;
        threadreq = runnable;
    }

    public static synchronized boolean wavesave(byte abyte0[], int i)
    {
        if(i > 0x1e8480)
            return false;
        if(savereq != null)
        {
            return false;
        } else
        {
            wavepos = (wavepos + 1) % 5;
            savelen = i;
            savebuf = abyte0;
            waveplay = true;
            savereq = "sound" + wavepos + ".wav";
            return true;
        }
    }

    public static synchronized boolean wavereplay()
    {
        if(savereq != null)
        {
            return false;
        } else
        {
            savebuf = null;
            waveplay = true;
            savereq = "sound" + wavepos + ".wav";
            return true;
        }
    }

    public static synchronized void midisave(byte abyte0[], int i)
    {
        if(i > 0x1e8480)
            return;
        if(savereq != null)
        {
        } else
        {
            midipos = (midipos + 1) % 5;
            savelen = i;
            savebuf = abyte0;
            midiplay = true;
            savereq = "jingle" + midipos + ".mid";
        }
    }

    public static void reporterror(String s)
    {
        System.out.println("Error: " + s);
    }

    private signlink()
    {
    }

    public static final int clientversion = 317;
    public static int uid;
    public static int storeid = 32;
    public static RandomAccessFile cache_dat = null;
    public static final RandomAccessFile[] cache_idx = new RandomAccessFile[5];
    public static boolean sunjava;
    private static boolean active;
    private static int threadliveid;
    private static InetAddress socketip;
    private static int socketreq;
    private static Socket socket = null;
    private static int threadreqpri = 1;
    private static Runnable threadreq = null;
    private static String dnsreq = null;
    public static String dns = null;
    private static String urlreq = null;
    private static DataInputStream urlstream = null;
    private static int savelen;
    private static String savereq = null;
    private static byte[] savebuf = null;
    private static boolean midiplay;
    private static int midipos;
    public static String midi = null;
    public static int midivol;
    public static int midifade;
    private static boolean waveplay;
    private static int wavepos;
    public static int wavevol;
    public static boolean reporterror = true;
    public static String errorname = "";

}
