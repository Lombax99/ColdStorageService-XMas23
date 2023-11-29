import RPi.GPIO as GPIO

LED_PIN = 21

GPIO.setmode(GPIO.BCM)
GPIO.setup(LED_PIN,GPIO.OUT)
GPIO.setwarnings(False)

GPIO.output(LED_PIN, GPIO.LOW)

