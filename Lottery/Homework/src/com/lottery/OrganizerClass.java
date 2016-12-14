package com.lottery;


import java.util.*;

/**
 * Created by kirill on 13.10.16.
 */

class OrganizerClass implements Organizer
{
    private int currentValue;

    private int getCurrentValue()
    {
        return currentValue;
    }


    private int howManyTimes(int[] arr, int value)
    {
        int count = 0;
        for (int anArr : arr)
        {
            if (anArr % 10 == value)
                ++count;
        }
        return count;
    }


    void guess(PlayerClass[] players)
    {
        for (PlayerClass player : players)
        {
            for (int j = 0; j < player.card.length; ++j)
            {
                if (player.card[j] == getCurrentValue())
                {
                    int tmp = player.card[j];
                    player.card[j] = tmp * 10 + tmp;
                    System.out.println("[" + player.getName() + "]:\t" +
                            "Me! " + howManyTimes(player.card, currentValue) +
                            " occurs! Value " + getCurrentValue());
                }
            }
        }
    }


    void getShirik_6_Times(PlayerClass[] players)
    {
        Random rd = new Random();
        for (int i = 0; i < 6; ++i)
        {
            currentValue = rd.nextInt(10);
            System.out.println("[Organizer]:\tWho's gota " + currentValue + "?");
            guess(players);
        }
    }

    private List<Integer> winners = new ArrayList<>();
    private List<String> winnersNames = new ArrayList<>();

    void receiveCard(int[] card, int playerNumber, String name, int max)
    {
        int count = 0;
        for (int c : card)
        {
            if (c % 10 != 0)
                ++count;
        }
        if (count < max)
            System.out.println("[Organizer]:\t" + "Player #" +
                    playerNumber + "gets.....NOTHING!");
        else if (count == max)
        {
            winners.add(playerNumber);
            winnersNames.add(name);
        }
    }

    private int formSum()
    {
        if (winners.size() != 0)
            return 100 * winners.size();
        else
            return 0;
    }

    void sayResults(int N)
    {
        System.out.println("[Organizer]:\tThe prize sum is $" + formSum());
        if (formSum() == 0)
            System.out.println("[Organizer]:\tVI VSE LOHI!");
        else
        {
            for (int i = 0; i < winners.size(); ++i)
            {
                int prizeForEach = N * 100 / winners.size();
                System.out.println("[Organizer]:\t" + winnersNames.get(i) + "receives " + prizeForEach + "$!");
                System.out.println("[" + winnersNames.get(i) + "]" + ":\t" + "hurah! I won" + prizeForEach + "$!");
            }
        }
    }

    @Override
    public void register(Player player)
    {

    }

    @Override
    public void unregister(Player player)
    {

    }
}
