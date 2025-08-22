package pumpkin.roulette.bot.mapper;

import pumpkin.roulette.bot.common.PlayerInfo;

import java.util.List;

public interface CommonMapper<T> {
    T selectById(int id);

    int insert(T t);

    int update(T t);

    void delete(int id);
}
