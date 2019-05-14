package net;

public interface TCPConnectionListener
{
    default void onConnectionReady(TCPConnection tcpConnection){
    }
    default void onDisconnect(TCPConnection tcpConnection){
    }
    default void onException(TCPConnection tcpConnection, Exception e){
    }
    void onInputMsg(TCPConnection tcpConnection, String objectMsg) throws Exception;
}