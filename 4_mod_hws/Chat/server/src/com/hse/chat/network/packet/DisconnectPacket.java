package com.hse.chat.network.packet;

/**
 * Created by kirill on 23.04.17.
 */
public class DisconnectPacket extends  Packet{
    public DisconnectPacket(String[] rawData) {
        super(rawData);
    }

    public DisconnectPacket(PacketType packetType) {
        super(packetType);
    }

    @Override
    protected void indexOutgoingData() {

    }
}
