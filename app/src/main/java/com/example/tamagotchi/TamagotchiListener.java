package com.example.tamagotchi;

public interface TamagotchiListener{
    void onTamagotchiAction(String action);
    void addObserver(TamagotchiListener observer);

}
