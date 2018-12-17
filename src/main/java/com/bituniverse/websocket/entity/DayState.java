package com.bituniverse.websocket.entity;

import java.io.Serializable;

public class DayState implements Serializable {
    private static final long serialVersionUID = -7797887851126835407L;

    private Integer id;

    private String coin;

    private String in;

    private String out;

    private String actual;

    private String ratio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin == null ? null : coin.trim();
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in == null ? null : in.trim();
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out == null ? null : out.trim();
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual == null ? null : actual.trim();
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio == null ? null : ratio.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append(", id=").append(id);
        sb.append(", coin=").append(coin);
        sb.append(", in=").append(in);
        sb.append(", out=").append(out);
        sb.append(", actual=").append(actual);
        sb.append(", ratio=").append(ratio);
        sb.append("]");
        return sb.toString();
    }
}