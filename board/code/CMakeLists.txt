add_executable(code openMotor.cpp)

target_link_libraries(code pico_stdlib hardware_i2c hardware_gpio hardware_adc hardware_pwm)

pico_enable_stdio_usb(code 1)
pico_enable_stdio_uart(code 0)


pico_add_extra_outputs(code)