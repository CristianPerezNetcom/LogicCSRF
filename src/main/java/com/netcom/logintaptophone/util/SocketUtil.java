package com.netcom.logintaptophone.util;

import com.netcom.logintaptophone.dto.AtallaHostDTO;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

@Component
@Scope("prototype")
public class SocketUtil {
    private final Proxy proxy = Proxy.NO_PROXY;
    private int defaultSoTimeout;
    private int defaultConnectTimeout;
    private Socket socketTCP;
    public PrintStream serverOutput;
    public InputStream serverInput;

    @Getter
    private String encoding;

    @PostConstruct
    private void init() {

        final int[] vals = {0, 0};
        final String[] encs = {null};

        AccessController.doPrivileged(
                (PrivilegedAction<Void>) () -> {
                    vals[0] = Integer.getInteger("sun.net.client.defaultReadTimeout", 0);
                    vals[1] = Integer.getInteger("sun.net.client.defaultConnectTimeout", 0);
                    encs[0] = System.getProperty("file.encoding", "ISO8859_1");
                    return null;
                });

        if (vals[0] != 0) {
            defaultSoTimeout = vals[0];
        }
        if (vals[1] != 0) {
            defaultConnectTimeout = vals[1];
        }

        encoding = encs[0];
        try {
            if (!isASCIISuperset(encoding)) {
                encoding = "ISO8859_1";
            }
        } catch (Exception e) {
            encoding = "ISO8859_1";
        }

        if (proxy.type() == Proxy.Type.SOCKS) {
            socketTCP = new Socket(proxy);
        } else if (proxy.type() == Proxy.Type.DIRECT) {
            socketTCP = new Socket();
        } else {
            // Still connecting through a proxy
            // server & port will be the proxy address and port
            socketTCP = new Socket(proxy);
        }
    }


    public void createAndConnectSocket(AtallaHostDTO atallaHostDTO) {
        try {
            // Instance specific timeouts do have priority, that means
            // connectTimeout & readTimeout (-1 means not set)
            // Then global default timeouts
            // Then no timeout.
            if (atallaHostDTO.getTimeOut() >= 0) {
                socketTCP.connect(new InetSocketAddress(atallaHostDTO.getIp(), atallaHostDTO.getPort()), atallaHostDTO.getTimeOut());
            } else {
                if (defaultConnectTimeout > 0) {
                    socketTCP.connect(new InetSocketAddress(atallaHostDTO.getIp(), atallaHostDTO.getPort()), defaultConnectTimeout);
                } else {
                    socketTCP.connect(new InetSocketAddress(atallaHostDTO.getIp(), atallaHostDTO.getPort()));
                }
            }

            if (atallaHostDTO.getTimeOut() >= 0) {
                socketTCP.setSoTimeout(atallaHostDTO.getTimeOut());
            } else {
                if (defaultSoTimeout > 0) {
                    socketTCP.setSoTimeout(defaultSoTimeout);
                } else {
                    socketTCP.setSoTimeout(30 * 1000);
                }
            }

            serverOutput = new PrintStream(new BufferedOutputStream(socketTCP.getOutputStream()), true, encoding);
            serverInput = new BufferedInputStream(socketTCP.getInputStream());

        } catch (UnsupportedEncodingException e) {
            throw new InternalError(encoding + "encoding not found");
        } catch (IOException ioe) {
            System.out.println("IO EXCEPTION: " + ioe.getMessage() + " - " + ioe.getLocalizedMessage());
            throw new InternalError(encoding + "encoding not found");
        }
    }

    public byte[] sendTransaction(byte[] textoBytesPeticion, String ip, int timeOut) throws Exception {
        long time = 0;
        int lenInput = 0;
        byte[] textoBytesRespuesta = null;
        try {
            socketTCP.setSendBufferSize(textoBytesPeticion.length);
            serverOutput.write(textoBytesPeticion, 0, textoBytesPeticion.length);
            if (serverOutput.checkError()) {
                System.out.println("ERROR!!!");
                throw new IOException("Error enviando datos al host: " + ip);
            }
            serverOutput.flush();
            time = System.currentTimeMillis() + timeOut;
            while (System.currentTimeMillis() < time) {
                lenInput = serverInput.available();
                if (lenInput > 0) {
                    textoBytesRespuesta = new byte[lenInput];
                    serverInput.read(textoBytesRespuesta);
                    break;
                }
                Runtime.getRuntime().freeMemory();
            }
            return textoBytesRespuesta;
        } catch (Exception e) {
            throw e;
        } finally {
            closeServer();
        }
    }

    public void closeServer() throws IOException {
        /**
         * Cerramos el socket
         **/
        if (socketTCP == null) {
            return;
        } else {
            socketTCP.close();
            socketTCP = null;
        }
        /**
         * Cerramos el flujo de entrada
         */
        if (serverInput != null) {
            serverInput.close();
            serverInput = null;
        }
        /**
         * Cerramos el flujo de salida
         */
        if (serverOutput != null) {
            serverOutput.close();
            serverOutput = null;
        }
    }

    private static boolean isASCIISuperset(String encoding) throws Exception {
        String chkS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'();/?:@&=+$,";

        // Expected byte sequence for string above
        byte[] chkB = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72,
                73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99,
                100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114,
                115, 116, 117, 118, 119, 120, 121, 122, 45, 95, 46, 33, 126, 42, 39, 40, 41, 59,
                47, 63, 58, 64, 38, 61, 43, 36, 44};

        byte[] b = chkS.getBytes(encoding);
        return Arrays.equals(b, chkB);
    }
}
