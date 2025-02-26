#include "pico/stdlib.h"
#include <stdio.h>
#include <iostream>
#include <string.h>
#include <tusb.h>
#include "hardware/pwm.h"

// Définition des broches
#define PWM_PIN 15   // Broche PWM pour la vitesse du moteur
#define DIR_PIN 14   // Broche de direction du moteur

// Fonction pour configurer le PWM
uint slice_num;

void setup_pwm() {
    gpio_set_function(PWM_PIN, GPIO_FUNC_PWM);  // Configure la broche en mode PWM
    slice_num = pwm_gpio_to_slice_num(PWM_PIN); // Obtient le slice du PWM
    pwm_set_wrap(slice_num, 65535);             // Définit la résolution du PWM (16 bits)
    pwm_set_clkdiv(slice_num, 1.0f);            // Pas de division d'horloge
    pwm_set_enabled(slice_num, true);           // Active le PWM
}


void set_motor_direction(float speed_kmh) {
    if (speed_kmh >= 0) {
        gpio_put(DIR_PIN, 1);  // avant
    } else {
        gpio_put(DIR_PIN, 0);  // arrière
    }
}

int main() {
    stdio_init_all();  // Initialisation standard
    gpio_init(DIR_PIN);
    gpio_set_dir(DIR_PIN, GPIO_OUT);

    setup_pwm();  // Initialisation du PWM

    gpio_put(DIR_PIN, 1);

    const int MAX_MESSAGE_LENGTH = 50;
    const int MESSAGE_INSTRUCTION_LENGTH = 2;

    float DUTY_DIV = 1;
    float DUTY_CYCLE = 0;
    float CLKDIV   = 1;
    int PULSE_W    = 500;

    while (true){
        while (tud_cdc_available()) {
            static char message[MAX_MESSAGE_LENGTH];
            static unsigned int message_pos = 0;
            char inByte = getchar();
            if ( inByte != '\n') {
                message[message_pos] = inByte;
                message_pos++;
            } else {
                message[message_pos] = '\0';
                std::string COMMAND = message;
                std::string INSTRUCTION(COMMAND.substr(0, MESSAGE_INSTRUCTION_LENGTH));
    
                if (INSTRUCTION.compare("DC")==0){
                    std::string DutyCycle(COMMAND.substr(MESSAGE_INSTRUCTION_LENGTH, 5)); //XXX.FF
                    int DutyCycleMotor = std::stof(DutyCycle);
                    DUTY_CYCLE = DutyCycleMotor;
                }
                if (INSTRUCTION.compare("DD")==0){
                    std::string DutyCycleDiviser(COMMAND.substr(MESSAGE_INSTRUCTION_LENGTH, 6)); //XXX.FF
                    float DutyCycleDiviserGlobal = std::stof(DutyCycleDiviser);
                    DUTY_DIV = DutyCycleDiviserGlobal;
                }
                if (INSTRUCTION.compare("CK")==0){
                    std::string Clkdiv(COMMAND.substr(MESSAGE_INSTRUCTION_LENGTH, 6)); //XXX.FF
                    float ClockDiviser = std::stof(Clkdiv);
                    CLKDIV = ClockDiviser;
                }
                if (INSTRUCTION.compare("PW")==0){
                    std::string PulseWitdh(COMMAND.substr(MESSAGE_INSTRUCTION_LENGTH, 6));
                    int PulseWitdhFreq = std::stoi(PulseWitdh);
                    PULSE_W = PulseWitdhFreq;
                }
                message_pos = 0;
            }
        }

        pwm_set_clkdiv(slice_num, CLKDIV);
        pwm_set_wrap(slice_num, (125000000 / PULSE_W)); 
        uint16_t duty_cycle = 65535 * (DUTY_CYCLE/100.0f);
        pwm_set_gpio_level(PWM_PIN, duty_cycle/DUTY_DIV);
    }
}