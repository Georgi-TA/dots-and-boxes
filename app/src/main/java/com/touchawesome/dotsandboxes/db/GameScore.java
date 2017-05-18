package com.touchawesome.dotsandboxes.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import java.util.Date;

/**
 * Entity mapped to table "GameScore".
 */
@Entity(indexes = {
        @Index(value = "date ASC", unique = true)
})
public class GameScore {

    @Id
    private Long id;

    @NotNull
    private String opponent;
    private String mode;
    private java.util.Date date;
    private String score;

    public GameScore() {
    }

    public GameScore(Long id) {
        this.id = id;
    }

@Generated(hash = 1809875690)
public GameScore(Long id, @NotNull String opponent, String mode,
        java.util.Date date, String score) {
    this.id = id;
    this.opponent = opponent;
    this.mode = mode;
    this.date = date;
    this.score = score;
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public String getOpponent() {
    return this.opponent;
}

public void setOpponent(String opponent) {
    this.opponent = opponent;
}

public String getMode() {
    return this.mode;
}

public void setMode(String mode) {
    this.mode = mode;
}

public java.util.Date getDate() {
    return this.date;
}

public void setDate(java.util.Date date) {
    this.date = date;
}

public String getScore() {
    return this.score;
}

public void setScore(String score) {
    this.score = score;
}


}