package com.hse.chat;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kirill on 22.04.17.
 */
public class PacketDictionary {
    // constants
    //
    private static final Map<PacketType, Class<? extends Packet>> PACKET_DICTIONARY = new HashMap<>();

    static {
        PACKET_DICTIONARY.put(PacketType.CONNECT, ConnectPacket.class);
        PACKET_DICTIONARY.put(PacketType.DISCONNECT, DisconnectPacket.class);
        PACKET_DICTIONARY.put(PacketType.CHAT, ChatPacket.class);
        PACKET_DICTIONARY.put(PacketType.UPDATE, PacketUpdate.class);
    }

    public static Packet translatePacketType(PacketType type, String[] data) {
        Class clazz = PACKET_DICTIONARY.get(type);
        if (clazz != null) {
            try {
                return (Packet) clazz.getConstructor(String[].class).newInstance((Object) data);
            } catch (InstantiationException
                    | IllegalAccessException
                    | NoSuchMethodException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}


// EOF
