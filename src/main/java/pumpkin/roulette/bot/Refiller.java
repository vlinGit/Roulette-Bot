package pumpkin.roulette.bot;

import org.apache.ibatis.session.SqlSession;
import pumpkin.roulette.bot.common.PlayerInfo;
import pumpkin.roulette.bot.enums.DefaultEnums;
import pumpkin.roulette.bot.mapper.UserMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Refiller {
    Timestamp timestamp;
    BatisBuilder batisBuilder;

    Refiller (BatisBuilder batisBuilder) {
        this.batisBuilder = batisBuilder;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    private void updateTimestamp() {
        timestamp.setTime(System.currentTimeMillis());
    }

    public long ellapsedTime() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        return currentTimestamp.getTime() - this.timestamp.getTime();
    }

    public void startRefill() {
        Runnable refill = () -> {
            System.out.println("Starting refill");
            try(SqlSession session = batisBuilder.getSession()){
                UserMapper userMapper = session.getMapper(UserMapper.class);
                PlayerInfo noBalance = new PlayerInfo();
                noBalance.setBalance(0);

                List<PlayerInfo> noBalancePlayers = userMapper.selectByPlayerInfo(noBalance);
                if (!noBalancePlayers.isEmpty()) {
                    System.out.println("zero balance found");
                    for (PlayerInfo playerInfo : noBalancePlayers) {
                        playerInfo.setBalance(DefaultEnums.RECHARGE_BALANCE.getValue());
                        userMapper.update(playerInfo);
                    }
                }
                updateTimestamp();
            }catch(Exception e){
                e.printStackTrace();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(refill, 0, DefaultEnums.RECHARGE_DAYS.getValue(), TimeUnit.DAYS);
    }
}
