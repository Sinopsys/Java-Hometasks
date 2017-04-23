package com.hse.chat.network.packet;

/**
 * Created by kirill on 23.04.17.
 */
public class PacketUpdate extends Packet {
    private static final String SEPARATOR = ";";
    public String i_username;
    public String i_content;

    private String o_username;
    private String o_content;

    // Incoming constructor
    //
    public PacketUpdate(String[] rawData) {
        super(rawData);
        i_username = getData(1);
        i_content = "";
        for (int i = 2; i < rawData.length; ++i) {
            i_content += getData(i) + SEPARATOR;
        }
    }

    // Outcgoing constructor
    //
    public PacketUpdate(String username, String content) {
        super(PacketType.UPDATE);
        o_username = username;
        o_content = content;
    }

    @Override
    protected void indexOutgoingData() {
        addData(o_username);
        addData(o_content);
    }
}
