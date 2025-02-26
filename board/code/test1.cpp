/**
 * Copyright (c) 2020 Raspberry Pi (Trading) Ltd.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

#include "pico/stdlib.h"
#include <stdio.h>
#include <iostream>
#include <string.h>
#include "hardware/gpio.h"
#include "hardware/adc.h"
#include "hardware/uart.h"

int cnt = 0;

#define UART_ID uart0
#define BAUD_RATE 115200

#define UART_TX_PIN 12
#define UART_RX_PIN 13

int main() {
    stdio_init_all();

    uart_init(UART_ID, BAUD_RATE);

    gpio_set_function(UART_TX_PIN, GPIO_FUNC_UART);
    gpio_set_function(UART_RX_PIN, GPIO_FUNC_UART);


    char userinput1[5];
    char userinput2[3];
    char userinput3[3];
    char offCond[]="OF";
    char onCond[]="ON";
    char cmdgpion[]="IO ON";
    char cmdgpiof[]="IO OF";
    const uint LED_PIN = PICO_DEFAULT_LED_PIN;
    gpio_init(LED_PIN);
    gpio_init(1);
    gpio_init(2);
    gpio_init(3);
    gpio_init(4);
    gpio_init(5);
    gpio_init(6);
    gpio_init(7);
    gpio_init(8);
    gpio_init(26);
    gpio_init(28);
    gpio_set_dir(1, GPIO_OUT);
    gpio_set_dir(2, GPIO_OUT);
    gpio_set_dir(3, GPIO_OUT);
    gpio_set_dir(4, GPIO_OUT);
    gpio_set_dir(5, GPIO_OUT);
    gpio_set_dir(6, GPIO_OUT);
    gpio_set_dir(7, GPIO_OUT);
    gpio_set_dir(8, GPIO_OUT);
    //gpio_set_dir(26, GPIO_IN);
    gpio_set_dir(28, GPIO_IN);
    gpio_set_dir(LED_PIN, GPIO_OUT);
    gpio_pull_down(28);
    adc_init();
    adc_gpio_init(26);
    adc_select_input(0);
    char user_act[11] = "----------";
    char VALUE[3] = "ON";

    bool TEST_BUTTON_ACQ_ON=true;
    bool TEST_BUTTON_ACQ_OF=true;

    bool KIBS_COM_1_TOSEND=true;
    bool KIBS_COM_2_TOSEND=true;
    bool KIBS_COM_3_TOSEND=true;
    //on_uart_rx();

    char command[8]="A";
    int MAX_BYTES = 8;
    int i=0;


    while (true) {
        while (uart_is_readable(UART_ID)) {
            int ch = uart_getc(UART_ID);
            char chAscii = static_cast<char>(ch);
            
            if(ch!='\n'){
                command[i]=chAscii;
                i++;
                //printf("%c",chAscii);
            } else {
                std::string COMMAND = command;
                std::string INSTRUCTION(COMMAND.substr(0, 2));
                std::string ARGUMENT(COMMAND.substr(3, 2));
                std::string VALUE(COMMAND.substr(6, 1));
                std::cout << INSTRUCTION << ", " << ARGUMENT << ", " << VALUE << ".-> " << COMMAND << std::endl;
                std::cout.flush();
                if(INSTRUCTION.compare("IO")==0){
                    //gpio_put(1,1);
                    //uart_puts(UART_ID, "GPIO!");
                    if(ARGUMENT.compare("A0")==0){
                        uint GPIO_PIN_VALUE=std::stoi(VALUE);
                        gpio_put(1, GPIO_PIN_VALUE);
                    }
                    if(ARGUMENT.compare("A1")==0){
                        uint GPIO_PIN_VALUE=std::stoi(VALUE);
                        gpio_put(2, GPIO_PIN_VALUE);
                    }
                    if(ARGUMENT.compare("A2")==0){
                        uint GPIO_PIN_VALUE=std::stoi(VALUE);
                        gpio_put(3, GPIO_PIN_VALUE);
                    }
                    if(ARGUMENT.compare("A3")==0){
                        uint GPIO_PIN_VALUE=std::stoi(VALUE);
                        gpio_put(4, GPIO_PIN_VALUE);
                    }
                    if(ARGUMENT.compare("A4")==0){
                        uint GPIO_PIN_VALUE=std::stoi(VALUE);
                        gpio_put(5, GPIO_PIN_VALUE);
                    }
                    if(ARGUMENT.compare("A5")==0){
                        uint GPIO_PIN_VALUE=std::stoi(VALUE);
                        gpio_put(6, GPIO_PIN_VALUE);
                    }
                    if(ARGUMENT.compare("A6")==0){
                        uint GPIO_PIN_VALUE=std::stoi(VALUE);
                        gpio_put(7, GPIO_PIN_VALUE);
                    }
                    if(ARGUMENT.compare("A7")==0){
                        uint GPIO_PIN_VALUE=std::stoi(VALUE);
                        gpio_put(8, GPIO_PIN_VALUE);
                    }
                }
                if(INSTRUCTION.compare("SE")==0){ //special event
                    //uart_puts(UART_ID, "GPIO!");
                    if(ARGUMENT.compare("A0")==0){ //TEST
                        uint GPIO_PIN_VALUE=std::stoi(VALUE);
                        gpio_put(1,GPIO_PIN_VALUE);
                        gpio_put(2,GPIO_PIN_VALUE);
                        gpio_put(3,GPIO_PIN_VALUE);
                        gpio_put(4,GPIO_PIN_VALUE);
                        gpio_put(5,GPIO_PIN_VALUE);
                        gpio_put(6,GPIO_PIN_VALUE);
                        gpio_put(7,GPIO_PIN_VALUE);
                        gpio_put(8,GPIO_PIN_VALUE);
                    }
                }
                //strcpy(command,"");
                i=0;
                /**/
                
                /*if (uart_is_writable(UART_ID)) {
                    printf("RECU %s",command);
                    printf("EN INT %i",command);
                }*/
            }
        }
        //uint8_t ch = uart_getc(UART_ID);
        //printf("USRX %s",ch);
        if(!gpio_get(28)){
            /*gpio_put(1,0);
            gpio_put(2,0);
            gpio_put(3,0);
            gpio_put(4,0);
            gpio_put(5,0);
            gpio_put(6,0);
            gpio_put(7,0);
            gpio_put(8,0);*/
            if(TEST_BUTTON_ACQ_OF){
                strcpy(user_act, "01");
                strcpy(VALUE, "OF");
                printf("USIT %s %s",user_act, VALUE); //USer InTeraction
                //uart_puts(UART_ID, "USIT BTN_TEST-- OF");
                TEST_BUTTON_ACQ_OF=false;
                TEST_BUTTON_ACQ_ON=true;
            }
        } else if(gpio_get(28)) {
            /*gpio_put(1,1);
            gpio_put(2,1);
            gpio_put(3,1);
            gpio_put(4,1);
            gpio_put(5,1);
            gpio_put(6,1);
            gpio_put(7,1);
            gpio_put(8,1);*/
            if(TEST_BUTTON_ACQ_ON){
                strcpy(user_act, "01");
                strcpy(VALUE, "ON");
                printf("USIT %s %s",user_act, VALUE);
                //uart_puts(UART_ID, "USIT BTN_TEST-- ON");
                TEST_BUTTON_ACQ_OF=true;
                TEST_BUTTON_ACQ_ON=false;
            }
        }
        const float conversion_factor = 3.3f / (1 << 12);
        uint16_t result = adc_read();
        float KIBS_VOLTAGE = result * conversion_factor;
        if(KIBS_VOLTAGE<1.17 && KIBS_COM_1_TOSEND){
            KIBS_COM_1_TOSEND=false;
            KIBS_COM_2_TOSEND=true;
            KIBS_COM_3_TOSEND=true;
            strcpy(user_act, "0A");
            strcpy(VALUE, "01");
            printf("USIT %s %s",user_act, VALUE);
        } else if (KIBS_VOLTAGE>1.17 && KIBS_VOLTAGE<2.13 && KIBS_COM_2_TOSEND){
            KIBS_COM_1_TOSEND=true;
            KIBS_COM_2_TOSEND=false;
            KIBS_COM_3_TOSEND=true;
            strcpy(user_act, "0A");
            strcpy(VALUE, "00");
            printf("USIT %s %s",user_act, VALUE);
        } else if (KIBS_VOLTAGE>2.13 && KIBS_COM_3_TOSEND){
            KIBS_COM_1_TOSEND=true;
            KIBS_COM_2_TOSEND=true;
            KIBS_COM_3_TOSEND=false;
            strcpy(user_act, "0A");
            strcpy(VALUE, "02");
            printf("USIT %s %s",user_act, VALUE);
        }
        //printf("0x%03x,%f V\n", result, result * conversion_factor);

        sleep_ms(100);
        //printf("Commande:");
        /*scanf("%s %s %s",&userinput1, &userinput2, &userinput3);
        if(strcmp(userinput1,cmdgpio)==0){
            uint GPIO_PIN_CHOICE;
            try{
                GPIO_PIN_CHOICE=std::stoi(userinput2);
            }
            catch(const std::exception& e){
                std::cerr << e.what() << '\n';
                //printf("FIRST ARGUMENT IS NOT INT! %s\n",userinput1);
            }
            if(strcmp(userinput3,offCond)==0){
                gpio_put(GPIO_PIN_CHOICE,0);
            } else if(strcmp(userinput3,onCond)==0){
                gpio_put(GPIO_PIN_CHOICE,1);
            }
        }*/
        /*strcpy(user_act, "COM_CDP---");
        strcpy(VALUE, "ON");
        printf("USIT %s %s",user_act, VALUE); //USer InTeraction
        /*sleep_ms(500);
        scanf("%s %s %s",&userinput1, &userinput2, &userinput3);
        printf(userinput1);
        if(strcmp(userinput1,cmdgpio)==0){
            uint GPIO_PIN_CHOICE;
            try{
                GPIO_PIN_CHOICE=std::stoi(userinput2);
            }
            catch(const std::exception& e){
                std::cerr << e.what() << '\n';
                //printf("FIRST ARGUMENT IS NOT INT! %s\n",userinput1);
            }
            if(strcmp(userinput3,offCond)==0){
                gpio_put(GPIO_PIN_CHOICE,0);
            } else if(strcmp(userinput3,onCond)==0){
                gpio_put(GPIO_PIN_CHOICE,1);
            }
        }
        strcpy(user_act, "COM_CDP---");
        strcpy(VALUE, "OF");
        printf("USIT %s %s",user_act, VALUE);
        sleep_ms(500);*/
        //scanf("%s %s",&userinput1, &userinput2);
        /*uint GPIO_PIN_CHOICE;
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
        }*/
    
    }
}