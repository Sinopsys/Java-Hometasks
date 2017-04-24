package com.hse.chat;

/**
 * Created by kirill on 23.04.17.
 */

public class DisconnectPacket extends Packet {
    public String i_username;

    private String o_username;

    public DisconnectPacket(String[] rawData) {
        super(rawData);
        i_username = getData(1);
    }

    public DisconnectPacket(String username) {
        super(PacketType.DISCONNECT);
        o_username = username;
    }

    @Override
    protected void indexOutgoingData() {
        addData(o_username);
    }
}
