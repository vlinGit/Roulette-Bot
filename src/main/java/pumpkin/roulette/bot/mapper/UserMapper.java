package pumpkin.roulette.bot.mapper;

import pumpkin.roulette.bot.common.PlayerInfo;

import java.util.List;

public interface UserMapper extends CommonMapper<PlayerInfo>{
    PlayerInfo selectById(int id);

    PlayerInfo selectByUserId(String userId);

    List<PlayerInfo> selectByPlayerInfo(PlayerInfo playerInfo);

    int insert(PlayerInfo playerInfo);

    int update(PlayerInfo playerInfo);

    void delete(int id);
}
