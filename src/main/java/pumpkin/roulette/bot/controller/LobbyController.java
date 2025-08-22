package pumpkin.roulette.bot.controller;

import pumpkin.roulette.bot.BatisBuilder;
import pumpkin.roulette.bot.common.Lobby;

import java.util.*;

public class LobbyController {
    private final Map<String, Lobby> lobbyMap = new HashMap<>(); // messageId <-> Lobby

    public void add(Lobby lobby){
        lobby.setListener(deleteListener(lobby.getMessageId()));
        lobbyMap.put(lobby.getMessageId(), lobby);
    }

    public Runnable deleteListener(String lobbyId){
        return () -> {
            lobbyMap.remove(lobbyId);
        };
    }

    public void delete(String lobbyId){
        lobbyMap.remove(lobbyId);
    }

    public Lobby get(String lobbyId){
        return lobbyMap.get(lobbyId);
    }

    public List<Lobby> getAll(){
        return lobbyMap.values().stream().toList();
    }
}
