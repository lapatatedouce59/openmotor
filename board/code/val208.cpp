#include "pico/stdlib.h"
#include <stdio.h>
#include "hardware/pwm.h"

// Définition des broches
#define PWM_PIN 15   // Broche PWM pour la vitesse du moteur
#define DIR_PIN 14   // Broche de direction du moteur

// Fréquences PWM pour les deux phases
#define INITIAL_PWM_FREQ 500
#define START_PWM_FREQ 2000
#define FINAL_PWM_FREQ 2000

// Fonction pour configurer le PWM
uint slice_num;

void setup_pwm() {
    gpio_set_function(PWM_PIN, GPIO_FUNC_PWM);  // Configure la broche en mode PWM
    slice_num = pwm_gpio_to_slice_num(PWM_PIN); // Obtient le slice du PWM
    pwm_set_wrap(slice_num, 65535);             // Définit la résolution du PWM (16 bits)
    pwm_set_clkdiv(slice_num, 1.0f);            // Pas de division d'horloge
    pwm_set_enabled(slice_num, true);           // Active le PWM
}

// Fonction pour gérer la direction et l'inversion du signal du pont en H
void set_motor_direction(float speed_kmh) {
    // Inverser la direction en fonction du sens de la vitesse
    if (speed_kmh >= 0) {
        gpio_put(DIR_PIN, 1);  // Direction avant
    } else {
        gpio_put(DIR_PIN, 0);  // Direction arrière
    }
}

// Fonction pour simuler une vitesse
void simulate_speed(float speed_kmh) {

    set_motor_direction(speed_kmh);
    float duty_percent = speed_kmh / 80.0f;
    float duty_percent_start = speed_kmh / 5.0f;
    uint16_t duty_cycle = 65535 * duty_percent;

    pwm_set_gpio_level(PWM_PIN, (duty_cycle/1));

    printf("Vitesse actuelle : %.2f km/h\n", speed_kmh);

    //PHASE 1
    if (speed_kmh < 5) {
        pwm_set_clkdiv(slice_num, 50.0f);
        pwm_set_wrap(slice_num, (125000000 / 2000)); 
    }

    //PHASE 2
    if (speed_kmh <= 20 && speed_kmh > 5) {
        pwm_set_clkdiv(slice_num, 1.0f);
        pwm_set_wrap(slice_num, (125000000 / (START_PWM_FREQ))); 
    }

    // Phase 3
    if (speed_kmh > 20 && speed_kmh <= 80) {
        pwm_set_clkdiv(slice_num, 1.0f);
        float new_freq = INITIAL_PWM_FREQ + duty_percent * (FINAL_PWM_FREQ - INITIAL_PWM_FREQ);  // Augmentation linéaire
        pwm_set_wrap(slice_num, (125000000 / (new_freq)));  // Mettre à jour la fréquence
    }
}

int main() {
    stdio_init_all();  // Initialisation standard
    gpio_init(DIR_PIN);
    gpio_set_dir(DIR_PIN, GPIO_OUT);

    setup_pwm();  // Initialisation du PWM
    gpio_put(DIR_PIN, 1);  // Définir la direction du moteur

    for (float speed_kmh = 1; speed_kmh <= 5; speed_kmh += 0.25f) {
        simulate_speed(speed_kmh);
        sleep_ms(30);
    }

    simulate_speed(5);

    sleep_ms(200);

    // Simuler une vitesse de 0 à 80 km/h
    for (float speed_kmh = 5; speed_kmh <= 80; speed_kmh += 0.1f) {
        simulate_speed(speed_kmh);
        sleep_ms(30);
    }

    simulate_speed(0);
    sleep_ms(100);

    // Simuler une vitesse de 0 à 80 km/h
    for (float speed_kmh = 80; speed_kmh > 0; speed_kmh -= 0.1f) {
        simulate_speed(speed_kmh);
        sleep_ms(30);
    }

    while (1);  // Boucle infinie pour garder le programme actif
}