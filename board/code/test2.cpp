/**
 * Copyright (c) 2020 Raspberry Pi (Trading) Ltd.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

#include "pico/stdlib.h"
#include <stdio.h>
#include <iostream>
#include <string.h>

int main() {
    stdio_init_all();
    char userinput1[2];
    char userinput2[3];
    char offCond[]="OFF";
    char onCond[]="ON";
#ifndef PICO_DEFAULT_LED_PIN
#warning blink example requires a board with a regular LED
#else
    const uint LED_PIN = PICO_DEFAULT_LED_PIN;
    gpio_init(LED_PIN);
    gpio_init(1);
    gpio_init(4);
    gpio_set_dir(1, GPIO_OUT);
    gpio_set_dir(4, GPIO_OUT);
    gpio_set_dir(LED_PIN, GPIO_OUT);
    while (true) {
        printf("Commande:");
        scanf("%s %s",&userinput1, &userinput2);
        uint GPIO_PIN_CHOICE;
        try
        {
            GPIO_PIN_CHOICE=std::stoi(userinput1);
        }
        catch(const std::exception& e)
        {
            std::cerr << e.what() << '\n';
            printf("FIRST ARGUMENT IS NOT INT! %s\n",userinput1);
        }
        //printf("%s %s %d ",userinput2,offCond,strcmp(userinput2,offCond));
        if(strcmp(userinput2,offCond)==0){
            printf("PIN ");
            printf("%s %s\n",userinput1,userinput2);
            gpio_put(GPIO_PIN_CHOICE,0);
        } else if(strcmp(userinput2,onCond)==0){
            printf("PIN ");
            printf("%s %s\n",userinput1,userinput2);
            gpio_put(GPIO_PIN_CHOICE,1);
        } else {
            printf("INCONNU!\n");
        }
    }
#endif
}
