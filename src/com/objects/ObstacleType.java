package com.objects;

import com.main.Main;
import com.math.PolarVector;

import java.util.function.Consumer;



/*
Enum class to make it easier to have custom obstacles
Main.spawnObstacle() will choose one at random and call the spawn method
 */

public enum ObstacleType {

    Spike(player -> {
        GameObjectHandler.addObject(
                new Spike("spike", new PolarVector(Main.RADIUS - 1 ,player.getPosition().angle + 80), 21, 25, player)
        );
    }),

    SpikeHorde(player -> {
        GameObjectHandler.addObjects(
                new Spike("spike", new PolarVector(Main.RADIUS - 1 ,player.getPosition().angle + 172), 19, 20, player),
                new Spike("spike", new PolarVector(Main.RADIUS - 1 ,player.getPosition().angle + 175), 25, 25, player),
                new Spike("spike", new PolarVector(Main.RADIUS - 1 ,player.getPosition().angle + 180), 31, 30, player),
                new Spike("spike", new PolarVector(Main.RADIUS - 1 ,player.getPosition().angle + 185), 25, 25, player),
                new Spike("spike", new PolarVector(Main.RADIUS - 1 ,player.getPosition().angle + 188), 19, 20, player)
        );
    }),

    MovingSpikes(player -> {
        GameObjectHandler.addObjects(
                new Spike("spike", new PolarVector(Main.RADIUS - 1 ,player.getPosition().angle + 175), 35, 30, player, true),
                new Spike("spike", new PolarVector(Main.RADIUS - 1 ,player.getPosition().angle + 185), 35, 30, player, true)
        );
    }),

    BigSpike(player -> {
        GameObjectHandler.addObject(
                new Spike("spike", new PolarVector(Main.RADIUS - 1, player.getPosition().angle + 330), 50, 50, player, true)
        );
    })

    ;

    private final Consumer<Player> onSpawn;

    ObstacleType(Consumer<Player> onSpawn) {
        this.onSpawn = onSpawn;
    }

    public void spawn(Player player) { onSpawn.accept(player); }
}
