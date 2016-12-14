package com.lottery;


import java.util.Scanner;

/**
 * Created by kirill on 13.10.16.
 */

class Lottery
{
    private int N;
    private PlayerClass[] players;
    private OrganizerClass o;


    private void GiveCardToOrganizer()
    {
        int maxGuessed = 0;
        int prize = N * 100;
        System.out.println("Prize is $" + prize);

        for (PlayerClass p : players)
        {
            int count = 0;
            for (int c : p.card)
            {
                if (c % 10 != 0)
                    ++count;
            }
            if (count >= maxGuessed)
                maxGuessed = count;
        }

        for (PlayerClass player : players)
        {
            o.receiveCard(player.card, player.getNumber(), player.getName(), maxGuessed);
        }
    }

    void play()
    {
        getNumber();
        players = new PlayerClass[N];
        setPlayers();
        o = new OrganizerClass();
        o.getShirik_6_Times(players);
        GiveCardToOrganizer();
        o.sayResults(N);
    }

    void lookForMatches(Organizer o, int key)
    {

    }

    private void setPlayers()
    {
        for (int i = 1; i < players.length + 1; ++i)
        {
            players[i - 1] = new PlayerClass();
            players[i - 1].setName("player_" + i);
            players[i - 1].setNumber(i);
            players[i - 1].setCard();
        }
    }

    private void getNumber()
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("Number of participants: ");
        try
        {
            N = sc.nextInt();
            if (N < 1)
                throw new Exception();
        } catch (Exception e)
        {
            System.out.println("Enter a positive integer number of participants!");
        }
    }

}
