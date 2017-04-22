package com.hse.chat.network.packet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 22.04.17.
 */
public abstract class Packet {
    private List<String> dataList = new ArrayList<String>();
    PacketType packetType;

    // read incoming data from the same packet
    //
    public Packet(String[] rawData) {
        for (String item : rawData) {
            dataList.add(item);
        }
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

    protected String getOutGoingData() {
        // request child packets to create a raw data list to be send to the client
        dataList.clear();
        indexOutgoingData();
        return compileOutgoingData();
    }

    protected String compileOutgoingData() {
        StringBuffer stringBuffer = new StringBuffer(packetType.name()).append(";");
        for (int i = dataList.size(); i >= 0; --i) {
            String data = dataList.get(i);
            stringBuffer.append(data).append(";");
        }
        return stringBuffer.toString();
    }
}


// EOF
