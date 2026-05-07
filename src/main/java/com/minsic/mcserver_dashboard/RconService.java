package com.minsic.mcserver_dashboard;

import org.springframework.stereotype.Service;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Service
public class RconService {

    private final String host = "localhost";
    private final int port = 25575;
    private final String password = "060915";

    private static final int SERVERDATA_AUTH = 3;
    private static final int SERVERDATA_EXECCOMMAND = 2;

    public String sendCommand(String command) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 3000);
            socket.setSoTimeout(3000);

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            sendPacket(out, 1, SERVERDATA_AUTH, password);
            readPacket(in);

            sendPacket(out, 2, SERVERDATA_EXECCOMMAND, command);
            byte[] response = readPacket(in);

            return new String(response, "UTF-8").trim();
        } catch (Exception e) {
            System.out.println("RCON 에러: " + e.getClass().getName() + " - " + e.getMessage());
            return "서버 연결 실패: " + e.getMessage();
        } finally {
            if (socket != null) {
                try { socket.close(); } catch (Exception ignored) {}
            }
        }
    }

    private void sendPacket(OutputStream out, int id, int type, String payload) throws IOException {
        byte[] payloadBytes = payload.getBytes("UTF-8");
        int length = 4 + 4 + payloadBytes.length + 2;
        ByteBuffer buf = ByteBuffer.allocate(4 + length).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(length);
        buf.putInt(id);
        buf.putInt(type);
        buf.put(payloadBytes);
        buf.put((byte) 0);
        buf.put((byte) 0);
        out.write(buf.array());
        out.flush();
    }

    private byte[] readPacket(InputStream in) throws IOException {
        byte[] lenBytes = in.readNBytes(4);
        int length = ByteBuffer.wrap(lenBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        byte[] data = in.readNBytes(length);
        return ByteBuffer.wrap(data, 8, length - 10).array();
    }

    public String getPlayerList() {
        return sendCommand("list");
    }
}