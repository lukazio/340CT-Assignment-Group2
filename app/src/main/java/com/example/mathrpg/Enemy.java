package com.example.mathrpg;

public class Enemy {

    private int hp,attack;

    public int getHp() {return hp;}
    public int getAttack() {return attack;}

    public void setStats(int hp, int attack) {
        this.hp = hp;
        this.attack = attack;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
    }
}
