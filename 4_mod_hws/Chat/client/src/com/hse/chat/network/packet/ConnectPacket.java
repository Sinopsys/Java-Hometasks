package com.hse.chat.network.packet;

/**
 * Created by kirill on 22.04.17.
 */
public class ConnectPacket extends Packet {

    public String i_username;
    private String o_username;

    // Incoming constructor
    //
    public ConnectPacket(String[] rawData) {
        super(rawData);
        i_username = getData(1);
    }

    // Outcgoing constructor
    //
    public ConnectPacket(String username) {
        super(PacketType.CONNECT);
        o_username = username;
    }

    @Override
    protected void indexOutgoingData() {
        addData(o_username);
    }
}


// EOF
