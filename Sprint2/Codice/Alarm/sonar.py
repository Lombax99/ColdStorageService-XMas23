import time
import math

num = 0

file = open('sonar.txt', 'w')

while True:

    outnumber =  50 * (math.cos(0.01 * math.pi * num) + 1)

    print(str(outnumber))
    file.write(str(outnumber)+"\n")

    num = (num + 10) % 200
    time.sleep(1) #sleep one second

file.close()
