package com.hse.chat;

/**
 * Created by kirill on 23.04.17.
 */

public class ChatPacket extends Packet {
    public String i_username, i_message;
    private String o_username, o_message;

    // Incoming constructor
    //
    public ChatPacket(String[] rawData) {
        super(rawData);
        i_username = getData(1);
        i_message = getData(2);
    }

    // Outcgoing constructor
    //
    public ChatPacket(String username, String message) {
        super(PacketType.CHAT);
        o_username = username;
        o_message = message;
    }

    @Override
    protected void indexOutgoingData() {
        addData(o_username);
        // todo consider changing...
        addData(o_message);
    }
}
