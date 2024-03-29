package com.hse.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kirill on 22.04.17.
 */
public abstract class Packet {
    // constants
    //
    private static final String SEPARATOR = ";";

    // vars
    //
    private List<String> dataList = new ArrayList<>();
    private PacketType packetType;

    // read incoming data from the same packet
    //
    public Packet(String[] rawData) {
        dataList.addAll(Arrays.asList(rawData));
    }

    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    protected String getData(int index) {
        return dataList.get(index);
    }

    protected void addData(String data) {
        addData(data, 0);
    }

    protected void addData(String data, int index) {
        dataList.add(index, data);
    }

    protected abstract void indexOutgoingData();

    public String getOutGoingData() {
        // request child packets to create a raw data list to be send to the client
        //
        dataList.clear();
        indexOutgoingData();
        return compileOutgoingData();
    }

    protected String compileOutgoingData() {
        StringBuffer stringBuffer = new StringBuffer(packetType.name()).append(SEPARATOR);
        for (int i = dataList.size() - 1; i >= 0; --i) {
            String data = dataList.get(i);
            stringBuffer.append(data).append(SEPARATOR);
        }
        return stringBuffer.toString();
    }
}


// EOF
