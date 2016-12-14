package com.lottery;


import java.util.*;

/**
 * Created by kirill on 13.10.16.
 */

class PlayerClass implements Player
{
    private int number;
    private String name;
    int[] card;

    @Override
    public void startGame(Organizer organizer, int i)
    {

    }

    @Override
    public void acceptDigit(int digit)
    {

    }

    @Override
    public void gameOver()
    {

    }

    int getNumber()
    {
        return number;
    }

    void setNumber(int number)
    {
        this.number = number;
    }

    String getName()
    {
        return name;
    }

    void setName(String name)
    {
        this.name = name;
    }

    void setCard()
    {
        Random rd = new Random();
        card = new int[6];
        for (int i = 0; i < 6; ++i)
        {
            card[i] = rd.nextInt(10);
        }
    }
}
