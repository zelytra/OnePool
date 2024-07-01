package fr.zelytra.game.pool.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PoolBalls {
    WHITE(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    ELEVEN(11),
    TWELVE(12),
    THIRTEEN(13),
    FOURTEEN(14),
    FIFTEEN(15);

    public final int number;

    PoolBalls(int ballNumber) {
        this.number = ballNumber;
    }

    static PoolBalls getByNumber(int number) {
        for (PoolBalls ball : PoolBalls.values()) {
            if (ball.number == number) {
                return ball;
            }
        }
        throw new IllegalArgumentException("No pool ball with number: " + number);
    }

    @JsonValue
    public int toValue() {
        return number;
    }
}

