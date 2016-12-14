package com.lottery;


/**
 * Created by kirill on 13.10.16.
 */


interface Player
{
    void startGame(Organizer organizer, int i);

    //организатор просит игрока с номером i задумать комбинацию цифр;
    void acceptDigit(int digit);

    //игрок оповещается об очередной цифре;
    void gameOver(); // тираж закончен.
}
